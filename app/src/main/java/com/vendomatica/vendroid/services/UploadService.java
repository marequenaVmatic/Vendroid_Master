package com.vendomatica.vendroid.services;
/*
This class uploads the completed task data in android service mode to the web server.
This class is for the auto sincronize in background after the user complete the tasks.
 */
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.vendomatica.vendroid.Common.Common;

public class UploadService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.


        Common.getInstance().isUpload = false;
        mHandler_pendingtasks.sendEmptyMessage(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Falla con el Servicio de sincronizacion por favor verifique su internet", Toast.LENGTH_LONG).show();
    }



    //This function gets the log event data from the android sqlite db at first.
    //then in for instruction each log event is uploaded to the server.
    //NetworkManager postLogevent function is called.
    //after upload the log event, the log event from the android sqlite db is deleted.


    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //loadTasks();
            Common.getInstance().isUpload = false;
        }
    };


}
