package com.vendomatica.vendroid.connectivity.protocols.spengler;

import android.os.Handler;

import java.util.ArrayList;

class SpenglerInternalStorage {
    private static SpenglerInternalStorage instance;

    Handler handler;
    ArrayList<String> fileList;

    private SpenglerInternalStorage(){}

    public static SpenglerInternalStorage getInstance(){
        if (instance == null)
            instance =  new SpenglerInternalStorage();

        return instance;
    }
}
