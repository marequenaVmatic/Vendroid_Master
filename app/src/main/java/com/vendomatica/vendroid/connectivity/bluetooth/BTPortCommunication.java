package com.vendomatica.vendroid.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


public abstract class BTPortCommunication {

    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mmSocket;
    private BufferedInputStream bufIn;
    private BufferedOutputStream bufOut;

    private final BluetoothDevice mBTDevice;

    public BTPortCommunication(BluetoothDevice device) {
        mBTDevice = device;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public boolean openSockets(){

        if (mBTDevice.getBondState() != BluetoothDevice.BOND_BONDED){
            return false;
        }

        mmSocket = getBTSocket();

        int idx = 0;
        do {
            try{
                idx++;
                mmSocket.connect();

                bufIn = new BufferedInputStream(mmSocket.getInputStream());
                bufOut = new BufferedOutputStream(mmSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }while(bufIn==null && bufOut==null && idx<5);

        return !(bufIn == null || bufOut == null);

    }

    abstract public void openPort(int baud, IOnBtOpenPort callback);



    public void closeSockets(){
        try {
            if (bufIn != null && mmSocket!= null) {
                 mmSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] bytes) throws IOException {
        bufOut.write(bytes);
        bufOut.flush();
    }

    public void write(byte b) throws IOException {
        bufOut.write(b);
        bufOut.flush();
    }

    public int read(byte[] buf, int offset, int count) throws IOException {

        if (bufIn.available() == 0)
            return -1;

        return bufIn.read(buf, offset, count);
    }

    public int read(byte[] buf, int count) throws IOException {

        if (bufIn.available() == 0)
            return -1;

        return bufIn.read(buf, 0, count);
    }

    public int available()throws IOException {
        if (bufIn != null)
            return bufIn.available();
        return 0;
    }


    private BluetoothSocket getBTSocket(){
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 10) {
                tmp = mBTDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } else {
                BluetoothDevice hxm = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mBTDevice.getAddress());
                Method m = hxm.getClass().getMethod("createRfcommSocket", int.class);
                tmp = (BluetoothSocket) m.invoke(hxm, 1);
            }

        } catch (IOException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return tmp;
    }

    public void openSocketsAsyncWithPause(final IOnBtOpenPort callback, final int timeout){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!openSockets()) {
                    callback.onBTOpenPortError();
                }else{
                    callback.onBTOpenPortDone();
                }

            }
        }).run();
    }


}