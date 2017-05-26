package com.vendomatica.vendroid.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.IBinder;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.Model.logEvents;
import com.vendomatica.vendroid.db.DBManager;

import java.util.Calendar;

public class LogService extends Service {



    public LogService() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int userid = 0;
        User muser  = Common.getInstance().getLoginUser();
        if(muser!=null) {
            userid = muser.userid;
            if (intent != null) {
                String idTarea = new String();
                idTarea = intent.getStringExtra("idTT");
                if (idTarea == null)
                    idTarea = "0";

                Calendar c = Calendar.getInstance();
                SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String formattedDate = datetime.format(c.getTime());
                String description = new String();
                description = intent.getStringExtra("description");
                String latitude = new String();
                latitude = Common.getInstance().latitude;
                String longitude = new String();
                longitude = Common.getInstance().longitude;
                logEvents log = new logEvents(0, userid, Integer.parseInt(idTarea), formattedDate, description, latitude, longitude, false);
                DBManager.getManager().insertLogEvents(log);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
