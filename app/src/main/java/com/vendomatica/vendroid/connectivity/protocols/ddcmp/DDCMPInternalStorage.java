package com.vendomatica.vendroid.connectivity.protocols.ddcmp;

import android.os.Handler;

public class DDCMPInternalStorage {
        private static DDCMPInternalStorage instance;

        public static DDCMPInternalStorage getInstance(){
            if (instance == null)
                instance = new DDCMPInternalStorage();

            return instance;
        }


        public Handler handler;
}
