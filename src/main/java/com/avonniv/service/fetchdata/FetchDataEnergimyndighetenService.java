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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Optional;
import java.util.TimeZone;


@Service
public class FetchDataEnergimyndighetenService {

    private final String URL_FETCH_DATA = "http://www.energimyndigheten.se/rss-utlysningar-fran-energimyndigheten/";

    private final PublisherService publisherService;

    private final GrantService grantService;

    private final GrantProgramService grantProgramService;

    public FetchDataEnergimyndighetenService(PublisherService publisherService,
                                             GrantProgramService grantProgramService,
                                             GrantService grantService) {
        this.publisherService = publisherService;
        this.grantService = grantService;
        this.grantProgramService = grantProgramService;
    }

    //21600000 millisecond = 6 hour
    //run taks every 00h and 12h AM everyday
    @Scheduled(cron = "0 0 1,13 * * ?", zone = "CET")
    public void autoFetchDataFromFormas() {
        try {
            String name = "Energimyndigheten";
            Optional<Publisher> publisherOptional = publisherService.getById(13L);
            PublisherDTO publisherDTO;
            if (publisherOptional.isPresent()) {
                publisherDTO = new PublisherDTO(publisherOptional.get());
            } else {
                publisherDTO = new PublisherDTO(
                    null,
                    null,
                    null,
                    0,
                    "Energimyndigheten",
                    "Energimyndigheten arbetar för ett hållbart energisystem, som förenar ekologisk hållbarhet, konkurrenskraft och försörjningstrygghet.",
                    null,
                    "Kungsgatan 43, Eskilstuna",
                    "registrator@energimyndigheten.se",
                    "016-544 20 00",
                    "http://www.energimyndigheten.se/"
                );
                publisherDTO.setCrawled(true);
                publisherService.createPublisher(publisherDTO);
            }
            String externalUrl = "http://www.energimyndigheten.se/utlysningar/";
            String externalIdGrantProgram = name + "_" + externalUrl;
            Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
            GrantProgram grantProgram;
            if (optional.isPresent()) {
                grantProgram = optional.get();
            } else {
                GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                    null, null, null, 0,
                    "Energimyndigheten grant program",
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
            try {
                String json = Util.readUrl(URL_FETCH_DATA);
                JSONObject xmlJSONObj = XML.toJSONObject(json);
                String jsonPrettyPrintString = xmlJSONObj.toString(4);
                JSONObject jsonObject = new JSONObject(jsonPrettyPrintString).getJSONObject("rss").getJSONObject("channel");
                Instant now = Instant.now();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("item");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                        saveGrant(name, grantProgramDTO, jsonObjectItem, now);
                    }
                } catch (JSONException e) {
                    JSONObject jsonObjectItem = jsonObject.getJSONObject("item");
                    saveGrant(name, grantProgramDTO, jsonObjectItem, now);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGrant(String name, GrantProgramDTO grantProgramDTO, JSONObject jsonObjectItem, Instant now) throws ParseException, JSONException {
        String link = Util.readStringJSONObject(jsonObjectItem, "link");
        String externalIdGrant = name + "_" + link;
        Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
        GrantDTO grantDTO = grantOptional.map(GrantDTO::new).orElseGet(GrantDTO::new);

        grantDTO.setGrantProgramDTO(grantProgramDTO);
        grantDTO.setTitle(Util.readStringJSONObject(jsonObjectItem, "title"));
        grantDTO.setDescription(Util.readStringJSONObject(jsonObjectItem, "description"));
        grantDTO.setExternalId(externalIdGrant);
        grantDTO.setExternalUrl(link);
        grantDTO.setDataFromUrl(URL_FETCH_DATA);

        getDataFromURL(link, now, grantDTO);
        if (!grantOptional.isPresent()) {
            grantService.createGrantCall(grantDTO);
        } else {
            grantService.update(grantDTO);
        }
    }

    private void getDataFromURL(String link, Instant now, GrantDTO grantDTO) {
        try {
            Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select(".container-fluid #content .MainBody .proclamationInfoTable .proclamationInfo");
            Instant closeDate = null;
            for (Element elementItem : elements) {
                switch (elementItem.select(".left").text().trim()) {
                    case "Sista dag för ansökan":
                        closeDate = getDateFromString(elementItem.select(".right").text().trim());
                        break;
                    case "Utlysta medel":
                        grantDTO.setFinanceDescription(elementItem.select(".right").text().trim());
                        break;
                }
            }
            grantDTO.setCloseDate(closeDate, 2);
            if (closeDate != null && closeDate.getEpochSecond() < now.getEpochSecond()) {
                grantDTO.setStatus(GrantDTO.Status.close.getValue());
            } else {
                grantDTO.setStatus(GrantDTO.Status.open.getValue());
            }
        } catch (Exception ignored) {
        }
    }

    private Instant getDateFromString(String data) {
        try {
            if (data.length() == 10) {
                data += " 00:00";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
            return sdf.parse(data).toInstant();
        } catch (ParseException ignored) {
        }
        return null;
    }
}
