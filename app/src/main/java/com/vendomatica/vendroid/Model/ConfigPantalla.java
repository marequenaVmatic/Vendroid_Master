package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by Miguel Requena on 2017.
 */
public class ConfigPantalla  implements Serializable {

    public final static String TABLENAME = "tb_pantallas";
    public final static String IDTIPOPANATLLA = "idTipoPantalla";
    public final static String DESCTIPOPANATLLA  = "DescTipoPantalla";
    public final static String NOMBRETIPOPANATLLA  = "NombreTipoPantalla";
    public final static String COLORPANTALLA  = "colorPantalla";
    public final static String IDTIPOTAREA = "idTipoTarea";
    public final static String IDSUBTIPOTAREA = "idSubTipoTarea";
    public final static String IDCAMPO = "idCampo";
    public final static String NOMBRECAMPO = "NombreCampo";
    public final static String ORDERCARD = "orderCard";
    public final static String ACCIONCAMPO = "accionCampo";

    public int idTipoPantalla;
    public String DescTipoPantalla;
    public String NombreTipoPantalla;
    public int idTipoTarea;
    public int idSubTipoTarea;
    public String idCampo;
    public String NombreCampo;
    public int orderCard;
    public String accionCampo;
    public String ColorPantalla;


    public ConfigPantalla()
    {
        this.idTipoPantalla=0;
        this.DescTipoPantalla= "";
        this.NombreTipoPantalla= "";
        this.idTipoTarea = 0;
        this.idSubTipoTarea = 0;
        this.idCampo = "";
        this.NombreCampo = "";
        this.orderCard = 0;
        this.accionCampo = "";
        this.ColorPantalla = "";
    }
    public ConfigPantalla(int midTipoPantalla,String mDescTipoPantalla,String mNombreTipoPantalla,int midTipoTarea,int midSubTipoTarea,String midCampo,String mNombreCampo,int morderCard, String maccionCampo, String mColorPantalla)
    {
        this.idTipoPantalla=midTipoPantalla;
        this.DescTipoPantalla= mDescTipoPantalla;
        this.NombreTipoPantalla= mNombreTipoPantalla;
        this.idTipoTarea = midTipoTarea;
        this.idSubTipoTarea = midSubTipoTarea;
        this.idCampo = midCampo;
        this.NombreCampo = mNombreCampo;
        this.orderCard = morderCard;
        this.accionCampo = maccionCampo;
        this.ColorPantalla = mColorPantalla;
    }
}

