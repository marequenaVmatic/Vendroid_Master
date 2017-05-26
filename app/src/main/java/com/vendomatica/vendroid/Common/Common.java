package com.vendomatica.vendroid.Common;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.vendomatica.vendroid.Model.User;

import java.io.File;
import java.util.Timer;

/**
 * Created by Miguel Requena on 2017.
 * This has the global variables that uses in the app.
 */
public class Common {
    private static Common s_instance = null;

    public String gBatteryPercent;
    public boolean gChargingUSB;
    public boolean gChargingOther;

    public static Common getInstance() {
        if (s_instance == null) {
            s_instance = new Common();

        }
        synchronized (s_instance) {
            return s_instance;
        }
    }

    public final static String PREF_KEY_TEMPSAVE = "TEMPSAVE";
    public final static String PREF_KEY_USERDATA = "USERDATA";
    public final static String PREF_KEY_TEMPSAVE_ABASTEC = "TEMPSAVE::ABASTEC";
    public final static String PREF_KEY_TEMPSAVE_CONTADORES = "TEMPSAVE::CONTADORES";
    public final static String PREF_KEY_TEMPSAVE_RECAUDAR = "TEMPSAVE::RECAUDAR";

    public final static String PREF_KEY_CLOSEDTIME = "CLOSEDTIME";

    public final static String PREF_KEY_LATEST_LAT = "LATEST::LAT";
    public final static String PREF_KEY_LATEST_LNG = "LATEST::LNG";

    //WKF



    public String server_host = "http://vex.cl/Upload/";
    public boolean isUpload = false;
    public String latitude;
    public String longitude;
    public String signaturePath;
    public boolean isAbastec = false;
    public boolean isNeedRefresh = false;
    public boolean dayly = false;
    public String selectedNus;
    public String selectedQuantity;
    public boolean capture;
    public boolean mainAbastec = false;
    public boolean mainNoClick = false;
    public Timer mTimer;
    public Common()
    {
        gBatteryPercent = "Unknown";
        gChargingUSB = false;
        gChargingOther = false;
        isAbastec = false;
        mainAbastec = false;
        mainNoClick = false;
        mTimer = new Timer();
    }

    private User mUser;
    private Context mcontext;
    public final static int 		LEFTMENU_ANITIME = 250;

    public User getLoginUser() {
        User mUser = new User();
        return mUser.getObject(mUser);
    }

    public void setUser(User loginUser) {
        User mUser = new User();
        mUser.saveObject(loginUser);
        this.mUser = mUser.getObject(mUser);
    }



  public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    //actualizar cantidad de tareas.
    public void updateTab(Activity  context){
       /// View contentView = context.findViewById(android.R.id.content);
        //TabLayout tabs = (TabLayout)contentView.findViewById(R.id.tabs);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabs.getTabAt(0).setText("Pendientes ("+Common.getInstance().arrTickets.size()+")");
        //tabs.getTabAt(1).setText("Completados ("+Common.getInstance().arrTicketsCerrados.size()+")");
    }

    public Context getMcontext() {
        return mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public void CTRL(){


    }
}
