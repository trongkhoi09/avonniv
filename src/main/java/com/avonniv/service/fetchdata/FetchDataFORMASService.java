package com.avonniv.service.fetchdata;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.service.GrantProgramService;
import com.avonniv.service.GrantService;
import com.avonniv.service.PublisherService;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.service.dto.PublisherDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FetchDataFORMASService {

    private final PublisherService publisherService;

    private final GrantService grantService;

    private final GrantProgramService grantProgramService;

    public FetchDataFORMASService(PublisherService publisherService,
                                  GrantProgramService grantProgramService,
                                  GrantService grantService) {
        this.publisherService = publisherService;
        this.grantService = grantService;
        this.grantProgramService = grantProgramService;
    }

    //43200000 millisecond = 12 hour
    @Scheduled(fixedDelay = 43200000, initialDelay = 2000)
    public void autoFetchDataFromFormas() {
        try {
            String name = "Formas";
            Optional<Publisher> publisherOptional = publisherService.getPublisherByName(name);
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            String externalUrl = "http://formas.se/sv/Finansiering/Utlysningar/";
            String externalIdGrantProgram = name + "_" + externalUrl;
            Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
            GrantProgram grantProgram;
            if (optional.isPresent()) {
                grantProgram = optional.get();
            } else {
                GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                    null, null, null, 0,
                    "Formas grant program",
                    null,
                    GrantProgramDTO.TYPE.PUBLIC.getValue(),
                    publisherDTO,
                    null,
                    externalIdGrantProgram,
                    externalUrl,
                    null
                );
                grantProgram = grantProgramService.createGrant(grantProgramDTO);
            }
            GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
            List<URLFetch> listURLFetch = getListURLFetch();
            for (URLFetch urlFetch : listURLFetch) {
                try {
                    String json = Util.readUrl(urlFetch.getLink());
                    JSONObject xmlJSONObj = XML.toJSONObject(json);
                    String jsonPrettyPrintString = xmlJSONObj.toString(4);
                    JSONObject jsonObject = new JSONObject(jsonPrettyPrintString).getJSONObject("rss").getJSONObject("channel");
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("item");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                            saveGrant(name, grantProgramDTO, jsonObjectItem);
                        }
                    }catch (JSONException e){
                        JSONObject jsonObjectItem = jsonObject.getJSONObject("item");
                        saveGrant(name, grantProgramDTO, jsonObjectItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGrant(String name, GrantProgramDTO grantProgramDTO, JSONObject jsonObjectItem) throws ParseException, JSONException {
        String link = Util.readStringJSONObject(jsonObjectItem, "link");
        String externalIdGrant = name + "_" + link;
        Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
        if (!grantOptional.isPresent()) {
            Instant pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(jsonObjectItem.getString("pubDate")).toInstant();
            GrantDTO grantDTO = new GrantDTO(
                null, null, null, 0,
                grantProgramDTO,
                Util.readStringJSONObject(jsonObjectItem, "title"),
                null,
                Util.readStringJSONObject(jsonObjectItem, "description"),
                pubDate,
                null,
                null,
                null,
                externalIdGrant,
                link,
                null
            );
            getCloseDateAndAnnouncedDate(link, grantDTO);
            grantService.createGrantCall(grantDTO);
        }
    }

    private void getCloseDateAndAnnouncedDate(String url, GrantDTO grantDTO) {
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select("#body-wrapper #main .last-date");
            if (elements.size() == 1) {
                String data = elements.first().text();
                if (data.length() > 10) {
                    grantDTO.setCloseDate(new SimpleDateFormat("yyyy-MM-dd").parse(data.substring(data.length() - 10, data.length())).toInstant());
                }
            }
            elements = doc.select("#body-wrapper #main .decision-date");
            if (elements.size() == 1) {
                String data = elements.first().text();
                if (data.length() > 10) {
                    grantDTO.setAnnouncedDate(new SimpleDateFormat("yyyy-MM-dd").parse(data.substring(data.length() - 10, data.length())).toInstant());
                }
            }
        } catch (Exception e) {
        }
    }

    public List<URLFetch> getListURLFetch() {
        List<URLFetch> list = new ArrayList<>();
        list.add(new URLFetch("Aktuella utlysningar", "http://formas.se/RSS/RSS-Aktuella-utlysningar/"));
        list.add(new URLFetch("Kommande utlysningar", "http://formas.se/RSS/RSS-Kommande-utlysningar/"));
        list.add(new URLFetch("Beslutade utlysningar", "http://formas.se/RSS/RSS-Beslutade-utlysningar/"));
        return list;
    }

    class URLFetch {
        private String title;
        private String link;

        URLFetch(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }

}
