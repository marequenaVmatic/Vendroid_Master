package com.vendomatica.vendroid.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Miguel Requena on 2017.
 */
public class PlantillaTipoTarea implements Serializable {

    public final static String TABLENAME = "PlantillaTipoTarea";
    public final static String  IDSUBTIPOTAREA = "idSubTipoTarea";
    public final static String  IDCCAMPO = "idCampo";
    public final static String  NOMBRECAMPO = "NombreCampo";
    public final static String  ORDERCARD = "orderCard";
    public final static String  IDTIPOPANTALLA = "idTipoPantalla";
    public final static String ACCIONCAMPO = "AccionCampo";
    public final static String TIPOCAMPO = "TipoCampo";
    public final static String TABLACAMPO = "TablaCampo";


    public int idSubTipoTarea;
    public String idCampo;
    public String NombreCampo;
    public int orderCard;
    public int idTipoPantalla;
    public String accionCampo;
    public String tipoCampo;
    public String tablaCampo;


    public PlantillaTipoTarea() {
        this.idSubTipoTarea = 0;
        this.idCampo = "";
        this.NombreCampo = "";
        this.orderCard = 0;
        this.idTipoPantalla = 0;
        this.accionCampo = "";
        this.tipoCampo = "";
        this.tablaCampo = "";
    }

    public PlantillaTipoTarea(int midSubTipoTarea,String midCampo, String mNombreCampo, int morderCard, int midTipoPantalla ,String maccionCampo,String mtipoCampo,String mtablaCampo) {
        this.idSubTipoTarea = midSubTipoTarea;
        this.idCampo = midCampo;
        this.NombreCampo = mNombreCampo;
        this.orderCard = morderCard;
        this.idTipoPantalla = midTipoPantalla;
        this.accionCampo = maccionCampo;
        this.tipoCampo = mtipoCampo;
        this.tablaCampo = mtablaCampo;
    }

    public PlantillaTipoTarea(JSONObject jsonObject)
    {
        try {
            this.idSubTipoTarea =jsonObject.getInt("idSubTipoTarea");
            this.idCampo = jsonObject.getString("idCampo");
            this.NombreCampo = jsonObject.getString("NombreCampo");
            this.orderCard = jsonObject.getInt("orderCard");
            this.idTipoPantalla = jsonObject.getInt("idTipoPantalla");
            this.accionCampo = jsonObject.getString("AccionCampo");
            this.tipoCampo = jsonObject.getString("TipoCampo");
            this.tablaCampo = jsonObject.getString("TablaCampo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
