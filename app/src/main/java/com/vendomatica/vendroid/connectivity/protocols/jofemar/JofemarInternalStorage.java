package com.vendomatica.vendroid.connectivity.protocols.jofemar;

import android.os.Handler;

public class JofemarInternalStorage {
    private static JofemarInternalStorage instance;

    Handler handler;

    private JofemarInternalStorage(){}

    public static JofemarInternalStorage getInstance(){
        if (instance == null)
            instance = new JofemarInternalStorage();

        return instance;
    }
}
