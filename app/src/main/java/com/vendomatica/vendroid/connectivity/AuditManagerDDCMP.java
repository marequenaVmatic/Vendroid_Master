package com.vendomatica.vendroid.connectivity;

import android.os.Bundle;

import com.vendomatica.vendroid.connectivity.bluetooth.BTPortBuilder;
import com.vendomatica.vendroid.connectivity.bluetooth.IOnBtOpenPort;
import com.vendomatica.vendroid.connectivity.helpers.FileHelper;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.ddcmp.DDCMPCommunication;
import com.vendomatica.vendroid.connectivity.protocols.ddcmp.DDCMPDataReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Timer;

public class AuditManagerDDCMP extends AuditManagerBase{

    private ProtocolsBase mProtocolBase;

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;

    private int countdown = 4*60*1000;

    public AuditManagerDDCMP(IAuditManager i) {
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

                mProtocolBase = new DDCMPDataReader(AuditManagerDDCMP.this);
                ReferencesStorage.getInstance().comm = new DDCMPCommunication();

                mProtocolBase.startAudit();

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
    public void stop(){
        timerStop();

        if (mProtocolBase != null) {
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
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

        final String fileName = "ddcmp.dcp";

        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(str));

        mStoredFiles.add(f);
        mStoredFiles.add(str);
        mStoredFiles.add("DDCMP");
    }

}
