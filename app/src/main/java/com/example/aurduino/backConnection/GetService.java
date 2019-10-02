package com.example.aurduino.backConnection;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;


public class GetService extends IntentService {

    String url="https://io.adafruit.com/api/v2/chandan3966/feeds/";
    String result;

    public GetService() {
        super("GetService");
    }

     @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String feed = intent.getStringExtra("feed");

            url+=feed+"/data?x-aio-key=7dbe3f897e5941eba1a06ba39c5093c4&limit=1";

            fun();
             Intent i = new Intent("getData");
             i.putExtra("data",result);
            Log.d("getService", result);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

    void fun(){
        String inputLine;      try {
            //Create a URL object holding our url
            URL myUrl = new URL(url);         //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();         //Set methods and timeouts
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            //Connect to our url
            connection.connect();       //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());         //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();         //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }         //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();         //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


}
