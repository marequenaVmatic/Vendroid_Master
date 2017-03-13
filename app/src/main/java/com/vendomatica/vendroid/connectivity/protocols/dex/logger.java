package com.vendomatica.vendroid.connectivity.protocols.dex;

import android.os.Bundle;

import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;

public class logger {

    public static void log(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);
        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_LOG, b);
    }

    public static void sendErrorMessage(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_ERROR, b);
    }

    public static void sendDataMessage(byte[] data){
        final Bundle b = new Bundle();
        b.putByteArray("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
    }

    public static void sendDataMessage(String data){
        final Bundle b = new Bundle();
        b.putString("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
    }

    public static void sendTimeOutMessage(){
        final Bundle b = new Bundle();
        b.putString("Message", "TimeOut!");

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT, b);
    }

    public static void sendDoneMessage(){
        final Bundle b = new Bundle();
        b.putString("Message", "Done.");

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DONE, b);
    }

    public static void sendDataSize(Integer size){
        final Bundle b = new Bundle();
        b.putInt("Message", size);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ, b);
    }

    public static void sendMessage(int what, Bundle b){
        b.putString("Protocol", "DEX");

        final android.os.Handler h = DexInternalStorage.getInstance().handler;
        android.os.Message m = h.obtainMessage();
        m.what = what;
        m.setData(b);
        h.sendMessage(m);
    }
}
