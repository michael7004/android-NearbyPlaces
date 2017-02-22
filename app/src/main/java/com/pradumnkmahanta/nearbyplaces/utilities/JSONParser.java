package com.pradumnkmahanta.nearbyplaces.utilities;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Pradumn K Mahanta on 04-03-2016.
 **/

public class JSONParser {

    String charset = "UTF-8";
    HttpURLConnection httpURLConnection;
    DataOutputStream dataOutputStream;
    StringBuilder stringBuilder;
    URL urlObj;
    JSONObject jsonObject = null;
    StringBuilder sbParams;
    String paramsString;

    public JSONObject makeHttpRequest(String url, String method, HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }

        if (method.equals("POST")) {
            try {
                urlObj = new URL(url);
                httpURLConnection = (HttpURLConnection) urlObj.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept-Charset", charset);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();
                paramsString = sbParams.toString();
                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(paramsString);
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equals("GET")) {
            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }

            Log.d("URL",url);
            Log.d("URL", url);
            try {
                urlObj = new URL(url);
                httpURLConnection = (HttpURLConnection) urlObj.openConnection();
                httpURLConnection.setDoOutput(false);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept-Charset", charset);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            Log.d("JSON Parser", "Result: " + stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpURLConnection.disconnect();

        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jsonObject;
    }
}