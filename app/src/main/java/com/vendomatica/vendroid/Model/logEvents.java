package com.vendomatica.vendroid.Model;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Miguel Requena on 2017.
 */
public class logEvents {

    public final static String TABLENAME = "log_event";
    public final static String ID = "id";
    public final static String USERID = "userid";
    public final static String IDTAREA = "idTarea";
    public final static String FECHA = "fecha";
    public final static String DESCRIPTION = "description";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String SYNCRO = "sincro";

    public int id;
    public int userid;
    public int idTarea;
    public Date fecha;
    public String description;
    public String latitude;
    public String longitude;
    public boolean syncro;

    java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    public logEvents()
    {
        this.id = 0;
        this.userid = 0;
        this.idTarea = 0;
        this.fecha = null;
        this.description = "";
        this.latitude = "";
        this.longitude = "";
        this.syncro = false;
    }
    public logEvents(int mid, int muserid, int midTarea, String mfecha, String description, String latitude, String longitude, boolean mSyncro)
    {

        try {
            this.id = mid;
            this.userid = muserid;
            this.idTarea = midTarea;
            this.fecha = formato.parse(mfecha);
            this.description = description;
            this.latitude = latitude;
            this.longitude = longitude;
            this.syncro = mSyncro;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
