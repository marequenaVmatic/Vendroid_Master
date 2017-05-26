package com.vendomatica.vendroid.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Miguel Requena on 2017.
 */
public class Tarea implements Serializable {

    public final static String TABLENAME = "Tareas";
    public final static String  IDTAREA = "idTarea";
    public final static String  IDTIPOTAREA = "idTipoTarea";
    public final static String  IDSUBTIPOTAREA= "idSubTipoTarea";
    public final static String  FECHAINICIO = "FechaInicio";
    public final static String  HORAINICIO = "HoraInicio";
    public final static String  FEHCAFIN = "FechaFin";
    public final static String  FECHAINFO = "FechaInfo";
    public final static String  TIEMPOESTIMADO = "TiempoEstimado";
    public final static String  IDUSUARIO = "idUsuario";
    public final static String  IDDOC = "id_doc";
    public final static String  IDRESOLUCION = "idresolucion";
    public final static String  FECHAHORASINC = "FechaHoraSinc";
    public final static String  NOREALIZADO = "NoRealizado";
    public final static String  DESNOREALIZADO = "DescNoRealizado";
    public final static String SYNCRO = "sincro";
    public final static String COMENTARIO = "comentario";
    public final static String FOTOINI = "foto_ini";
    public final static String FOTOFIN = "foto_fin";
    public final static String WKF = "wkf";

    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public  int idTarea;
    public  int idTipoTarea;
    public  int idSubTipoTarea;
    public  java.util.Date FechaInicio;
    public  java.util.Date HoraInicio;
    public  Date FechaFin;
    public  Date FechaInfo;
    public  int TiempoEstimado;
    public  int idUsuario;
    public  int id_doc;
    public  int idResolucion;
    public  Date FechaHoraSinc;
    public  int NoRealizado;
    public  String  DescNoRealizado;
    public  boolean Syncro;
    public  String  comentario;
    public  boolean foto_ini;
    public  boolean foto_fin;
    public  boolean wkf;
    public   ArrayList<TareaDetalle> Detalle;
    public   ArrayList<Fotos> Fotos;


    public Tarea() {
        Calendar c = Calendar.getInstance();

        this.idTarea = 0;
        this.idTipoTarea = 0;
        this.idSubTipoTarea = 0;
        this.FechaInicio = null;
        this.HoraInicio = null;
        this.FechaFin = null;
        this.FechaInfo = null;
        this.TiempoEstimado = 0;
        this.idUsuario = 0;
        this.id_doc = 0;
        this.idResolucion = 0;
        this.FechaHoraSinc = null;
        this.NoRealizado = 0;
        this.DescNoRealizado = "";
        this.Syncro = false;
        this.comentario = "";
        this.foto_ini=false;
        this.foto_fin=false;
        this.wkf=true;
        this.Detalle = new ArrayList<TareaDetalle>();
        this.Fotos = new ArrayList<Fotos>();
    }

