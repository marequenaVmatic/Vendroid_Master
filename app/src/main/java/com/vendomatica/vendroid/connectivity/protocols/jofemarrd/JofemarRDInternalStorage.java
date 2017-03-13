package com.vendomatica.vendroid.connectivity.protocols.jofemarrd;

import android.os.Handler;

public class JofemarRDInternalStorage {
    private static JofemarRDInternalStorage instance;

    Handler handler;

    private JofemarRDInternalStorage(){}

    public static JofemarRDInternalStorage getInstance(){
        if (instance == null)
            instance = new JofemarRDInternalStorage();

        return instance;
    }
}
