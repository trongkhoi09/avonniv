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
    @Scheduled(fixedDelay = 43200000, initialDelay = 1000)
    public void autoFetchDataFromSME() {
        try {
            Instant now = Instant.now();
            String name = "SME instrument";
            Optional<Publisher> publisherOptional = publisherService.getPublisherByName(name);
            PublisherDTO publisherDTO = publisherOptional.map(PublisherDTO::new).orElse(null);
            List<URLFetch> listURLFetch = getListURLFetch();
            for (URLFetch urlFetch : listURLFetch) {
                String json = Util.readUrl(getURLTopicAPI(urlFetch.getFrameworkProgramme()));
                List<CallData> listCallData = getListCallData(getURLCallAPI(urlFetch.getFrameworkProgramme()));
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
                                String externalIdGrant = name + "_" + topicFileName;
                                Optional<Grant> grantOptional = grantService.getByExternalId(externalIdGrant);
                                if (!grantOptional.isPresent()) {
                                    GrantDTO grantDTO = new GrantDTO(
                                        null, null, null, 0,
                                        grantProgramDTO,
                                        Util.readStringJSONObject(jsonCall, "title"),
                                        null,
                                        getDescription(topicFileName),
                                        readDateJSONObject(jsonCall, "plannedOpeningDate"),
                                        readDateJSONObject(jsonCall, "deadlineDates"),
                                        null,
                                        null,
                                        externalIdGrant,
                                        getURLTopic(urlFetch.getFrameworkProgramme(), topicFileName),
                                        null
                                    );
                                    Util.setStatus(grantDTO, now);

                                    for (int j = 0; j < listCallData.size(); j++) {
                                        boolean check = false;
                                        CallData callData = listCallData.get(j);
                                        if (callData.getCallFileName().equals(callFileName)) {
                                            if (callData.getOpenDate().equals(grantDTO.getOpenDate())) {
                                                for (Instant instant : callData.getCloseDate()) {
                                                    if (instant.equals(grantDTO.getCloseDate())) {
                                                        grantDTO.setFinanceDescription(callData.getAmount());
                                                        check = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (check) {
                                            listCallData.remove(j);
                                            break;
                                        }
                                    }
                                    grantService.createGrantCall(grantDTO);
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
        return listDate.get(0);
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
                    JSONArray jsonBudgetArray = jsonCallBudgetOverviewArray.getJSONObject(0).getJSONArray("Budget");
                    for (int j = 0; j < jsonBudgetArray.length(); j++) {
                        JSONObject jsonBudgetObject = jsonBudgetArray.getJSONObject(j);
                        CallData callData = getCallData(callFileName, jsonBudgetObject);
                        listCallData.add(callData);
                    }
                } catch (JSONException e) {
                    JSONObject jsonBudgetObject = jsonCallBudgetOverviewArray.getJSONObject(0).getJSONObject("Budget");
                    CallData callData = getCallData(callFileName, jsonBudgetObject);
                    listCallData.add(callData);
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
        callData.setCloseDate(readListDateJSONObject(jsonBudgetObject, "Deadline"));
        if (jsonBudgetObject.has("Amount")) {
            try {
                try {
                    JSONArray jsonArray = jsonBudgetObject.getJSONArray("Amount");
                    if (jsonArray.length() > 0) {
                        callData.setAmount(jsonArray.getString(0));
                    }
                } catch (Exception e) {
                    callData.setAmount(jsonBudgetObject.getString("Amount"));
                }
            } catch (Exception e) {
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
        return new SimpleDateFormat("dd MMM yyyy").parse(dateString).toInstant();
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
    }

}
