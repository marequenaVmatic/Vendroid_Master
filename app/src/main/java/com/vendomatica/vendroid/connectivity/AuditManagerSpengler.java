package com.vendomatica.vendroid.connectivity;

import android.os.Bundle;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortBuilder;
import com.vendomatica.vendroid.connectivity.bluetooth.IOnBtOpenPort;
import com.vendomatica.vendroid.connectivity.helpers.FileHelper;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.spengler.FirstProtocolDataReader;
import com.vendomatica.vendroid.connectivity.protocols.spengler.SpenglerCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AuditManagerSpengler
        extends AuditManagerBase
        implements IonAuditState {


    private ProtocolsBase mProtocolBase;

    private final int DELTA_TIME = 100;
    private final int BAUDRATE = 19200;

    private int countdown = 4*60*1000;


    public AuditManagerSpengler(IAuditManager callback){
        super(callback);
    }


    public void go(String btType){

        if (callback != null) {
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {

                ArrayList<String> files = new ArrayList<>();
                files.add("OPNCASH.DAT");
                files.add("ADMIN.28");
                mProtocolBase = new FirstProtocolDataReader(AuditManagerSpengler.this, files);
                mProtocolBase.startAudit();

                ReferencesStorage.getInstance().comm = new SpenglerCommunication();

                timerStart(DELTA_TIME, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can't initialize BT.");
            }
        });
    }

    //Callback function on timer tick
    @Override
    public void onTimerTick() {
        countdown -= DELTA_TIME;
        try {
            safeUpdate();

        } catch (IOException e) {
            e.printStackTrace();
            stopErrorWithMessage("Can't download data!");
        }

        if (countdown<0){
            stopErrorWithMessage("Time Out!");
        }
    }

    private void safeUpdate() throws IOException {
        //Check is our communication valid
        if (ReferencesStorage.getInstance().comm.isStop()) {
            stopErrorWithMessage("Communication Error");
        } else {
            mProtocolBase.update(DELTA_TIME);
        }
    }

    public void stop(){
        timerStop();

        if (mProtocolBase != null) {
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }
    }


    @Override
    public void onAuditData(Bundle b) {
        //Here we have file!!! We must parse it, show and store

        final String fileName = b.getString("FileName");
        final byte[] d = b.getByteArray("Data");

        String data;
        String type;

        switch (fileName) {
            case "OPNCASH.DAT":
                data = extractOPNCASH(d);
                type = "Spengler_OPN";
                break;
            case "ADMIN.28":
                data = extractADMIN(d);
                type = "Spengler_ADM28";
                break;
            default:
                log("Unknown file:" + fileName);
                return;
        }
        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(data));

        mStoredFiles.add(f);
        mStoredFiles.add(data);
        mStoredFiles.add(type);
    }


    private String extractOPNCASH(byte[] d){
        final StringBuilder l = new StringBuilder();
        String s;

        for (int j = 0; j < d.length ; j = j + 44)
        {
            s = "";
            for (int i = 0; i < 44; i = i + 4)
            {
                final int n = (0x00FF&d[j + i]) + 256 *(0x00FF&d[j + i + 1]) + 256 * 256 *(0x00FF&d[j + i + 2]) + 256 * 256 * 256 *(0x00FF&d[j + i + 3]);
                s = s + " " + Integer.toString(n);
            }
            l.append(s);
            l.append("\n\r");
        }

        return l.toString();
    }

    private String extractADMIN(byte[] d){
        final StringBuilder l = new StringBuilder();

        int category;
        for (category = 0; category <= 2; category++)
        {
            for (int position = 0; position <= 11; position++)
            {
                int offset = 72 * category + 2 * position;
                int count = (0x00FF&d[offset]) + 256 * (0x00FF&d[offset + 1]);
                offset = 72 * category + 24 + 4 * position;
                int amount = (0x00FF&d[offset]) + 256 * (0x00FF&d[offset + 1]) +
                        256 * 256 * (0x00FF&d[offset + 2]) + 256 * 256 * 256 * (0x00FF&d[offset + 3]);

                final String s = Integer.toString(position) + " " + Integer.toString(category) + " " +
                        Integer.toString(count) + " " + Integer.toString(amount);
                l.append(s);
                l.append("\n\r");
            }
        }

        for (int position = 0; position <= 11; position++)
        {
            int offset = 72 * category + 2 * position;
            int count = d[offset] + 256 * d[offset + 1];

            int amount = 0;

            final String s = Integer.toString(position) + " " + Integer.toString(category) + " " +
                    Integer.toString(count) + " " + Integer.toString(amount);
            l.append(s);
            l.append("\n\r");
        }

        return l.toString();
    }
}
