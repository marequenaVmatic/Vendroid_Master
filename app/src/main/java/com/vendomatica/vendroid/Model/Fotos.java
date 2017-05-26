package com.vendomatica.vendroid.Model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Miguel Requena on 2017.
 */
public class Fotos implements Serializable {

    public final static String TABLENAME = "Fotos";
    public final static String  IDTAREA = "idTarea";
    public final static String  NOMBREFOTO = "fotoNobmre";
    public final static String  TIPO = "tipo";
    public final static String SYNCRO = "sincro";


    public  int idTarea;
    public  String fotoNombre;
    public  int tipo;
    public  boolean Syncro;


    public Fotos() {
        Calendar c = Calendar.getInstance();
        this.idTarea = 0;
        this.fotoNombre = "";
        this.tipo = 0;
        this.Syncro = false;
    }

    public Fotos(int sidTarea, String sfotonombre, int sTipo, boolean sSyncro) {
        this.idTarea = sidTarea;
        this.fotoNombre = sfotonombre;
        this.tipo = sTipo;
        this.Syncro = sSyncro;
    }
}
