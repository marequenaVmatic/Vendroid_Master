package com.vendomatica.vendroid.connectivity.protocols.jofemar;

import android.os.Bundle;
import android.util.Log;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.helpers.ArraysHelper;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;
import java.util.Arrays;

public class JofemarCollectState  <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement {

    public static final Event UPDATE_STATE = new Event("UPDATE_STATE", 4000);

    enum states {
        INITIAL,
        COLLECTION,
        DONE,
        TIMEOUT,
    }

    private states currentState = states.INITIAL;

    private byte data[];

    private final int BUF_SIZE = 256;
    private byte buf[] = new byte[BUF_SIZE];

    public JofemarCollectState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        return false;
    }

    @Override
    public void stopAudit() {

    }

    @Override
    public void update(int deltaTime) throws IOException {
        JofemarCommunication comm = (JofemarCommunication) ReferencesStorage.getInstance().comm;

        if (timeOut <0){
            if (data == null) {
                Log.d("JofemarRDCollectState", "TimeOut");
                sendTimeOutMessage("Timeout!");
                currentState = states.TIMEOUT;
            }else{
                if (!new String(Arrays.copyOf(data, 50)).contains("\n\r")){
                    onError();
                    return;
                }
                if (!new String(Arrays.copyOf(data, 5)).contains("***")){
                //if (data[1]!= (byte)0x2A || data[2]!= (byte)0x2A){
                    onError();
                    return;
                }

                auditDone();
            }
        }

        switch (currentState) {
            case INITIAL:
                    currentState = states.COLLECTION;
                    data = null;
                    Log.d("JofemarRDCollectState", "Initial");
                    log("Start read log\n");
                    castEvent(UPDATE_STATE);
                break;
            case COLLECTION:
                int l = comm.available();
                if ( l == 0)
                    break;

                l = (l > 256) ?  BUF_SIZE : l;

                comm.read(buf ,l);
                data = ArraysHelper.appendData(data, Arrays.copyOf(buf, l));
                Log.d("JofemarRDCollectState", "read:" + Integer.toString(l) + " bytes");

                sendDataSize(data.length);

                castEvent(UPDATE_STATE);
                break;
            case DONE:

                castEvent(UPDATE_STATE);
                break;
            case TIMEOUT:

                castEvent(UPDATE_STATE);
                break;
        }

        timeOut = timeOut - deltaTime;
    }

    private void onError() {
        sendErrorMessage("Some error occured while downloading data.");
        currentState = states.TIMEOUT;
        castEvent(UPDATE_STATE);
    }

    private void auditDone() {
        Log.d("JofemarCollectState", "Done");
        sendDataMessage(data);
        sendDoneMessage();
        currentState = states.DONE;
        castEvent(UPDATE_STATE);
    }

    void sendErrorMessage(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_ERROR, b);
    }

    void sendDataMessage(byte[] data){
        final Bundle b = new Bundle();
        b.putByteArray("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
    }

    void sendDataSize(Integer size){
        final Bundle b = new Bundle();
        b.putInt("Data", size);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ, b);
    }

    void sendTimeOutMessage(String s){
        final Bundle b = new Bundle();
        b.putString("Message", s);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT, b);
    }

    void sendDoneMessage(){
        final Bundle b = new Bundle();
        b.putString("Message", "Done.");

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DONE, b);
    }

    void log(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);
        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_LOG, b);
    }

    void sendMessage(int what, Bundle b){
        b.putString("Protocol", "Jofemar");

        final android.os.Handler h = JofemarInternalStorage.getInstance().handler;
        android.os.Message m = h.obtainMessage();
        m.what = what;
        m.setData(b);
        h.sendMessage(m);
    }
}
