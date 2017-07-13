package com.avonniv.service.fetchdata;

import com.avonniv.domain.CrawlHistory;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.service.CrawHistoryService;
import com.avonniv.service.GrantProgramService;
import com.avonniv.service.GrantService;
import com.avonniv.service.PublisherService;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.service.dto.PublisherDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
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

    //43200000 millisecond = 12 hour
    @Scheduled(fixedDelay = 43200000)
    public void autoFetchDataFromVinnova() {
        try {
            String name = "Vinnova";
            Optional<Publisher> publisherOptional = publisherService.getPublisherByName(name);
            Optional<CrawlHistory> crawlHistoryOptional = crawHistoryService.findByName(name);
            //1483228800L = Sunday, January 1, 2017 12:00:00 AM
            Instant lastDateCrawl = Instant.ofEpochSecond(1483228800L);
            if (crawlHistoryOptional.isPresent()) {
                lastDateCrawl = crawlHistoryOptional.get().getLastDateCrawl();
            }
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            while (true) {
                String url = getURLString(lastDateCrawl);
                String json = readUrl(url);
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); ++i) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String externalIdGrantProgram = name + "_" + readStringJSONObject(jsonObject, "Diarienummer");
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
                        grantProgramDTO.setExternalUrl(getURLString(grantProgramDTO.getName()));
                        grantProgram = grantProgramService.createGrant(grantProgramDTO);
                    }

                    if (jsonObject.has("AnsokningsomgangLista")) {
                        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
                        JSONArray AnsokningsomgangLista = jsonObject.getJSONArray("AnsokningsomgangLista");
                        List<DataFetch> listURL = getURLGrant(grantProgramDTO.getExternalUrl());
                        for (int j = 0; j < AnsokningsomgangLista.length(); j++) {
                            JSONObject object = AnsokningsomgangLista.getJSONObject(j);
                            String externalIdGrant = name + "_" + readStringJSONObject(object, "Diarienummer");
                            Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
                            if (!grantOptional.isPresent()) {
                                GrantDTO grantDTO = new GrantDTO(
                                    null, null, null, 0,
                                    grantProgramDTO,
                                    readStringJSONObject(object, "Titel"),
                                    null,
                                    readStringJSONObject(object, "Beskrivning"),
                                    readDateJSONObject(object, "Oppningsdatum"),
                                    readDateJSONObject(object, "Stangningsdatum"),
                                    readDateJSONObject(object, "UppskattatBeslutsdatum"),
                                    readDateJSONObject(object, "TidigastProjektstart"),
                                    externalIdGrant,
                                    null,
                                    null
                                );
                                for (int k = 0; k < listURL.size(); k++) {
                                    DataFetch dataFetch = listURL.get(k);
                                    if (dataFetch.getTitle().equals(grantDTO.getTitle())) {
                                        grantDTO.setFinanceDescription(dataFetch.getFinanceDescription());
                                        grantDTO.setExternalUrl(dataFetch.getExternalUrl());
                                        listURL.remove(k);
                                        break;
                                    }
                                }
                                grantService.createGrantCall(grantDTO);
                            }
                        }
                    }
                }
                if (Instant.now().toEpochMilli() < lastDateCrawl.toEpochMilli()) {
                    break;
                }
                crawHistoryService.update(name, lastDateCrawl);
                lastDateCrawl = Instant.ofEpochSecond(lastDateCrawl.plus(1, ChronoUnit.DAYS).getEpochSecond());
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

    private List<DataFetch> getURLGrant(String url) {
        try {
            if (url == null) {
                return new ArrayList<>();
            }
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select("#area_main .module-utlysning-call-collection .clearfix");
            List<DataFetch> listURL = new ArrayList<>();
            for (Element element : elements) {
                DataFetch dataFetch = new DataFetch();
                dataFetch.setExternalUrl("https://www.vinnova.se" + (element.select(".medium-15 .element-link--arrow-after").attr("href")));
                getDataFromURL(dataFetch);
                listURL.add(dataFetch);
            }
            return listURL;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void getDataFromURL(DataFetch dataFetch) {
        try {
            Document doc = Jsoup.connect(dataFetch.getExternalUrl())
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36")
                .timeout(30000)
                .get();
            Elements elements = doc.select("#area_main .module-utlysning-three-col .row .items .item");
            if (elements.size() != 0) {
                elements = elements.last().select(".text p");
                if (elements.size() != 0) {
                    dataFetch.setFinanceDescription(elements.first().toString());
                }
            }
            elements = doc.select("#area_main .navigation__container .current a span");
            if (elements.size() != 0) {
                dataFetch.setTitle(elements.first().text());
            }
        } catch (Exception ignored) {
        }
    }

    public String getURLString(Instant lastDateCrawl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastDateCrawl.getEpochSecond() * 1000);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return "http://data.vinnova.se/api/v1/utlysningar/" + year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    public String getURLString(String title) {
        try {
            String link = "https://www.vinnova.se/e/" + removeAccent(title).replaceAll(" ", "-");
            URL url = new URL(link);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            if (HttpURLConnection.HTTP_OK == urlConn.getResponseCode()) {
                return link;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String removeAccent(String txt) {
        txt = txt.toLowerCase();
        String temp = Normalizer.normalize(txt, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        txt = pattern.matcher(temp).replaceAll("");
        txt = txt.replaceAll("[^\\p{ASCII}]", "");
        return txt;
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
                if (date.toCharArray()[date.length() - 1] != 'Z') {
                    date = date + "Z";
                }
                Instant instant = Instant.parse(date);
                if (instant.getEpochSecond() < 0) {
                    return null;
                } else {
                    return instant;
                }
            } catch (Exception e) {
            }
        }
        return null;

    }

    class DataFetch {
        private String title;
        private String externalUrl;
        private String financeDescription;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getExternalUrl() {
            return externalUrl;
        }

        public void setExternalUrl(String externalUrl) {
            this.externalUrl = externalUrl;
        }

        public String getFinanceDescription() {
            return financeDescription;
        }

        public void setFinanceDescription(String financeDescription) {
            this.financeDescription = financeDescription;
        }
    }
}
