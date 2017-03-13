package com.vendomatica.vendroid.connectivity;


import android.os.Bundle;
import android.util.Log;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortBuilder;
import com.vendomatica.vendroid.connectivity.bluetooth.IOnBtOpenPort;
import com.vendomatica.vendroid.connectivity.helpers.FileHelper;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.jofemarrd.JofemarRDCommunication;
import com.vendomatica.vendroid.connectivity.protocols.jofemarrd.JofemarRDDataReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerJofemarRD extends AuditManagerBase implements IonAuditState {

    public static final String JOFEMAR_RD = "Jofemar_RD";
    private ProtocolsBase mProtocolBase;

    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;

    private int attempsCounter = 0;
    private int dataSize = 0;

    private int countdown = 4*60*1000;


    public AuditManagerJofemarRD(IAuditManager i) {
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

                mProtocolBase = new JofemarRDDataReader(AuditManagerJofemarRD.this);
                mProtocolBase.startAudit();

                ReferencesStorage.getInstance().comm = new JofemarRDCommunication();

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

        if (mProtocolBase != null){
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
            mProtocolBase = null;
        }

    }

    @Override
    public void onAuditData(Bundle b) {
        final byte[] a = b.getByteArray("Data");
        final String str = new String(a);

        final String fileName = "jofemar_rd.dat";

        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(str));

        mStoredFiles.add(f);
        mStoredFiles.add(str);
        mStoredFiles.add(JOFEMAR_RD);
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
        Log.d("AuditManagerJofemartRD", "BT Port closed");

        mProtocolBase = new JofemarRDDataReader(AuditManagerJofemarRD.this);
        mProtocolBase.startAudit();

        Log.d("AuditManagerJofemarRD" , "BT Port try to open");
        log("Try to  open BT Port.");
        ReferencesStorage.getInstance().btPort.openSocketsAsyncWithPause(new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                log("BT Port Opened.");
                Log.d("AuditManagerJofemarRD" , "BT Port opened");
                ReferencesStorage.getInstance().comm = new JofemarRDCommunication();

                mTimer = new Timer("JofemarRDAuditManagerUpdate");
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
