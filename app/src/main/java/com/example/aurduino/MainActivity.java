package com.example.aurduino;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
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

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends FragmentActivity implements LocationListener {

    TextView time1, time2, value1, value2,locate;
    SupportMapFragment mapFragment;
    ProgressBar progressBar;
    protected LocationManager locationManager;

    int val=0;
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
                    Assign(obj.getString("value"),obj.getString("updated_at"));

                }catch(Exception e){e.printStackTrace();}
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(br,new IntentFilter("getData"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(br);

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
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, GetService.class);
        i.putExtra("feed","manual");
        startService(i);

//        Intent i1 = new Intent(this, GetService.class);
//        i1.putExtra("feed","manual");
//        startService(i);

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
        if(val==0) {
            val=1023;
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            b2.setTextColor(getResources().getColor(R.color.white));


        }
        else {
            val=0;

            b2.setBackgroundColor(getResources().getColor(R.color.white));
            b2.setTextColor(getResources().getColor(R.color.black));

        }

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
        i.putExtra("feed","hello");
        startService(i);

    }

    private void Assign(String val,String time){
        value1.setText(Calc(Integer.parseInt(val))+"%");
        time1.setText(time);
        waveLoadingView.setProgressValue(Calc(Integer.parseInt(val)));
    }

    private int Calc(int value){
        return (value/1023)*100;
    }
}