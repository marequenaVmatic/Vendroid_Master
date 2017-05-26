package com.vendomatica.vendroid.Model;

import java.util.Calendar;

/**
 * Created by Miguel Requena on 2017.
 */
public class TareaTipo {

    public final static String TABLENAME = "TareaTipo";
    public final static String  IDTIPOTAREA = "idTipoTarea";
    public final static String  DESCTIPOTAREA = "DescTipoTarea";



    public int idTipoTarea;
    public String DescTipoTarea;


    public TareaTipo() {
        Calendar c = Calendar.getInstance();

        this.idTipoTarea = 0;
        this.DescTipoTarea = "";
    }

    public TareaTipo(int sidTipoTarea, String sDescTipoTarea) {
        this.idTipoTarea = sidTipoTarea;
        this.DescTipoTarea = sDescTipoTarea;
    }
}
