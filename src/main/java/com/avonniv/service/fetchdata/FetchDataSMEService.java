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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;


@Service
public class FetchDataSMEService {

    private final PublisherService publisherService;

    private final GrantService grantService;

    private final GrantProgramService grantProgramService;

    public FetchDataSMEService(PublisherService publisherService,
                               GrantProgramService grantProgramService,
                               GrantService grantService) {
        this.publisherService = publisherService;
        this.grantService = grantService;
        this.grantProgramService = grantProgramService;
    }

    private static String getURLTopicAPI(String frameworkProgramme) {
        return "http://ec.europa.eu/research/participants/portal/data/call/" + frameworkProgramme.toLowerCase() + "/" + frameworkProgramme.toLowerCase() + "_topics.json";
    }

    private static String getURLCallAPI(String frameworkProgramme) {
        return "http://ec.europa.eu/research/participants/portal/data/call/" + frameworkProgramme.toLowerCase() + "/calls.json";
    }

    private static String getURLTopic(String frameworkProgramme, String topicFileName) {
        return "http://ec.europa.eu/research/participants/portal/desktop/en/opportunities/" + frameworkProgramme.toLowerCase() + "/topics/" + topicFileName + ".html";
    }

    private static String getURLCall(String frameworkProgramme, String topicFileName) {
        return "http://ec.europa.eu/research/participants/portal/desktop/en/opportunities/" + frameworkProgramme.toLowerCase() + "/calls/" + topicFileName + ".html";
    }

    private static String getURLDescriptionTopic(String topicFileName) {
        return "http://ec.europa.eu/research/participants/portal/data/call/topics/" + topicFileName + ".json";
    }

