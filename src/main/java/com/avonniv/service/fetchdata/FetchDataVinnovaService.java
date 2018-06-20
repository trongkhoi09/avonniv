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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class FetchDataVinnovaService {
    private final Logger log = LoggerFactory.getLogger(FetchDataVinnovaService.class);

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

    public void autoFetchDataFromVinnova() {
        try {
            Instant now = Instant.now();
            String name = "Vinnova";
            Optional<Publisher> publisherOptional = publisherService.getById(1L);
            Optional<CrawlHistory> crawlHistoryOptional = crawHistoryService.findByName(name);
            //1483228800L = Sunday, January 1, 2017 12:00:00 AM
            Instant lastDateCrawl = Instant.ofEpochSecond(1483228800L);
            if (crawlHistoryOptional.isPresent()) {
                lastDateCrawl = crawlHistoryOptional.get().getLastDateCrawl();
            }
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            List<String> listIdCrawled = new ArrayList<>();
            while (true) {
                String url = getURLString(lastDateCrawl);
                String json = Util.readUrl(url);
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String externalIdGrantProgram = name + "_" + Util.readStringJSONObject(jsonObject, "Diarienummer");
                    Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
                    GrantProgram grantProgram;
                    if (optional.isPresent()) {
                        grantProgram = optional.get();
                    } else {
                        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                            null, null, null, 0,
                            Util.readStringJSONObject(jsonObject, "Titel"),
                            Util.readStringJSONObject(jsonObject, "Beskrivning"),
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
                    if (jsonObject.has("AnsokningsomgangDnrLista")) {
                        GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);
                        JSONArray AnsokningsomgangLista = jsonObject.getJSONArray("AnsokningsomgangDnrLista");
                        List<DataFetch> listURL = getURLGrant(grantProgramDTO.getExternalUrl());
                        for (int j = 0; j < AnsokningsomgangLista.length(); j++) {
                            JSONObject diarienummerAnsokningsomgang = AnsokningsomgangLista.getJSONObject(j);
                            String id = Util.readStringJSONObject(diarienummerAnsokningsomgang, "DiarienummerAnsokningsomgang");
                            if (!listIdCrawled.contains(id)) {
                                listIdCrawled.add(id);
                                String urlGrant = "https://data.vinnova.se/api/ansokningsomgangar/" + id;
                                String jsonAnsokningsomgangar = Util.readUrl(urlGrant);
                                JSONArray jsonArrayAnsokningsomgangar = new JSONArray(jsonAnsokningsomgangar);
                                if (jsonArrayAnsokningsomgangar.length() == 1) {
                                    JSONObject object = jsonArrayAnsokningsomgangar.getJSONObject(0);
                                    String externalIdGrant = name + "_" + Util.readStringJSONObject(object, "Diarienummer");
                                    Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
                                    GrantDTO grantDTO = grantOptional.map(GrantDTO::new).orElseGet(GrantDTO::new);
                                    grantDTO.setExternalId(externalIdGrant);
                                    grantDTO.setGrantProgramDTO(grantProgramDTO);
                                    grantDTO.setTitle(Util.readStringJSONObject(object, "Titel"));
                                    grantDTO.setDescription(Util.readStringJSONObject(object, "Beskrivning"));
                                    grantDTO.setOpenDate(readDateJSONObject(object, "Oppningsdatum"));
                                    grantDTO.setCloseDate(readDateJSONObject(object, "Stangningsdatum"));
                                    grantDTO.setAnnouncedDate(readDateJSONObject(object, "UppskattatBeslutsdatum"));
                                    grantDTO.setProjectStartDate(readDateJSONObject(object, "TidigastProjektstart"));
                                    grantDTO.setDataFromUrl(urlGrant);
                                    String month = Util.readStringJSONObject(object, "AnnonseringslägePeriod");
                                    String year = Util.readStringJSONObject(object, "AnnonseringslägeÅr");
                                    if (year != null && !year.equals("null")) {
                                        grantDTO.setStatus(GrantDTO.Status.coming.getValue());
                                        if (grantDTO.getOpenDate() == null) {
                                            grantDTO.setOpenDate(getDateByYearAndMonth(year, month));
                                        }
                                    }
                                    String publik = Util.readStringJSONObject(object, "Publik");
                                    String webbsida = Util.readStringJSONObject(object, "Webbsida");
                                    if ((publik != null && publik.equals("0")) || (webbsida != null && webbsida.equals("0"))) {
                                        grantDTO.setStatus(GrantDTO.Status.un_publish.getValue());
                                    } else {
                                        if (grantDTO.getStatus() == GrantDTO.Status.un_publish.getValue()) {
                                            grantDTO.setStatus(GrantDTO.Status.undefined.getValue());
                                        }
                                        Util.setStatus(grantDTO, now);
                                    }
                                    for (int k = 0; k < listURL.size(); k++) {
                                        DataFetch dataFetch = listURL.get(k);
                                        if (dataFetch.getTitle().trim().equals(grantDTO.getTitle().trim())) {
                                            grantDTO.setFinanceDescription(dataFetch.getFinanceDescription());
                                            grantDTO.setExternalUrl(dataFetch.getExternalUrl());
                                            listURL.remove(k);
                                            break;
                                        }
                                    }
                                    if (grantDTO.getExternalUrl() == null)
                                        grantDTO.setExternalUrl("https://www.vinnova.se/sok-finansiering/hitta-finansiering/");
                                    if (!grantOptional.isPresent()) {
                                        grantService.createGrantCall(grantDTO);
                                    } else {
                                        grantService.update(grantDTO);
                                    }
                                }
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

    private Instant getDateByYearAndMonth(String year, String month) {
        try {
            if (month == null || month.equals("null")) {
                month = "January";
            }
            return new SimpleDateFormat("yyyy MM dd").parse(Util.convertStringMonthToNumber(year + " " + month + " 01")).toInstant();
        } catch (Exception e) {
            return null;
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
                if (dataFetch.getTitle() != null) {
                    listURL.add(dataFetch);
                } else {
                    log.error("Title null", dataFetch.getExternalUrl());
                }
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
            }else {
                elements = doc.select("#area_main .navigation__container .current a p");
                if (elements.size() != 0) {
                    dataFetch.setTitle(elements.first().text());
                }else {
                    System.out.println();
                }
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
        return "https://data.vinnova.se/api/utlysningar/" + year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
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

    private static Instant readDateJSONObject(JSONObject object, String key) {
        if (object.has(key)) {
            try {
                String date = object.getString(key);
                if (date.equals("null")) {
                    return null;
                }
                if (date.toCharArray()[date.length() - 1] != 'Z') {
                    date = date + "Z";
                }
                Instant instant = Instant.parse(date);
                if (instant.getEpochSecond() < 0) {
                    return null;
                } else {
                    return instant;
                }
            } catch (Exception ignored) {
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
