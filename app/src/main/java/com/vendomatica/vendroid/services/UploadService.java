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
import com.vendomatica.vendroid.Model.DetailCounter;
import com.vendomatica.vendroid.Model.LogEvent;
import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.PendingTasks;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;

import java.io.File;
import java.util.ArrayList;

public class UploadService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        postAllPendingTask();
        postAllTinPendingTask();
        postAllDetailCounters();
        postAllLogEvent();
        postAllLogFile();
        Common.getInstance().isUpload = false;
        mHandler_pendingtasks.sendEmptyMessage(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Falla con el Servicio de sincronizacion por favor verifique su internet", Toast.LENGTH_LONG).show();
    }



    //This function gets the log file data from the android sqlite db at first.
    //then in for instruction each log file is uploaded to the server.
    //NetworkManager postLogFile function is called.
    //after upload the log file, the log file from the android sqlite db and sdcard is deleted.
    private int postAllLogFile(){
        Toast.makeText(this, "Sincronizando Log", Toast.LENGTH_LONG).show();
        DBManager.setContext(getApplicationContext());
        ArrayList<LogFile> logs = DBManager.getManager().getLogFiles();
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogFile(logs.get(i));
            if (bRet1) {
                File f = new File(logs.get(i).getFilePath());
                if(f.exists())
                    f.delete();

                DBManager.getManager().deleteLogFile(logs.get(i));
            } else
                return 0;
        }
        return 1;
    }
    //This function gets the log event data from the android sqlite db at first.
    //then in for instruction each log event is uploaded to the server.
    //NetworkManager postLogevent function is called.
    //after upload the log event, the log event from the android sqlite db is deleted.
    private int postAllLogEvent(){
        Toast.makeText(this, "Sincronizando Event Log", Toast.LENGTH_LONG).show();
        ArrayList<LogEvent> logs = DBManager.getManager().getLogEvents(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogEvent(logs.get(i));
            if (bRet1)
                DBManager.getManager().deleteLogEvent(Common.getInstance().getLoginUser().getUserId(), logs.get(i).datetime);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the Pendingtask data from the android sqlite db at first.
    //then in for instruction each Pendingtask is uploaded to the server.
    //NetworkManager Pendingtask function is called.
    //after upload the Pendingtask, the Pendingtask from the android sqlite db is deleted.
    private int postAllPendingTask() {
        Toast.makeText(this, "Sincronizando Tareas Pendientes", Toast.LENGTH_LONG).show();
        DBManager.setContext(getApplicationContext());
        ArrayList<PendingTasks> tasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{"", "", "", "", ""};
            int nCurIndex = 0;
            if (!tasks.get(i).file1.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (!tasks.get(i).file2.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (!tasks.get(i).file3.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (!tasks.get(i).file4.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (!tasks.get(i).file5.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }

            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Aux_valor2, tasks.get(i).Aux_valor3, tasks.get(i).Aux_valor4, tasks.get(i).Aux_valor5, tasks.get(i).Glosa, arrPhotos, nCurIndex, tasks.get(i).Completed, tasks.get(i).Comment, tasks.get(i).Aux_valor6, tasks.get(i).QuantityResumen, tasks.get(i).comment_notcap);
            if (bRet1)
                DBManager.getManager().deletePendingTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the TinTask data from the android sqlite db at first.
    //then in for instruction each TinTask is uploaded to the server.
    //NetworkManager TinTask function is called.
    //after upload the TinTask, the TinTask from the android sqlite db is deleted.
    private int postAllTinPendingTask() {
        Toast.makeText(this, "Sincronizando tareas", Toast.LENGTH_LONG).show();
        DBManager.setContext(getApplicationContext());
        ArrayList<TinTask> tasks = DBManager.getManager().getTinPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postTinTask(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deletePendingTinTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the DetailCounter data from the android sqlite db at first.
    //then in for instruction each DetailCounter is uploaded to the server.
    //NetworkManager DetailCounter function is called.
    //after upload the DetailCounter, the DetailCounter from the android sqlite db is deleted.
    private int postAllDetailCounters() {
        Toast.makeText(this, "Sincronizando Detalle", Toast.LENGTH_LONG).show();
        DBManager.setContext(getApplicationContext());
        ArrayList<DetailCounter> tasks = DBManager.getManager().getDetailCounter();
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postDetailCounter(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deleteDetailTask(tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }




    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //loadTasks();
            Common.getInstance().isUpload = false;
        }
    };


}
