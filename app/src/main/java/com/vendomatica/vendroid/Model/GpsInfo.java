package com.vendomatica.vendroid.Model;

/**
 * Created by Miguel Requena on 2015-11-30.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsInfo extends Service implements LocationListener {

    private final Context mContext;

    boolean isGPSEnabled = false;


    boolean isNetworkEnabled = false;


    boolean isGetLocation = false;

    Location location;
    double lat;
    double lon;

    private static final long MIN_DISTANCE_UPDATES = 10;

    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public GpsInfo(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                        Log.e("PERMISSION_EXCEPTION", "1");
                    }catch (SecurityException e){
                        Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                    }
                    if (locationManager != null) {
                        try {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Log.e("PERMISSION_EXCEPTION", "2");
                        }catch (SecurityException e){
                            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                        }
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                            Log.e("PERMISSION_EXCEPTION", "3");
                        }catch (SecurityException e){
                            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                        }
                        if (locationManager != null) {
                            try {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.e("PERMISSION_EXCEPTION", "4");
                            }catch (SecurityException e){
                                Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                            }
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(GpsInfo.this);
                Log.e("PERMISSION_EXCEPTION", "5");
            }catch (SecurityException e){
                Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
            }
        }
    }


    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}