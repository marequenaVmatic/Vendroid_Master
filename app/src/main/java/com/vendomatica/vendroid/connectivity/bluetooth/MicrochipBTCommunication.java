package com.vendomatica.vendroid.connectivity.bluetooth;

import android.bluetooth.BluetoothDevice;

public class MicrochipBTCommunication extends BTPortCommunication{
    public MicrochipBTCommunication(BluetoothDevice device) {
        super(device);
    }

    @Override
    public void openPort(int baud, IOnBtOpenPort callback) {

    }
}
