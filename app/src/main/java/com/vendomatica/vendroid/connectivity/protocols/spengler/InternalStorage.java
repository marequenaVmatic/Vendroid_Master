package com.vendomatica.vendroid.connectivity.protocols.spengler;

import android.os.Handler;

import java.util.ArrayList;

class InternalStorage {
    private static InternalStorage instance;


    Handler handler;
    ArrayList<String> fileList;
    Integer fIndex = 0;

    private InternalStorage(){}

    public static InternalStorage getInstance(){
        if (instance == null)
            instance =  new InternalStorage();

        return instance;
    }
}
