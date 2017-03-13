package com.vendomatica.vendroid.connectivity;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortCommunication;
import com.vendomatica.vendroid.connectivity.protocols.Communication;

public class ReferencesStorage {
    private static ReferencesStorage instance;

    private ReferencesStorage(){

    }

    public static ReferencesStorage getInstance(){
        if (instance == null){
            instance = new ReferencesStorage();
        }

        return instance;
    }

    public BTPortCommunication btPort;
    public Communication comm;
}
