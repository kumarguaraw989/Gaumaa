package com.ecom.agrisewa.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GpsLocation {

    public static double latitude;
    public static double longitude;

    public static String getGPSLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) (context), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            return "";
        } else {
            GPSTracker gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                String cityName = null;
                String fullLocation = "";
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        cityName = addresses.get(0).getLocality();
                        fullLocation = cityName + "-" + addresses.get(0).getAddressLine(0);
                        Log.e("COORDINATES", "Latitude: " + latitude + ", Longitude: " + longitude);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return fullLocation;
            } else {
                gps.showSettingsAlert();
                return "";
            }
        }
    }

    public static List<String> getCoordinates(Context context) {
        String location = getGPSLocation(context);
        if (!location.equals("")) {
            List<String> coordinates = new ArrayList<>();
            coordinates.add(latitude + "");
            coordinates.add(longitude + "");
            return coordinates;
        } else {
            return new ArrayList<>();
        }
    }

}
