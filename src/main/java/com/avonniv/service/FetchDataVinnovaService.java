package com.avonniv.service;

import com.avonniv.domain.CrawlHistory;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.service.dto.PublisherDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


@Service
@Transactional
public class FetchDataVinnovaService {

    private final PublisherService publisherService;

    private final GrantService grantService;

    private final GrantProgramService grantProgramService;

    private final CrawHistoryService crawHistoryService;

    public FetchDataVinnovaService(PublisherService publisherService,
                                   GrantProgramService grantProgramService,
                                   CrawHistoryService crawHistoryService,
                                   GrantService grantService) {
        this.publisherService = publisherService;
        this.grantService = grantService;
        this.crawHistoryService = crawHistoryService;
        this.grantProgramService = grantProgramService;
    }

    public String getURLString(LocalDateTime localDateTime) {
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue() + 1;
        int day = localDateTime.getDayOfMonth();
        return "http://data.vinnova.se/api/v1/utlysningar/" + year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    //43200000 millisecond = 12 hour
    @Scheduled(fixedDelay = 43200000)
    public void autoFetchDataFromVinnova() {
        try {
            String name = "Vinnova";
            Optional<Publisher> publisherOptional = publisherService.getById(1L);
            Optional<CrawlHistory> crawlHistoryOptional = crawHistoryService.findByName(name);
            //1483228800L = Sunday, January 1, 2017 12:00:00 AM
            Instant lastDateCrawl = Instant.ofEpochSecond(1483228800L);
            if (crawlHistoryOptional.isPresent()) {
                lastDateCrawl = crawlHistoryOptional.get().getLastDateCrawl();
            }
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            while (true) {
                LocalDateTime localDateTime = LocalDateTime.ofInstant(lastDateCrawl, ZoneOffset.UTC);

                String json = readUrl(getURLString(localDateTime));
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); ++i) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String externalIdGrantProgram = readStringJSONObject(jsonObject, "Diarienummer");
                    Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
                    GrantProgram grantProgram;
                    if (optional.isPresent()) {
                        grantProgram = optional.get();
                    } else {
                        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                            null, null, null, 0,
                            readStringJSONObject(jsonObject, "Titel"),
                            readStringJSONObject(jsonObject, "Beskrivning"),
                            GrantProgramDTO.TYPE.PUBLIC.getValue(),
                            publisherDTO,
                            null,
                            externalIdGrantProgram,
                            null,
                            readDateJSONObject(jsonObject, "Publiceringsdatum")
                        );
                        grantProgram = grantProgramService.createGrant(grantProgramDTO);
                    }

                    if (jsonObject.has("AnsokningsomgangLista")) {
                        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
                        JSONArray AnsokningsomgangLista = jsonObject.getJSONArray("AnsokningsomgangLista");
                        for (int j = 0; j < AnsokningsomgangLista.length(); j++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String externalIdGrant = readStringJSONObject(object, "Diarienummer");
                            Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
                            if (!grantOptional.isPresent()) {
                                GrantDTO grantDTO = new GrantDTO(
                                    null, null, null, 0,
                                    grantProgramDTO,
                                    readStringJSONObject(object, "Titel"),
                                    null,
                                    readStringJSONObject(object, "Beskrivning"),
                                    readDateJSONObject(jsonObject, "Oppningsdatum"),
                                    readDateJSONObject(jsonObject, "Stangningsdatum"),
                                    readDateJSONObject(jsonObject, "UppskattatBeslutsdatum"),
                                    readDateJSONObject(jsonObject, "TidigastProjektstart"),
                                    externalIdGrant,
                                    null,
                                    null
                                );
                                grantService.createGrantCall(grantDTO);
                            }
                        }
                    }
                }
                crawHistoryService.update(name, lastDateCrawl.minus(1, ChronoUnit.DAYS));
                if (Instant.now().toEpochMilli() < lastDateCrawl.toEpochMilli()) {
                    break;
                }
                lastDateCrawl = lastDateCrawl.plus(1, ChronoUnit.DAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private static String readStringJSONObject(JSONObject object, String key) {
        if (object.has(key)) {
            try {
                return object.getString(key);
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static Instant readDateJSONObject(JSONObject object, String key) {
        if (object.has(key)) {
            try {
                String date = object.getString(key);
                return Instant.parse(date);
            } catch (Exception e) {
            }
        }
        return null;

    }
}
