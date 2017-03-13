package com.vendomatica.vendroid.Common;
/*
    This activity is for base work in app. For example, menu and actionbar.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vendomatica.vendroid.services.LogService;
import com.vendomatica.vendroid.MainActivity;
//import com.vendomatica.vendroid.MapActivity;
import com.vendomatica.vendroid.Model.LogEvent;
import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.PendingTasks;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.ReportActivity;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends FragmentActivity implements OnClickListener {
    protected Context mContext;
    private LinearLayout mLnBackAni, mLnSlideMenu;
    private int mSideMenuWidth = 0;
    android.content.SharedPreferences.Editor ed;
    SharedPreferences sp;
    private ComponentName mService;
    private Timer mDaylyTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        mProgDlg = new ProgressDialog(mContext);
        mProgDlg.setCancelable(true);
        mProgDlg.setTitle("Please");
        mProgDlg.setMessage("Waiting");
        mProgDlgLoading = new ProgressDialog(this);
        mProgDlgLoading.setCancelable(false);
        mProgDlgLoading.setTitle("Sincronize");
        mProgDlgLoading.setMessage("Loading Now!");
        sp = getSharedPreferences("userinfo", 1);
        ed = sp.edit();
    }

    private ProgressDialog mProgDlg;
    private ProgressDialog mProgDlgLoading;


    private boolean mIsShowSlideMenu = false;
    private ValueAnimator mSlideAnimator = null;

    public void toggleSlideMenu() {
        mIsShowSlideMenu = !mIsShowSlideMenu;
        mSlideAnimator.cancel();
        mSlideAnimator.start();
        mLnSlideMenu.setVisibility(mIsShowSlideMenu ? View.VISIBLE : View.GONE);
        mLnBackAni.setVisibility(mIsShowSlideMenu ? View.VISIBLE : View.GONE);
    }

    protected void showToast(int iResId) {
        Toast.makeText(mContext, iResId, Toast.LENGTH_LONG).show();
    }

    protected void showToast(String strMsg) {
        Toast.makeText(mContext, strMsg, Toast.LENGTH_LONG).show();
    }
    private void setService(String description){
        Intent service = new Intent(mContext, LogService.class);
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
        service.putExtra("taskid", "");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }
    private  boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }
//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.btnSlideMenu:
//            case R.id.lnBackAni:
//                toggleSlideMenu();
//                break;
//            case R.id.lnMenuSincronize:
//                toggleSlideMenu();
//                setService("The user clicks the Sincronize button");
//                if (getConnectivityStatus()) {
//                    boolean repeat = true;
//                    mProgDlgLoading.show();
//                    while(repeat){
//                        if(Common.getInstance().isUpload == false) {
//                            //Toast.makeText(mContext, "hihi", Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        try{
//                            Thread.sleep(1000);
//                        }catch (Throwable a){
//
//                        }
//                    }
//                    new Thread(mRunnable_pendingtasks).start();
//                } else
//                    Toast.makeText(mContext, "The network is not available now!!!", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.lnMenuDayly:
//                toggleSlideMenu();
//                if(Common.getInstance().arrTickets.size() == 0 && Common.getInstance().mainAbastec == true && Common.getInstance().mainNoClick == true){
//                    showCompleteDialog();
//                }
//                break;
//            case R.id.lnMenuMaps:
//                toggleSlideMenu();
//                setService("The user clicks the Google map button");
//                if (getConnectivityStatus()) {
//                    intent = new Intent(mContext, MapActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
//                }else
//                    Toast.makeText(mContext, "The network is not available now!!!", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.lnMenuReport:
//                toggleSlideMenu();
//                intent = new Intent(mContext, ReportActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                break;
//            case R.id.lnMenuLogout:
//                toggleSlideMenu();
//                ed.putBoolean("login", false);
//                ed.commit();
//                if(Common.getInstance().mTimer != null){
//                    Common.getInstance().mTimer.cancel();
//                    Common.getInstance().mTimer = null;
//                }
//                Intent intent_logout = new Intent(mContext, LoginActivity.class);
//                intent_logout.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent_logout);
//                break;
//        }
//    }
    private void showCompleteDialog(){
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,2000);
        if(mDaylyTimer != null) {
            mDaylyTimer.cancel();
            mDaylyTimer = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("CIERRE DIARIO")
                .setMessage("Marque SI para realizar descarga de datos en SGV y generar pedido. Marque NO en caso que desee re abastecer una maquina.")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(getConnectivityStatus()) {
                            Common.getInstance().mainNoClick = false;
                            boolean repeat = true;
                            while (repeat) {
                                if (Common.getInstance().isUpload == false) {
                                    break;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (Throwable a) {

                                }
                            }
                            mProgDlg.show();
                            new Thread(mDaylyRunnable).start();
                        }else{
                            dialog.cancel();
                            showFailDialog();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Common.getInstance().mainNoClick = true;
                        mDaylyTimer = new Timer();
                        mDaylyTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mHandler_time.sendEmptyMessage(0);
                            }
                        }, 600000, 600000);
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private Runnable mDaylyRunnable = new Runnable() {
        @Override
        public void run() {
            int ret1 = postAllPendingTask();
            int ret2 = postAllTinPendingTask();
            int ret3 = postAllLogEvents();
            int ret4 = postAllLogFile();
            if(ret1 == 1 && ret2 == 1 && ret3 == 1 && ret4 == 1) {
                boolean ret = NetworkManager.getManager().postDayly(Common.getInstance().getLoginUser().getUserId());
                mDaylyHandler.sendEmptyMessage(0);
            }else{
                mDaylyHandler.sendEmptyMessage(-1);
            }
        }
    };
    private Handler mDaylyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgDlg.hide();
            if(mDaylyTimer != null){
                mDaylyTimer.cancel();
                mDaylyTimer = null;
            }
            if(msg.what == -1)
                showFailDialog();
            else
                showConfirmDialog();
        }
    };
    private void showFailDialog(){
        Common.getInstance().mainNoClick = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        builder.setTitle("Sincronizacion fallo.")
                .setMessage("Por favor revise su conexion a internet.")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mDaylyTimer != null) {
                            mDaylyTimer.cancel();
                            mDaylyTimer = null;
                        }
                        mDaylyTimer = new Timer();
                        mDaylyTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mHandler_time.sendEmptyMessage(0);
                            }
                        }, 600000, 600000);
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private Handler mHandler_time = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //Check the result from the nutrition api.
            if (msg.what == 0) {
                showCompleteDialog();
            }
        }

    };
    private void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        builder.setTitle("Sincronizacion success.")
                .setMessage("Sincronizacion realizada exitosamente!!!")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Common.getInstance().dayly = true;
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Runnable mRunnable_pendingtasks = new Runnable() {
        @Override
        public void run() {
            postAllPendingTask();
            postAllTinPendingTask();
            postAllLogEvents();
            postAllLogFile();
            mHandler_pendingtasks.sendEmptyMessage(1);

        }
    };
    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            mProgDlg.hide();
            if (msg.what == 1) {
                Toast.makeText(mContext, "Pending Tasks was uploaded!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else if (msg.what == 0) {
                Toast.makeText(mContext, "Pending Tasks was failed to upload!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            }
        }
    };
    private void loadTasks() {
        //mProgDlgLoading.show();
        new Thread(mRunnable_tasks).start();
    }
    private Runnable mRunnable_tasks = new Runnable() {

        @Override
        public void run() {

            Common.getInstance().arrTickets.clear();
            Common.getInstance().arrCompleteTasks.clear();
            Common.getInstance().arrCompleteTinTasks.clear();
            Common.getInstance().arrCategory.clear();
            Common.getInstance().arrProducto.clear();
            Common.getInstance().arrProducto_Ruta.clear();
            Common.getInstance().arrUsers.clear();
            Common.getInstance().arrTaskTypes.clear();
            Common.getInstance().arrMachineCounters.clear();
            Common.getInstance().arrCompleteDetailCounters.clear();
            Common.getInstance().arrCommentErrors.clear();

            int nRet = NetworkManager.getManager().loadPublicArray(Common.getInstance().arrTickets);
            //NetworkManager.getManager().loadCategory(Common.getInstance().arrCategory, Common.getInstance().arrProducto, Common.getInstance().arrProducto_Ruta, Common.getInstance().arrUsers, Common.getInstance().arrTaskTypes);
            //NetworkManager.getManager().loadMachine(Common.getInstance().arrMachineCounters);
            DBManager.getManager().deleteAlltickets();

            DBManager.getManager().insertTickets(Common.getInstance().arrTickets);


            //NetworkManager.getManager().loadProducto(Common.getInstance().arrProducto);
            //Common.getInstance().arrPendingTasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
            //Common.getInstance().arrTinTasks = DBManager.getManager().getTinPendingTask(Common.getInstance().getLoginUser().getUserId());
            mHandler_task.sendEmptyMessage(nRet);
        }
    };
    private Handler mHandler_task = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlgLoading.hide();
            if (msg.what == 0) {
                Toast.makeText(mContext, "Load Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            } else if (msg.what == 1) {
                Toast.makeText(mContext, "Load failed!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(mContext, "Load failed due to network problem! Please check your network status", Toast.LENGTH_SHORT).show();
            }
            //setTaskNumber();
        }
    };
    private int postAllPendingTask() {
        ArrayList<PendingTasks> tasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{null, null, null, null, null};
            int nCurIndex = 0;
            if (tasks.get(i).file1 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (tasks.get(i).file2 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (tasks.get(i).file3 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (tasks.get(i).file4 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (tasks.get(i).file5 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }
            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Aux_valor2, tasks.get(i).Aux_valor3, tasks.get(i).Aux_valor4, tasks.get(i).Aux_valor5, tasks.get(i).Glosa, arrPhotos, nCurIndex, tasks.get(i).Completed, tasks.get(i).Comment, tasks.get(i).Aux_valor6, tasks.get(i).QuantityResumen, tasks.get(i).comment_notcap);
            if (!bRet1)
                return 0;
            DBManager.getManager().deletePendingTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
        }
        return 1;
    }
    private int postAllLogFile(){
        ArrayList<LogFile> logs = DBManager.getManager().getLogFiles();
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogFile(logs.get(i));
            if (bRet1)
                DBManager.getManager().deleteLogFile(logs.get(i));
            else
                return 0;
        }
        return 1;
    }
    private int postAllLogEvents() {
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

    private int postAllTinPendingTask() {
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

    @Override
    public void onClick(View v) {

    }
}
