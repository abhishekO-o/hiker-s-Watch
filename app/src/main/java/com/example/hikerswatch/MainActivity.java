package com.example.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends WearableActivity {
    TextView lat, lng, alt, myaddress;
    Button btn;
    LocationManager manager;
    LocationListener listener;
    private TextView mTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();
        }

    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }


    }

    public void updateLocInfo(Location location) {
        lat = (TextView) findViewById(R.id.lat);
        lng = (TextView) findViewById(R.id.lng);
        alt = (TextView) findViewById(R.id.alt);
        lat.setText("Latitude : " + location.getLatitude());
        lng.setText("Longitude : " + location.getLongitude());
        alt.setText("Altitude : " + location.getAltitude());

        Log.i("location", location.toString() + "");

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String userAddress = "Could not find Address";
            List<Address> listaddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listaddress != null && listaddress.size() > 0) {
                Log.i("placeInfo", listaddress.get(0).toString());
                userAddress = "Address : ";
                userAddress += listaddress.get(0).getAddressLine(0);
                /*if(listaddress.get(0).getAdminArea()!=null){
                    userAddress+=listaddress.get(0).getAdminArea();
                }
                if(listaddress.get(0).getCountryName()!=null){
                    userAddress+=listaddress.get(0).getCountryName();
                }
                if(listaddress.get(0).getPostalCode()!=null){
                    userAddress+=listaddress.get(0).getPostalCode();
                }
                */
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocInfo(location);

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
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                updateLocInfo(location);
            }
        }

        // Enables Always-on
        setAmbientEnabled();
    }
}
