package com.example.aurduino.backConnection;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostService extends IntentService {

    String data,urls="https://io.adafruit.com/api/v2/chandan3966/feeds/",result="";

    public PostService() {
        super("PostService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            data=intent.getStringExtra("data");
            String feed = intent.getStringExtra("feed");

            urls+=feed+"/data?x-aio-key=7dbe3f897e5941eba1a06ba39c5093c4";

            fun();
            Log.d("postService", result);
        }
    }

    void fun() {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(urls);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setRequestMethod("POST");

            // Send the post body
            if (data != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data);
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                result = convertInputStreamToString(inputStream);

                // From here you can convert the string to JSON with whatever JSON parser you like to use               // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method            } else {
                // Status code is not 200
                // Do something to handle the error
            }

        } catch (Exception e) {
//            Log.d(TAG, e.getLocalizedMessage());
        }
    }
    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
