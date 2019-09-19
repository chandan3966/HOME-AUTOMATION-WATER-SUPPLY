package com.example.aurduino;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FetchActivity extends AsyncTask<Void,Void,String> {

    TextView value2,value1;
    ProgressBar progressBar;
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public FetchActivity(TextView value1,TextView value2, ProgressBar progressBar) {
        this.value1 = value1;
        this.value2 = value2;
        this.progressBar = progressBar;
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("http://dummy.restapiexample.com/api/v1/employees");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            if (inputStream == null)
                return "Data is not fetched";
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.INVISIBLE);
        if (s.equals("Data is not fetched")) {
            value1.setText("Data is not fetched for Some Reason");
            value2.setText("Data is not fetched for Some Reason");
        } else {
            try {
                JSONObject root = new JSONObject(s);
                String activity = root.getString("id");
                String activity1 = root.getString("id");
                value1.setText(activity);
                value2.setText(activity1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
