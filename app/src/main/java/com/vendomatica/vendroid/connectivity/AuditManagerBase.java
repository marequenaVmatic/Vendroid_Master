package com.vendomatica.vendroid.connectivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;

import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AuditManagerBase implements IonAuditState {
    protected IAuditManager callback;
    protected List<String> mStoredFiles;
    protected Timer mTimer;
    protected Handler mHandler = new Handler();

    BluetoothDevice mDevice;
    public void setBTDevice(BluetoothDevice d){
        mDevice = d;
    }

    public AuditManagerBase(IAuditManager i){
        callback = i;
        mStoredFiles = new ArrayList<>();
    }

    abstract public void go(String btType);
    abstract public void stop();

    abstract public void onTimerTick();


    public void timerStart(int start, int delta){
        if (mTimer == null){
            mTimer = new Timer("AuditManagerBase");
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    onTimerTick();
                }
            }, start, delta);
        }
    }

    public void timerStop(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onAuditDone(Bundle b) {
        //stopWithMessage("Done!");
        stopSuccess(mStoredFiles);
    }

    @Override
    public void onAuditError(Bundle b) {
        final String s = b.getString("Message");
        stopErrorWithMessage("Error! Stop with message: " + s);
    }

    @Override
    public void onAuditTimeOut(Bundle b) {
        final String s = b.getString("Message");
        stopErrorWithMessage("Timeout! Stop with message: " + s);
    }

    @Override
    public void onAuditUpdate(Bundle b) {

    }

    @Override
    public void onAuditLog(Bundle b) {
        final String s = b.getString("Message");
        if (callback!= null){
            callback.onAuditLog(s);
        }
    }

    public void onAuditDataRead(Bundle b){
        final Integer i = b.getInt("Message");
        if (callback != null){
            callback.onAuditDataTransferedSize(i);
        }
    }

    protected void stopErrorWithMessage(final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //these functions should be call on UI thread.
                stop();

                if (callback != null) {
                    callback.onError(msg);
                    callback.onAuditLog(msg);
                }
            }
        });
    }

    protected void stopSuccess(List<String> filesList){
        stop();

        if (callback != null){
            callback.onSuccess(filesList);
        }
    }

    protected void log(String msg){
        if (callback != null) {
            callback.onAuditLog(msg);
        }
    }


}
