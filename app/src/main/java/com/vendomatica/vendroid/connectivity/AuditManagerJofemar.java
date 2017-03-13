package com.vendomatica.vendroid.connectivity;


import android.os.Bundle;
import android.util.Log;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortBuilder;
import com.vendomatica.vendroid.connectivity.bluetooth.IOnBtOpenPort;
import com.vendomatica.vendroid.connectivity.helpers.FileHelper;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.jofemar.JofemarCommunication;
import com.vendomatica.vendroid.connectivity.protocols.jofemar.JofemarDataReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerJofemar extends AuditManagerBase implements IonAuditState {

    private ProtocolsBase mProtocolBase;

    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 1200;

    private int attempsCounter = 0;
    private int dataSize = 0;

    private int countdown = 4*60*1000;


    public AuditManagerJofemar(IAuditManager i) {
        super(i);
    }

    @Override
    public void go(String btType) {

        if (callback != null) {
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                log("BT Port Opened.");

                mProtocolBase = new JofemarDataReader(AuditManagerJofemar.this);
                mProtocolBase.startAudit();

                ReferencesStorage.getInstance().comm = new JofemarCommunication();

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

    @Override
    public void stop() {
        timerStop();

        if (mProtocolBase != null){
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
            mProtocolBase = null;
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
    public void onAuditData(Bundle b) {
        final byte[] a = b.getByteArray("Data");
        final String str = new String(a);

        final String fileName = "jofemar.dat";

        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(str));

        mStoredFiles.add(f);
        mStoredFiles.add(str);
        mStoredFiles.add("Jofemar_M");
    }

    @Override
    public void onAuditDataRead(Bundle b) {
        if (attempsCounter == 0){
            dataSize = b.getInt("Message");
        }else{
            final Integer size = b.getInt("Message") + dataSize;
            b.putInt("Message", size);
        }
        super.onAuditDataRead(b);
    }

    @Override
    public void onAuditError(Bundle b) {
        if (attempsCounter == 1) {
            super.onAuditError(b);
            return;
        }

        log("Wrong log file. Try to read it again");

        //According to the issue with BT reconnection
        //We have wrong data in the buffer
        //So we close all and try to reinitialize all staff again
        attempsCounter++;

        stop();
        Log.d("AuditManagerJofemart", "BT Port closed");

        mProtocolBase = new JofemarDataReader(AuditManagerJofemar.this);
        mProtocolBase.startAudit();

        Log.d("AuditManagerJofemart" , "BT Port try to open");
        log("Try to  open BT Port.");
        ReferencesStorage.getInstance().btPort.openSocketsAsyncWithPause(new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                log("BT Port Opened.");
                Log.d("AuditManagerJofemar" , "BT Port opened");
                ReferencesStorage.getInstance().comm = new JofemarCommunication();

                mTimer = new Timer("JofemarAuditManagerUpdate");
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            safeUpdate();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mProtocolBase.stopAudit();
                        }
                    }
                }, 100, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can not re-initialize BT");
            }
        }, 10000);
    }
}
