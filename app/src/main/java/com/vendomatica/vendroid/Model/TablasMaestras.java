package com.vendomatica.vendroid.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Miguel Requena on 2017.
 */
public class TablasMaestras implements Serializable {

    public final static String TABLENAME = "tb_maestras";
    public final static String IDTABLA = "idTabla";
    public final static String TABLA = "Tabla";
    public final static String IDCAMPO = "idCampo";
    public final static String NOMBRECAMPO = "NombreCampo";
    public final static String VALORCAMPO = "ValorCampo";
    public final static String TABLAPADRE = "TablaPadre";
    public final static String IDCAMPOPADRE = "idCampoPadre";
    public final static String FILTRO2 = "Filtro2";
    public final static String IDCAMPOPADRE2 = "idCampoPadre2";
    public final static String VIGENCIA = "Vigencia";

    public int idTabla;
    public String Tabla;
    public String idCampo;
    public String NombreCampo;
    public String ValorCampo;
    public String TablaPadre;
    public String idCampoPadre;
    public String Filtro2;
    public String idCampoPadre2;
    public boolean Vigencia;


    public TablasMaestras() {
        this.idTabla = 0;
        this.Tabla = "";
        this.idCampo = "";
        this.NombreCampo = "";
        this.ValorCampo = "";
        this.TablaPadre = "";
        this.idCampoPadre = "";
        this.Filtro2 = "";
        this.idCampoPadre2 = "";
        this.Vigencia = true;
    }
    public TablasMaestras(int midTabla,String mTabla,String midCampo,String mNombreCampo,String mValorCampo,String mTablaPadre,String midCampoPadre,String mFiltro2,String midCampoPadre2,boolean mVigencia)
    {
        this.idTabla = midTabla;
        this.Tabla = mTabla;
        this.idCampo = midCampo;
        this.NombreCampo = mNombreCampo;
        this.ValorCampo = mValorCampo;
        this.TablaPadre = mTablaPadre;
        this.idCampoPadre = midCampoPadre;
        this.Filtro2 = mFiltro2;
        this.idCampoPadre2 = midCampoPadre2;
        this.Vigencia = mVigencia;
    }

    public TablasMaestras(JSONObject jsonObject)
    {
        try {
            this.idTabla = jsonObject.getInt("idTabla");
            this.Tabla = jsonObject.getString("Tabla");
            this.idCampo = jsonObject.getString("idCampo");
            this.NombreCampo = jsonObject.getString("NombreCampo");
            this.ValorCampo = jsonObject.getString("ValorCampo");
            this.TablaPadre = jsonObject.getString("TablaPadre");
            this.idCampoPadre = jsonObject.getString("idCampoPadre");
            this.Filtro2 = jsonObject.getString("Filtro2");
            this.idCampoPadre2 = jsonObject.getString("idCampoPadre2");
            this.Vigencia = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
