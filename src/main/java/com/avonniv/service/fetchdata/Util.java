package com.avonniv.service.fetchdata;

import com.avonniv.service.dto.GrantDTO;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;

public class Util {
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(120000);
            urlConnection.setReadTimeout(120000);
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

    public static String readStringJSONObject(JSONObject object, String key) {
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

    public static String convertStringMonthToNumber(String date) {
        String dateString = date;
        dateString = dateString.toLowerCase().replaceAll("januari", "01");
        dateString = dateString.replaceAll("februari", "02");
        dateString = dateString.replaceAll("mars", "03");
        dateString = dateString.replaceAll("april", "04");
        dateString = dateString.replaceAll("maj", "05");
        dateString = dateString.replaceAll("juni", "06");
        dateString = dateString.replaceAll("juli", "07");
        dateString = dateString.replaceAll("augusti", "08");
        dateString = dateString.replaceAll("september", "09");
        dateString = dateString.replaceAll("oktober", "10");
        dateString = dateString.replaceAll("november", "11");
        dateString = dateString.replaceAll("december", "12");
        return dateString;
    }

    public static void setStatus(GrantDTO grantDTO, Instant now) {
        if (grantDTO.getStatus() == GrantDTO.Status.un_publish.getValue()) {
            return;
        }
        if (grantDTO.getCloseDate() != null && grantDTO.getCloseDate().getEpochSecond() < now.getEpochSecond()) {
            grantDTO.setStatus(GrantDTO.Status.close.getValue());
        } else if (grantDTO.getOpenDate() != null) {
            if (grantDTO.getOpenDate().getEpochSecond() > now.getEpochSecond()) {
                grantDTO.setStatus(GrantDTO.Status.coming.getValue());
            } else {
                grantDTO.setStatus(GrantDTO.Status.open.getValue());
            }
        }
    }
}
