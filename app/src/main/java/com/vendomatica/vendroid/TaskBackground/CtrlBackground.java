package com.vendomatica.vendroid.TaskBackground;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.vendomatica.vendroid.services.LogService;

/**
 * Created by miguel_r on 05-04-2017.
 */

 public class CtrlBackground extends AsyncTask<Void, Integer, Boolean> {

    public Context mcontext;
    private int count = 0;
    private NotificationManager manager;
    private int typeError;


    private ComponentName mService;

    private int NOTIF_REF = 1;

    @Override
    protected Boolean doInBackground(Void... params) {
        sendPsition();
        return true;
    }



    private void sendPsition() {
        setService("CTRL RUTA");
    }



    public void setService(String description) {


        Intent service = new Intent(mcontext, LogService.class);
        service.putExtra("idTarea", "0");
        service.putExtra("description", description);
        mService = mcontext.startService(service);
    }

    private boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
        //int progreso = values[0].intValue();

        //pbarProgreso.setProgress(progreso);
    }

    @Override
    protected void onPreExecute() {
        //pbarProgreso.setMax(100);
        //pbarProgreso.setProgress(0);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //if(result)
        // Toast.makeText(MainHilos.this, "Tarea finalizada!",
        //          Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        //Toast.makeText(MainHilos.this, "Tarea cancelada!",
        //        Toast.LENGTH_SHORT).show();
    }
}