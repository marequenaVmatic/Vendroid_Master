package com.vendomatica.vendroid.connectivity.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class SenaBTCommunication extends BTPortCommunication{

    private final byte[] AT_COMMAND = "+++".getBytes();
    private final byte[] AT_PASSWORD = "AT+PASS=0000\r".getBytes();
    private final byte[] AT_SPEED_BEG = "AT+UARTCONFIG,".getBytes();
    private final byte[] AT_SPEED_END = ",N,1\r".getBytes();
    private final byte[] AT_RESTART = "ATZ\r".getBytes();
    private Handler handler = new Handler();

    public SenaBTCommunication(BluetoothDevice device){
        super(device);
    }


    //This function is async
    public void openPort(int baud, final IOnBtOpenPort callback) {
        final int baudRate = baud;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!openSockets()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBTOpenPortError();
                        }
                    });
                    return;
                }

                final byte[] buf = new byte[300];

                try {
                    //TODO: change readnum to sleep
                    write(AT_COMMAND);
                    readNum(buf, 22);
                    Log.d("AT_COMMAND:", new String(buf));

                    write(AT_PASSWORD);
                    readNum(buf, 29);
                    Log.d("AT_PASSWORD:", new String(buf));

                    write(AT_SPEED_BEG);
                    write(Integer.toString(baudRate).getBytes());
                    write(AT_SPEED_END);
                    readNum(buf, 8);
                    Log.d("AT_SPEED:", new String(buf));

                    write(AT_RESTART);
                    closeSockets();
                    Thread.sleep(5000);

                    Log.d("SenaBTCommunication", "ReOpenSocket");
                    if (!openSockets()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onBTOpenPortError();
                            }
                        });
                        return;
                    }
                    Log.d("SenaBTCommunication", "SocketOpened");
                }catch (Exception e){
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBTOpenPortError();
                        }
                    });
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onBTOpenPortDone();
                    }
                });
            }
        }).start();
    }




    /*blocking request!*/
    public void readNum(byte[] d, int num) throws IOException {
        int n = 0;
        do{
            n = available();
        }while (n<num);

        read(d, num);
    }

}

