package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by Miguel Requena on 2017.
 */
public class Tabla implements Serializable {

    public final static String TABLENAME = "tb_tabla";
    public final static String IDCAMPO = "idCampo";
    public final static String VALORCAMPO = "ValorCampo";

    public String idCampo;
    public String ValorCampo;


    public Tabla() {
        this.idCampo = "";
        this.ValorCampo = "";
    }
    public Tabla(String midCampo, String mValorCampo)
    {
        this.idCampo = midCampo;
        this.ValorCampo = mValorCampo;
    }
}
