package com.vendomatica.vendroid.connectivity.protocols;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.CircularArray;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.bluetooth.BTPortCommunication;

import java.io.IOException;
import java.util.Arrays;

public abstract class Communication {

    public Communication(){
        backgroundThread.start();
    }

    protected final CircularArray<Byte> buf = new CircularArray<>();

    private boolean mStop = false;
    //Thread safe method to send data between threads.
    Thread backgroundThread = new Thread(new Runnable() {
        @Override
        public void run() {
            final BTPortCommunication btPort = ReferencesStorage.getInstance().btPort;

            final int B_SIZE = 1024;
            byte data[] = new byte[B_SIZE];

            while (!mStop){
                try {
                    while (btPort.available()!=0) {
                        final int count = btPort.read(data, B_SIZE);
                        sendMessage(data, count);
                        //Log.d("Rx", Integer.toString(count));
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    mStop = true;
                }
                Thread.yield();
            }
        }


        private void sendMessage(byte[] data, int count){
            if (count!=0 && data != null){
                android.os.Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putByteArray("Data", Arrays.copyOf(data, count));
                b.putInt("Length", count);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }


    });

    public void setStop(boolean flag){
        mStop = flag;
    }

    public boolean isStop(){
        return mStop;
    }

    private final Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            byte[] d = msg.getData().getByteArray("Data");
            int length = msg.getData().getInt("Length");

            if (d != null && length != 0){
                //Cool we have some data :)
                //put it into the big storage and call parser :)
                synchronized (buf) {
                    for (int i = 0; i < length; i++) {
                        //bigDataBuffer[bigDataCounter+i] = d[i];
                        buf.addLast(d[i]);
                    }
                    //bigDataCounter += length;
                }

                onData();
            }
        }
    };

    abstract public void onData();

    abstract public void write(byte[] data);

}