    //43200000 millisecond = 12 hour
    @Scheduled(fixedDelay = 21600000, initialDelay = 1000)
    public void autoFetchDataFromSME() {
        try {
            Instant now = Instant.now();
            String name = "SME instrument";
            Optional<Publisher> publisherOptional = publisherService.getById(9L);
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            List<URLFetch> listURLFetch = getListURLFetch();
            for (URLFetch urlFetch : listURLFetch) {
                String json = Util.readUrl(getURLTopicAPI(urlFetch.getFrameworkProgramme()));
                String URLCallAPI = getURLCallAPI(urlFetch.getFrameworkProgramme());
                List<CallData> listCallData = getListCallData(URLCallAPI);
                JSONObject object = new JSONObject(json);
                if (object.has("topicData")) {
                    object = object.getJSONObject("topicData");
                    if (object.has("Topics")) {
                        JSONArray jsonArray = object.getJSONArray("Topics");
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jsonCall = jsonArray.getJSONObject(i);
                            if (jsonCall.has("callFileName")) {
                                String callFileName = jsonCall.getString("callFileName");
                                String externalIdGrantProgram = name + "_" + callFileName;
                                Optional<GrantProgram> optional = grantProgramService.getByExternalId(externalIdGrantProgram);
                                GrantProgram grantProgram;
                                if (optional.isPresent()) {
                                    grantProgram = optional.get();
                                } else {
                                    GrantProgramDTO grantProgramDTO = new GrantProgramDTO(
                                        null, null, null, 0,
                                        Util.readStringJSONObject(jsonCall, "callTitle"),
                                        null,
                                        GrantProgramDTO.TYPE.PUBLIC.getValue(),
                                        publisherDTO,
                                        null,
                                        externalIdGrantProgram,
                                        getURLCall(urlFetch.getFrameworkProgramme(), callFileName),
                                        readDateJSONObject(jsonCall, "publicationDate")
                                    );
                                    grantProgram = grantProgramService.createGrant(grantProgramDTO);
                                }
                                GrantProgramDTO grantProgramDTO = new GrantProgramDTO(grantProgram);

                                String topicFileName = Util.readStringJSONObject(jsonCall, "topicFileName");
                                String subCallId = Util.readStringJSONObject(jsonCall, "subCallId");
                                String externalIdGrant = name + "_" + topicFileName + "_" + subCallId;
                                Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
                                GrantDTO grantDTO = grantOptional.map(GrantDTO::new).orElseGet(GrantDTO::new);

                                grantDTO.setGrantProgramDTO(grantProgramDTO);
                                grantDTO.setTitle(Util.readStringJSONObject(jsonCall, "title"));
                                grantDTO.setDescription(getDescription(topicFileName));
                                grantDTO.setOpenDate(readDateJSONObject(jsonCall, "plannedOpeningDate"));
                                grantDTO.setCloseDate(readDateJSONObject(jsonCall, "deadlineDates"));
                                grantDTO.setExternalId(externalIdGrant);
                                grantDTO.setExternalUrl(getURLTopic(urlFetch.getFrameworkProgramme(), topicFileName));
                                grantDTO.setDataFromUrl(URLCallAPI);
                                Util.setStatus(grantDTO, now);
                                List<String> topics = new ArrayList<>();
                                try {
                                    String identifier = Util.readStringJSONObject(jsonCall, "identifier");
                                    JSONArray topic = jsonCall.getJSONArray("actions").getJSONObject(0).getJSONArray("types");
                                    for (int k = 0; k < topic.length(); k++) {
                                        topics.add(identifier + " - " + topic.getString(k));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String amount = null;
                                List<CallData> listCallDataFor = listCallData.stream().filter(callData -> callData.getCallFileName().equalsIgnoreCase(callFileName)).collect(Collectors.toList());
                                for (int j = 0; j < listCallDataFor.size(); j++) {
                                    CallData callData = listCallDataFor.get(j);
                                    if (callData.getCallFileName().equals(callFileName)) {
                                        boolean checkTopic = false;
                                        for (String topic : topics) {
                                            if (callData.getTopics().contains(topic)) {
                                                checkTopic = true;
                                                break;
                                            }
                                        }
                                        if (checkTopic) {
                                            if (callData.getOpenDate().equals(grantDTO.getOpenDate())) {
                                                Instant deadlineDate = readDateJSONObject(jsonCall, "deadlineDates");
                                                for (Instant instant : callData.getCloseDate()) {
                                                    if (instant.equals(deadlineDate)) {
                                                        grantDTO.setFinanceDescription(callData.getAmount());
                                                        if (amount == null) {
                                                            amount = callData.getAmount();
                                                        } else {
                                                            amount = (Integer.parseInt(amount) + Integer.parseInt(callData.getAmount())) + "";
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                grantDTO.setFinanceDescription(amount);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<URLFetch> getListURLFetch() {
        List<URLFetch> list = new ArrayList<>();
        list.add(new URLFetch("Horizon 2020", "H2020"));
        list.add(new URLFetch("Research Fund for Coal & Steel", "RFCS"));
        list.add(new URLFetch("3rd Health Programme", "3HP"));
        list.add(new URLFetch("Promotion of Agricultural Products", "AGRIP"));
        list.add(new URLFetch("Consumer Programme", "CP"));
        list.add(new URLFetch("COSME", "COSME"));
        list.add(new URLFetch("Justice Programme", "JUST"));
        list.add(new URLFetch("Asylum, Migration and Integration Fund", "AMIF"));
        list.add(new URLFetch("Internal Security Fund – Borders", "ISFB"));
        list.add(new URLFetch("Internal Security Fund - Police", "ISFP"));
        list.add(new URLFetch("Rights, Equality and Citizenship Programme", "REC"));
        list.add(new URLFetch("Union Civil Protection Mechanism", "UCPM"));
        return list;
    }


    private static String getDescription(String topicFileName) {
        try {
            String url = getURLDescriptionTopic(topicFileName);
            String json = Util.readUrl(url);
            JSONObject object = new JSONObject(json);
            if (object.has("description")) {
                return object.getString("description");
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Instant readDateJSONObject(JSONObject object, String key) {
        List<Instant> listDate = readListDateJSONObject(object, key);
        if (listDate.isEmpty()) {
            return null;
        }
        return listDate.get(listDate.size() - 1);
    }

    public List<CallData> getListCallData(String url) {
        try {
            String json = Util.readUrl(url);
            net.sf.json.JSONObject jsonObjectSF = net.sf.json.JSONObject.fromObject(json);
            JSONObject jsonObject = new JSONObject(jsonObjectSF.toString());
            JSONArray jsonArray = jsonObject.getJSONObject("callData").getJSONArray("Calls");
            List<CallData> listCallData = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCall = jsonArray.getJSONObject(i);
                String callFileName = jsonCall.getJSONObject("CallIdentifier").getString("FileName");
                JSONArray jsonCallBudgetOverviewArray = jsonCall.getJSONArray("CallBudgetOverview");
                try {
                    JSONObject jsonCallBudgetOverview = jsonCallBudgetOverviewArray.getJSONObject(0);
                    for (int j = 0; j < jsonCallBudgetOverview.names().length(); j++) {
                        JSONObject jsonBudgetObject = jsonCallBudgetOverview.getJSONObject(jsonCallBudgetOverview.names().getString(j));
                        CallData callData = getCallData(callFileName, jsonBudgetObject);
                        listCallData.add(callData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    JSONObject jsonBudgetObject = jsonCallBudgetOverviewArray.getJSONObject(0).getJSONObject("Budget");
//                    CallData callData = getCallData(callFileName, jsonBudgetObject);
//                    listCallData.add(callData);
                }

            }
            return listCallData;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private CallData getCallData(String callFileName, JSONObject jsonBudgetObject) throws ParseException, JSONException {
        CallData callData = new CallData();
        callData.setCallFileName(callFileName);
        if (jsonBudgetObject.has("Opening date")) {
            callData.setOpenDate(getInstantByString(jsonBudgetObject.getString("Opening date")));
        }
        JSONArray topic = jsonBudgetObject.getJSONArray("Topic");
        List<String> topics = new ArrayList<>();
        for (int i = 0; i < topic.length(); i++) {
            topics.add(topic.getString(i));
        }
        callData.setTopics(topics);
        callData.setCloseDate(readListDateJSONObject(jsonBudgetObject, "Deadline"));
        if (jsonBudgetObject.has("BudgetYearAmount")) {
            try {
                JSONArray jsonArray = jsonBudgetObject.getJSONArray("BudgetYearAmount");
                int amount = 0;
                for (int i = 1; i < jsonArray.length(); i = i + 2) {
                    amount = amount + Integer.parseInt(jsonArray.getString(i).replaceAll(",", ""));
                }
                if (jsonArray.length() % 2 == 0) {
                    callData.setAmount(amount + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return callData;
    }

    private static List<Instant> readListDateJSONObject(JSONObject object, String key) {
        List<Instant> listDate = new ArrayList<>();
        if (object.has(key)) {
            try {
                try {
                    JSONArray jsonArray = object.getJSONArray(key);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Instant instant = getInstantByString(jsonArray.getString(i));
                        listDate.add(instant);
                    }
                } catch (Exception e) {
                    Instant instant = getInstantByString(object.getString(key));
                    listDate.add(instant);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listDate;
    }

    private static Instant getInstantByString(String dateString) throws ParseException, JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(dateString).toInstant();
    }

    class URLFetch {
        private String title;
        private String frameworkProgramme;

        URLFetch(String title, String frameworkProgramme) {
            this.title = title;
            this.frameworkProgramme = frameworkProgramme;
        }

        public String getTitle() {
            return title;
        }

        public String getFrameworkProgramme() {
            return frameworkProgramme;
        }
    }

    class CallData {
        private String callFileName;
        private Instant openDate;
        private List<String> topics;
        private List<Instant> closeDate = new ArrayList<>();
        private String amount;

        public String getCallFileName() {
            return callFileName;
        }

        public void setCallFileName(String callFileName) {
            this.callFileName = callFileName;
        }

        public Instant getOpenDate() {
            return openDate;
        }

        public void setOpenDate(Instant openDate) {
            this.openDate = openDate;
        }

        public List<Instant> getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(List<Instant> closeDate) {
            this.closeDate = closeDate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }
    }

}
