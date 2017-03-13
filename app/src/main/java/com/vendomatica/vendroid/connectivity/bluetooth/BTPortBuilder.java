package com.vendomatica.vendroid.connectivity.bluetooth;


import android.bluetooth.BluetoothDevice;

public class BTPortBuilder {
    public static BTPortCommunication Build(String type, BluetoothDevice d){
/*
        if (MainActivity.DEBUG == 1)
            type = "SENA";
*/

        if (type.compareTo("SENA") == 0) {
            return new SenaBTCommunication(d);
        }else if (type.compareTo("Microchip") == 0) {
            return new MicrochipBTCommunication(d);
        }

        return null;
    }
}
