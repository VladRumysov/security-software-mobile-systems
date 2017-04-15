package com.example.vlad.lab_googlemaps;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vlad on 15.04.2017.
 */

public class ConnectAndRequestToServer {
    HttpURLConnection httpcon;
    String url = null;
    String result = null;

    public void POST(String url, SendingData sendingData) {

        try {
            //Build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("device_id", sendingData.getDevice_id());
            jsonObject.accumulate("coords", sendingData.getCoords());
            jsonObject.accumulate("is_hash", sendingData.getIs_hash());

            //Connect
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            writer.close();
            os.close();

            Log.d(">>>>>>>", jsonObject.toString());

            //Read
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

            Log.d("RESULT: " , result);

        } catch (Exception e) {
            e.getMessage();
        }

    }
}
