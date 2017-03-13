package com.vendomatica.vendroid.connectivity;


import android.app.Application;

public class vendingApp extends Application {
    public static final String APP_PACKAGE = "com.dexreader";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
