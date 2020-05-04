package com.example.aurduino;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aurduino.backConnection.GetService;
import com.example.aurduino.backConnection.PostService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends FragmentActivity implements LocationListener {

    TextView time1, time2, value1, value2,locate;
    Timer timer;
    SupportMapFragment mapFragment;
    ProgressBar progressBar;
    protected LocationManager locationManager;
    int cou = 0,cou1 = 0;
    String val="";
    boolean but = false;
    WaveLoadingView waveLoadingView;
    Button b,b2;
    private GoogleMap mMap;
    String Address;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    BroadcastReceiver br,br1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        declarations();
        progressBar.setVisibility(View.VISIBLE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


                br=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String data = intent.getStringExtra("data");
                        progressBar.setVisibility(View.INVISIBLE);
                        int p=0;
                        try{
                            JSONArray ja = new JSONArray(data);
                            JSONObject obj = ja.getJSONObject(0);
                            Assign(obj.getString("value"),obj.getString("created_at"));

                        }catch(Exception e){e.printStackTrace();}
                    }
                };
        Intent i1 = new Intent(this, GetService.class);
        i1.putExtra("feed","ultrasonic");
        startService(i1);


        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(br,new IntentFilter("getData"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(br);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(br1);

    }

    public void declarations(){
        value1= findViewById(R.id.value1);
        locate= findViewById(R.id.location);
        value2 = findViewById(R.id.value2);
        time1= findViewById(R.id.time1);
        time2= findViewById(R.id.time2);
        b2 = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
        b = findViewById(R.id.refreshButton);
        waveLoadingView = findViewById(R.id.waveLoadingView);
    }


    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Address = address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        locate.setText(Address);
    }

    public void fun(View v){
        Intent i = new Intent(this, PostService.class);

        colorDecider();

        try{
            JSONObject j = new JSONObject();
            j.put("value",val);
            i.putExtra("data",j.toString());
        }catch(Exception e){e.printStackTrace();}
        i.putExtra("feed","manual");
        startService(i);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void fun2(View v) {
        //getting the progressbar
        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, GetService.class);
        i.putExtra("feed","ultrasonic");
        startService(i);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                br1=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String data = intent.getStringExtra("data");
                        progressBar.setVisibility(View.INVISIBLE);
                        int p=0;
                        try{
                            JSONArray ja = new JSONArray(data);
                            JSONObject obj = ja.getJSONObject(0);
                            val = obj.getString("value");
                            but = true;
                            Toast.makeText(getApplicationContext(),obj.getString("value"),Toast.LENGTH_SHORT).show();
                            if (val.equals("on")){
                                b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                b2.setTextColor(getResources().getColor(R.color.white));
                            }
                            else{
                                b2.setBackgroundColor(getResources().getColor(R.color.white));
                                b2.setTextColor(getResources().getColor(R.color.black));
                            }

                        }catch(Exception e){e.printStackTrace();}
                    }
                };


                Intent i = new Intent(getApplicationContext(), GetService.class);
                i.putExtra("feed","manual");
                startService(i);

                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(br1,new IntentFilter("getData"));
            }
        },3000);

    }

    private void Assign(String val,String time){
        int ji1 = (int) Calc((double) Integer.parseInt(val));
        ji1 = 100 - ji1;
        if (ji1>=80){
            waveLoadingView.setWaveColor(Color.GREEN);
            if (ji1>=88){
                waveLoadingView.setCenterTitle("Full");
                if (cou == 0)
                fullNotifications(ji1);
                cou++;
            }
            else{
                waveLoadingView.setCenterTitle(ji1+"%");
            }
        }
        else if(ji1 <80 && ji1 >= 60){
            waveLoadingView.setWaveColor(R.color.lightblue);
            waveLoadingView.setCenterTitle(ji1+"%");
        }
        else if(ji1 < 60 && ji1 >= 40){
            waveLoadingView.setWaveColor(Color.YELLOW);
            waveLoadingView.setCenterTitle(ji1+"%");
        }
        else if(ji1 < 40 && ji1 >= 20){
            waveLoadingView.setWaveColor(Color.CYAN);
            waveLoadingView.setCenterTitle(ji1+"%");
        }
        else{
            waveLoadingView.setWaveColor(Color.RED);
            waveLoadingView.setCenterTitle(ji1+"%");
            if (cou1 == 0)
                emptyNotifications(ji1);
            cou1++;
        }
        value1.setText(ji1+"%");
        time1.setText(time);

        waveLoadingView.setProgressValue(ji1);
    }

    private double Calc(Double i){
        return ((i/(double) 140)*100);
    }

    private void fullNotifications(int per){
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this)
                .setContentTitle("Watanker Alert")
                .setContentText("Your tank has reached "+per+"% so turn off");

        Intent i = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        builder1.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder1.build());
    }

    private void emptyNotifications(int per){
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this)
                .setContentTitle("Watanker Alert")
                .setContentText("Your tank has reached "+per+"% so turn on");

        Intent i = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        builder1.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder1.build());
    }

    private void colorDecider(){
        if(val.equals("on")) {

            val="off";

            b2.setBackgroundColor(getResources().getColor(R.color.white));
            b2.setTextColor(getResources().getColor(R.color.black));

        }
        else {
            val="on";
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            b2.setTextColor(getResources().getColor(R.color.white));

        }
    }


}