    public Tarea(int sidTarea,int sidTipoTarea,int sidSubTipoTarea,String  sFechaInicio,String sHoraInicio,String sFechaFin,String sFechaInfo,int sTiempoEstimado,int sidUsuario,int sid_doc,int sidResolucion,String sFechaHoraSinc,int sNoRealizado,String  sDescNoRealizado, boolean sSyncro, String scomentario,boolean sfoto_ini ,boolean sfoto_fin, boolean swkf) {

        try {
            this.idTarea = sidTarea;
            this.idTipoTarea = sidTipoTarea;
            this.idSubTipoTarea = sidSubTipoTarea;
            this.FechaInicio = formato.parse(sFechaInicio);
            this.HoraInicio = formato.parse(sHoraInicio);
            this.FechaFin = formato.parse(sFechaFin);
            this.FechaInfo = formato.parse(sFechaInfo);
            this.TiempoEstimado = sTiempoEstimado;
            this.idUsuario = sidUsuario;
            this.id_doc = sid_doc;
            this.idResolucion = sidResolucion;
            this.FechaHoraSinc = formato.parse(sFechaHoraSinc);
            this.NoRealizado = sNoRealizado;
            this.DescNoRealizado = sDescNoRealizado;
            this.Syncro = sSyncro;
            this.comentario = scomentario;
            this.foto_ini = sfoto_ini;
            this.foto_fin = sfoto_fin;
            this.wkf = swkf;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Tarea(JSONObject jsonObject)
    {
        try {
            this.idTarea = jsonObject.getInt("idTarea");
            this.idTipoTarea = jsonObject.getInt("idTipoTarea");
            this.idSubTipoTarea = jsonObject.getInt("idSubTipoTarea");
            if(jsonObject.getString("FechaInicio")!="null")
                this.FechaInicio = formato.parse(jsonObject.getString("FechaInicio"));
            if(jsonObject.getString("HoraInicio")!="null")
                this.HoraInicio = formato.parse(jsonObject.getString("HoraInicio"));
            if(jsonObject.getString("FechaFin")!="null")
                this.FechaFin = formato.parse(jsonObject.getString("FechaFin"));
            if(jsonObject.getString("FechaInfo")!="null")
                this.FechaInfo = formato.parse(jsonObject.getString("FechaInfo"));
            this.TiempoEstimado = jsonObject.getInt("TiempoEstimado");
            this.idUsuario = jsonObject.getInt("idUsuario");
            this.id_doc = jsonObject.getInt("id_doc");
            this.idResolucion = (jsonObject.getString("idResolucion")!="null"?jsonObject.getInt("idResolucion"):0);
            if(jsonObject.getString("FechaHoraSinc")!="null")
                this.FechaHoraSinc = formato.parse(jsonObject.getString("FechaHoraSinc"));
            this.NoRealizado = jsonObject.getInt("NoRealizado");
            this.DescNoRealizado = jsonObject.getString("DescNoRealizado");
            this.Syncro = false;
            if(!jsonObject.getString("Comentario").equals("null"))
                this.comentario = jsonObject.getString("Comentario");
            this.foto_ini = Boolean.parseBoolean(jsonObject.getString("foto_ini"));
            this.foto_fin = Boolean.parseBoolean(jsonObject.getString("foto_fin"));
            this.wkf = Boolean.parseBoolean(jsonObject.getString("wkf"));

            // Detalle
            JSONArray mDetalleTarea  =jsonObject.getJSONArray("DetalleTarea");
            ArrayList<TareaDetalle> lstDetalle = new ArrayList<TareaDetalle>();
            for (int i = 0; i < mDetalleTarea.length(); i++) {
                JSONObject jsonObject1 = mDetalleTarea.getJSONObject(i);
                TareaDetalle nDet = new TareaDetalle();
                nDet.idTarea = jsonObject1.getInt("idTarea");
                nDet.idCampo = jsonObject1.getString("idCampo");
                nDet.ValorCampo = jsonObject1.getString("ValorCampo");
                nDet.Syncro =false;
                lstDetalle.add(nDet);
            }
            this.setDetalle(lstDetalle);

            // Fotos
            if(!jsonObject.getString("Fotos").equals("null")) {
                JSONArray mFotos = jsonObject.getJSONArray("Fotos");
                ArrayList<Fotos> lstFotos = new ArrayList<Fotos>();
                for (int i = 0; i < mFotos.length(); i++) {
                    JSONObject jsonObject1 = mFotos.getJSONObject(i);
                    Fotos nFot = new Fotos();
                    nFot.idTarea = jsonObject1.getInt("idTarea");
                    nFot.fotoNombre = jsonObject1.getString("nombreFoto");
                    nFot.tipo = jsonObject1.getInt("tipo");
                    nFot.Syncro = true;
                    lstFotos.add(nFot);
                }
                this.setFotos(lstFotos);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void setDetalle(ArrayList<TareaDetalle> mDetalle){
        this.Detalle=mDetalle;
    }

    public void setFotos(ArrayList<Fotos> mFotos){
        this.Fotos=mFotos;
    }
}
