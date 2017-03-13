package com.vendomatica.vendroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.db.DBManager;

public class LogService extends Service {
    //private String latitude, longitude;
    //LocationLoader mLocationLoader;
    //private Location mNewLocation;
    public LogService() {
        /*
        MapsInitializer.initialize(LogService.this);
        mLocationLoader = new LocationLoader(this, false);
        mLocationLoader
                .SetOnLoadEventListener(new LocationLoader.OnLoadEventListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mNewLocation = location;
                        getLocation();
                        // mUpdateLocationHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onAddressChanged(String strAddress) {

                    }

                    @Override
                    public void onError(int iErrorCode) {
                        getLocation();
                    }
                });*/
    }/*
    private void getLocation() {
        if (mNewLocation == null)
            return;
        latitude = String.valueOf(mNewLocation.getLatitude());
        longitude = String.valueOf(mNewLocation.getLongitude());
    }*/
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String userid = "";
        if(intent != null) {
            userid = intent.getStringExtra("userid");
            String taskid = new String();
            taskid = intent.getStringExtra("taskid");
            String datetime = new String();
            datetime = intent.getStringExtra("datetime");
            String description = new String();
            description = intent.getStringExtra("description");
            String latitude = new String();
            latitude = intent.getStringExtra("latitude");
            String longitude = new String();
            longitude = intent.getStringExtra("longitude");
            DBManager.getManager().insertLogEvent(userid, taskid, datetime, description, latitude, longitude, Common.getInstance().gBatteryPercent, Common.getAvailableInternalMemorySize(), Common.getInstance().gChargingUSB ? 1 : 0, Common.getInstance().gChargingOther ? 1 : 0);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
