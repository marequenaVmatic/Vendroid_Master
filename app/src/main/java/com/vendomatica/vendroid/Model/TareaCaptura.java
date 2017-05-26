package com.vendomatica.vendroid.Model;

import java.util.Calendar;

/**
 * Created by Miguel Requena on 2017.
 */
public class TareaCaptura {

    public final static String TABLENAME = "TareaCaptura";
    public final static String  IDTAREA = "idTarea";
    public final static String  IDCCAMPO = "idCampo";
    public final static String  NOMBRECAMPO = "NombreCampo";
    public final static String  VALORCAMPO = "ValorCampo";
    public final static String SYNCRO = "sincro";



    public int idCampo;
    public String NombreCampo;
    public String ValorCampo;
    public boolean Syncro;


    public TareaCaptura() {
        Calendar c = Calendar.getInstance();

        this.idCampo = 0;
        this.NombreCampo = "";
        this.ValorCampo = "";
        this.Syncro = false;
    }

    public TareaCaptura(int sidCampo, String sNombreCampo, String sValorCampo, boolean sSyncro) {
        this.idCampo = sidCampo;
        this.NombreCampo = sNombreCampo;
        this.ValorCampo = sValorCampo;
        this.Syncro = sSyncro;
    }
}
