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

    public static void setStatus(GrantDTO grantDTO, Instant now) {
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
