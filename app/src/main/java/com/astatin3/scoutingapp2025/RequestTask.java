package com.astatin3.scoutingapp2025;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class RequestTask extends AsyncTask<String, String, String> {

    private Function<String, String> resultFunction = null;

    @Override
    protected String doInBackground(String... uri) {
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String[] headers = uri[1].split(", ");
            for(String header : headers){
                String[] split = header.split(": ");
                conn.setRequestProperty(split[0], split[1]);
            }
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                return readStream(conn.getInputStream());
                // Do normal input or output stream reading
            }
            else {
                return null; // See documentation for more info on response handling
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public void onResult(Function<String, String> func) {
        this.resultFunction = func;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(resultFunction != null){
            resultFunction.apply(result);
        }
    }
}