package com.example.aurduino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {

    TextView time1, time2, value1, value2,locate;
    SupportMapFragment mapFragment;
    ProgressBar progressBar;
    protected LocationManager locationManager;

    Button b;
    private GoogleMap mMap;
    String Address;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declarations();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Toast.makeText(this, "OnMapReady", Toast.LENGTH_SHORT).show();

        LatLng home = new LatLng(11.0168, 76.9558);
        float zoomLevel = (float) 5.0;
        mMap.addMarker(new MarkerOptions().position(home).title("You are at here!!!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoomLevel));

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHeroList();
            }
        });

    }



    public void declarations(){
        value1= findViewById(R.id.value1);
        locate= findViewById(R.id.location);
        value2 = findViewById(R.id.value2);
        time1= findViewById(R.id.time1);
        time2= findViewById(R.id.time2);
        progressBar = findViewById(R.id.progressBar);
        b = findViewById(R.id.button);
    }



    @Override
    public void onLocationChanged(Location location) {
        LatLng home = new LatLng(location.getLatitude(), location.getLongitude());
        float zoomLevel = (float) 5.0;

        mMap.addMarker(new MarkerOptions().position(home).title("You are at here now!!!"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoomLevel));

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(11.0168, 76.9558, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Address = address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        locate.setText(Address);
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

    private void loadHeroList() {
        //getting the progressbar
        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://io.adafruit.com/api/v2/rohinivsenthil/feeds/hello",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("response", response);

                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            Log.d("json", "onResponse: "+obj);


                                //creating a hero object and giving them the values from json object
                                value1.setText(obj.getString("last_value")+"Bpm");
                                value2.setText(obj.getString("last_value")+"*F");
                                time1.setText(obj.getString("updated_at"));
                                time2.setText(obj.getString("updated_at"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

}