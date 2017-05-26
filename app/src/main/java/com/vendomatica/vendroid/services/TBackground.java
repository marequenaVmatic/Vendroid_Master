package com.vendomatica.vendroid.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.net.Sincro;

/**
 * Created by miguel_r on 05-04-2017.
 */

 public class TBackground extends AsyncTask<Void, Integer, Boolean> {

        public Context mcontext;
        private int count=0;
          private NotificationManager manager;
    private int typeError;


    private ComponentName mService;

          private int NOTIF_REF = 1;
        @Override
        protected Boolean doInBackground(Void... params) {

            //sendNotify();
            //sendPsition();
            String existAbast = "";

                syncro();

            return true;
        }
    //private void  sendPsition() {
    //    setService("CTRL RUTA");
    //}
        private void  sendNotify(){

            String existAbast = " - ";
            Integer Cantd=0;



            if(Cantd>0) {
                Intent intent = new Intent(mcontext, MainActivity.class);
                //intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("SYNCRO",true);
                PendingIntent piDismiss = PendingIntent.getActivity(
                        mcontext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(mcontext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(
                                        mcontext.getResources(),
                                        R.mipmap.ic_launcher
                                        )
                                )
                                .setContentTitle("Vendroid")
                                .setContentText("Maquinas Pendientes por sincronizar. Vendroid!!")
                                .setColor(mcontext.getResources().getColor(R.color.clr_orange))
                                .setStyle(
                                        new NotificationCompat.BigTextStyle()
                                                .bigText("Maquinas Pendientes por sincronizar. Vendroid!!"))
                                .addAction(R.drawable.back_row,
                                        "SINCRONIZAR", piDismiss)
                                .setAutoCancel(true);
                mBuilder.setContentIntent(piDismiss);
                Notification notification = mBuilder.build();

                manager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIF_REF++, notification);
            }
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

    private boolean getConnectivityWifi() {
        ConnectivityManager cm = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return false;
        }
        return false;
    }

    private void syncro(){

            if (getConnectivityStatus()) {
                Sincro.getManager().setContext(mcontext);
                Sincro.getManager().SincronizarUpload();
                User muser = new User();
                muser = muser.getObject(muser);
                Sincro.getManager().SincronizarDownload(muser,false);

            } else {
            }
    }

    private Runnable mRunnabletasks = new Runnable() {
        @Override
        public void run() {

            Sincro.getManager().setContext(mcontext);
            Sincro.getManager().SincronizarUpload();

        }
    };

    //This function gets the DetailCounter data from the android sqlite db at first.
    //then in for instruction each DetailCounter is uploaded to the server.
    //NetworkManager DetailCounter function is called.
    //after upload the DetailCounter, the DetailCounter from the android sqlite db is deleted.


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