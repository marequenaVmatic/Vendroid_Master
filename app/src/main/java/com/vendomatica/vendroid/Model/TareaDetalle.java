package com.vendomatica.vendroid.Model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Miguel Requena on 2017.
 */
public class TareaDetalle implements Serializable {

    public final static String TABLENAME = "TareaDetalle";
    public final static String  IDTAREA = "idTarea";
    public final static String  IDCCAMPO = "idCampo";
    public final static String  VALORCAMPO = "ValorCampo";
    public final static String SYNCRO = "sincro";


    public  int idTarea;
    public  String idCampo;
    public  String ValorCampo;
    public  boolean Syncro;


    public TareaDetalle() {
        Calendar c = Calendar.getInstance();
        this.idTarea = 0;
        this.idCampo = "";
        this.ValorCampo = "";
        this.Syncro = false;
    }

    public TareaDetalle(int sidTarea,String sidCampo,String sValorCampo,boolean sSyncro) {
        this.idTarea = sidTarea;
        this.idCampo = sidCampo;
        this.ValorCampo = sValorCampo;
        this.Syncro = sSyncro;
    }
}
