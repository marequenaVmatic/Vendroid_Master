package com.vendomatica.vendroid.connectivity;

import android.os.Bundle;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortBuilder;
import com.vendomatica.vendroid.connectivity.bluetooth.IOnBtOpenPort;
import com.vendomatica.vendroid.connectivity.helpers.FileHelper;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.dex.DexCommunication;
import com.vendomatica.vendroid.connectivity.protocols.dex.DexProtocolReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Timer;

public class AuditManagerDex
extends AuditManagerBase {

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;
    private static final int TIMEOUT = 1*60*1000;

    private int countdown = TIMEOUT;

    ProtocolsBase mProtocolBase;

    public AuditManagerDex(IAuditManager callback){
        super(callback);
    }

    @Override
    public void go(String btType) {

        if(callback!= null){
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                mProtocolBase = new DexProtocolReader(AuditManagerDex.this);

                ReferencesStorage.getInstance().comm = new DexCommunication();
                mProtocolBase.startAudit();

                timerStart(DELTA_TIME, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can't initialize BT");
            }
        });
    }

    @Override
    public void onAuditDataRead(Bundle b) {
        super.onAuditDataRead(b);

        countdown = TIMEOUT;
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
        //Check is communication valid
        if (ReferencesStorage.getInstance().comm.isStop()){
            stopErrorWithMessage("Communication Error");
        } else {
            mProtocolBase.update(DELTA_TIME);
        }
    }

    @Override
    public void stop() {
        timerStop();

        if (mProtocolBase != null) {
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }
    }

    @Override
    public void onAuditData(Bundle b) {
        final String a = b.getString("Data");
        final String fileName = "dex.dat";

        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(a));

        mStoredFiles.add(f);
        mStoredFiles.add(a);
        mStoredFiles.add("DEX");
    }
}
