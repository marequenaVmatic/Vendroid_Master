package com.vendomatica.vendroid.db;
/*
	This file has the manage functions for the android sqlite database..
	for example, insert, get, delete operation about the android sqlite database.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.Fotos;
import com.vendomatica.vendroid.Model.PlantillaTipoTarea;
import com.vendomatica.vendroid.Model.Tabla;
import com.vendomatica.vendroid.Model.TablasMaestras;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.Model.logEvents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBManager {
	/*
	private Context mContext;
	public DBManager(Context context) {
		mContext = context;
	}*/
	//////shijin//////////
	private DatabaseHelper mDBHelper = null;

	/*public DBManager(Context context) {
		mDBHelper = new DatabaseHelper(context);
	}*/
	private DBManager(Context context) {
		if (mDBHelper == null) {
			mDBHelper = new DatabaseHelper(context.getApplicationContext());
		}
	}

	private static Context s_context;

	public static void setContext(Context context) {
		s_context = context.getApplicationContext();
	}

	private static DBManager s_instance = null;

	public static DBManager getManager() {
		if (s_instance == null) {
			s_instance = new DBManager(s_context);
		}
		synchronized (s_instance) {
			return s_instance;
		}
	}

	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
	SimpleDateFormat formatoLong = new SimpleDateFormat("EE MMM dd HH:mm:ss Z yyyy", Locale.US);
	//Insert function about the User class
	public long insertUser(User info) {
		ContentValues values = new ContentValues();
		values.put(User.USERID, info.userid);
        values.put(User.USERLOGIN, info.userlogin);
		values.put(User.PASSWORD, info.password);
		values.put(User.NOMBRE, info.nombre);
		values.put(User.APEPAT, info.apepat);
		values.put(User.APEMAT, info.apemat);
		values.put(User.TELEFONO, info.telefono);
		values.put(User.MAIL, info.mail);
		values.put(User.RUT, info.rut);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(User.TABLENAME, User.USERID + "=" +  info.userid, null);
		long lRet = db.insert(User.TABLENAME, null, values);
		if(lRet>0){
			for (int i = 0; i < info.pantallas.size(); i++) {
				insertPantalla(info.pantallas.get(i));
			}
		}
		return lRet;
	}

	//get function about User class from the android sqlite db.
	public User getUser(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		User info = new User();
		Cursor cursor = db.query(User.TABLENAME, new String[]{
				User.USERID,
				User.USERLOGIN,
				User.PASSWORD,
				User.NOMBRE,
				User.APEPAT,
				User.APEMAT,
				User.TELEFONO,
				User.MAIL,
				User.RUT
		}, User.USERID + "=" + userid, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			info.userid = cursor.getInt(0);
			info.userlogin = cursor.getString(1);
			info.password = cursor.getString(2);
			info.nombre = cursor.getString(3);
			info.apepat = cursor.getString(4);
			info.apemat = cursor.getString(5);
			info.telefono = cursor.getString(6);
			info.mail = cursor.getString(7);
			info.rut = cursor.getString(8);
			info.setPantallas(getPantalla());
			cursor.moveToNext();
		}


		cursor.close();
		//db.close();
		return info;
	}

	//get function about User class from the android sqlite db.
	public User getUserLogin(String username,String pwd) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		User info = new User();
		Cursor cursor = db.query(User.TABLENAME, new String[]{
				User.USERID,
				User.USERLOGIN,
				User.PASSWORD,
				User.NOMBRE,
				User.APEPAT,
				User.APEMAT,
				User.TELEFONO,
				User.MAIL,
				User.RUT
		}, User.USERLOGIN + "='" + username.toLowerCase() +"' AND "+User.PASSWORD.toLowerCase()+"='" + pwd +"'", null, null, null, null);
		//User.USERLOGIN + "='" + username +"' AND "+User.PASSWORD+"='" + pwd +"'"

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			info.userid = cursor.getInt(0);
			info.userlogin = cursor.getString(1);
			info.password = cursor.getString(2);
			info.nombre = cursor.getString(3);
			info.apepat = cursor.getString(4);
			info.apemat = cursor.getString(5);
			info.telefono = cursor.getString(6);
			info.mail = cursor.getString(7);
			info.rut = cursor.getString(8);
			info.setPantallas(getPantalla());
			cursor.moveToNext();
		}


		cursor.close();
		//db.close();
		return info;
	}
	//insert function about configPantalla class from the android sqlite db.
	public long insertPantalla(ConfigPantalla info) {
		ContentValues values = new ContentValues();
		values.put(ConfigPantalla.IDTIPOPANATLLA, info.idTipoPantalla);
		values.put(ConfigPantalla.DESCTIPOPANATLLA, info.DescTipoPantalla);
		values.put(ConfigPantalla.NOMBRETIPOPANATLLA, info.NombreTipoPantalla);
		values.put(ConfigPantalla.IDTIPOTAREA, info.idTipoTarea);
		values.put(ConfigPantalla.IDSUBTIPOTAREA, info.idSubTipoTarea);
		values.put(ConfigPantalla.IDCAMPO, info.idCampo);
		values.put(ConfigPantalla.NOMBRECAMPO, info.NombreCampo);
		values.put(ConfigPantalla.ORDERCARD, info.orderCard);
		values.put(ConfigPantalla.ACCIONCAMPO, info.accionCampo);
		values.put(ConfigPantalla.COLORPANTALLA, info.ColorPantalla);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(ConfigPantalla.TABLENAME, ConfigPantalla.IDTIPOPANATLLA + "=" +  info.IDTIPOPANATLLA + " and " + ConfigPantalla.IDCAMPO+ "='" +  info.IDCAMPO+"'", null);
		long lRet = db.insert(ConfigPantalla.TABLENAME, null, values);

		return lRet;
	}

	//insert function about configPantalla class from the android sqlite db.
	public long deletePantalla() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long lRet = db.delete(ConfigPantalla.TABLENAME, null, null);
		return lRet;
	}

	//get function about configPantalla class from the android sqlite db.
	public ArrayList<ConfigPantalla> getPantalla() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<ConfigPantalla> infos = new ArrayList<ConfigPantalla>();
		Cursor cursor = db.query(ConfigPantalla.TABLENAME, new String[]{
				ConfigPantalla.IDTIPOPANATLLA,
				ConfigPantalla.DESCTIPOPANATLLA,
				ConfigPantalla.NOMBRETIPOPANATLLA,
				ConfigPantalla.IDTIPOTAREA,
				ConfigPantalla.IDSUBTIPOTAREA,
				ConfigPantalla.IDCAMPO,
				ConfigPantalla.NOMBRECAMPO,
				ConfigPantalla.ORDERCARD,
				ConfigPantalla.ACCIONCAMPO,
				ConfigPantalla.COLORPANTALLA
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ConfigPantalla info = new ConfigPantalla();
			info.idTipoPantalla = cursor.getInt(0);
			info.DescTipoPantalla = cursor.getString(1);
			info.NombreTipoPantalla = cursor.getString(2);
			info.idTipoTarea = cursor.getInt(3);
			info.idSubTipoTarea = cursor.getInt(4);
			info.idCampo = cursor.getString(5);
			info.NombreCampo = cursor.getString(6);
			info.orderCard = cursor.getInt(7);
			info.accionCampo = cursor.getString(8);
			info.ColorPantalla = cursor.getString(9);
			infos.add(info);
			cursor.moveToNext();
		}


		cursor.close();
		//db.close();
		return infos;
	}

	//get function about configPantalla class from the android sqlite db.
	public ArrayList<ConfigPantalla> getPantallaTAB() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<ConfigPantalla> infos = new ArrayList<ConfigPantalla>();
		String selecet = "SELECT DISTINCT idTipoPantalla,DescTipoPantalla,NombreTipoPantalla,idTipoTarea,idSubTipoTarea,accionCampo,ColorPantalla FROM " + ConfigPantalla.TABLENAME + " WHERE DescTipoPantalla = 'TAB'" ;
		Cursor cursor =db.rawQuery(selecet, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ConfigPantalla info = new ConfigPantalla();
			info.idTipoPantalla = cursor.getInt(0);
			info.DescTipoPantalla = cursor.getString(1);
			info.NombreTipoPantalla = cursor.getString(2);
			info.idTipoTarea = cursor.getInt(3);
			info.idSubTipoTarea = cursor.getInt(4);
			info.accionCampo = cursor.getString(5);
			info.ColorPantalla =cursor.getString(6);
			info.idCampo = "";
			info.NombreCampo = "";

			info.orderCard = 0;
			infos.add(info);
			cursor.moveToNext();
		}


		cursor.close();
		//db.close();
		return infos;
	}


	//insert function about PlantillaTipoTarea class from the android sqlite db.
	public long insertPlantillaTipoTarea(PlantillaTipoTarea info) {
		ContentValues values = new ContentValues();
		values.put(PlantillaTipoTarea.IDSUBTIPOTAREA, info.idSubTipoTarea);
		values.put(PlantillaTipoTarea.IDCCAMPO, info.idCampo);
		values.put(PlantillaTipoTarea.NOMBRECAMPO, info.NombreCampo);
		values.put(PlantillaTipoTarea.ORDERCARD, info.orderCard);
		values.put(PlantillaTipoTarea.ACCIONCAMPO, info.accionCampo);
		values.put(PlantillaTipoTarea.IDTIPOPANTALLA, info.idTipoPantalla);
		values.put(PlantillaTipoTarea.TIPOCAMPO, info.tipoCampo);
		values.put(PlantillaTipoTarea.TABLACAMPO, info.tablaCampo);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long lRetDL = db.delete(PlantillaTipoTarea.TABLENAME, PlantillaTipoTarea.IDCCAMPO+ "='" +  info.idCampo + "' and " + PlantillaTipoTarea.IDTIPOPANTALLA+ "=" +  info.idTipoPantalla+" and " + PlantillaTipoTarea.IDSUBTIPOTAREA+ "=" +  info.idSubTipoTarea, null);
		long lRet = db.insert(PlantillaTipoTarea.TABLENAME, null, values);

		return lRet;
	}

	//get function about PLantilla class from the android sqlite db.
	public ArrayList<PlantillaTipoTarea> getPlantillaTT(int idSubTipoPantalla) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<PlantillaTipoTarea> infos = new ArrayList<PlantillaTipoTarea>();
		Cursor cursor = db.query(PlantillaTipoTarea.TABLENAME, new String[]{
				PlantillaTipoTarea.IDSUBTIPOTAREA,
				PlantillaTipoTarea.IDCCAMPO,
				PlantillaTipoTarea.NOMBRECAMPO,
				PlantillaTipoTarea.ORDERCARD,
				PlantillaTipoTarea.IDTIPOPANTALLA,
				PlantillaTipoTarea.ACCIONCAMPO,
				PlantillaTipoTarea.TIPOCAMPO,
				PlantillaTipoTarea.TABLACAMPO
		}, PlantillaTipoTarea.IDSUBTIPOTAREA +" = "+idSubTipoPantalla, null, null, null, null);
		cursor.moveToFirst();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PlantillaTipoTarea info = new PlantillaTipoTarea();
			info.idSubTipoTarea = cursor.getInt(0);
			info.idCampo = cursor.getString(1);
			info.NombreCampo = cursor.getString(2);
			info.orderCard = cursor.getInt(3);
			info.idTipoPantalla = cursor.getInt(4);
			info.accionCampo = cursor.getString(5);
			info.tipoCampo = cursor.getString(6);
			info.tablaCampo = cursor.getString(7);
			infos.add(info);
			cursor.moveToNext();
		}


		cursor.close();
		//db.close();
		return infos;
	}


	//insert function about Tareas class from the android sqlite db.
	public long insertTarea(Tarea info) {
		ContentValues values = new ContentValues();
		values.put(Tarea.IDTAREA, info.idTarea);
		values.put(Tarea.IDTIPOTAREA, info.idTipoTarea);
		values.put(Tarea.IDSUBTIPOTAREA, info.idSubTipoTarea);
		values.put(Tarea.FECHAINICIO, String.valueOf(info.FechaInicio));
		values.put(Tarea.HORAINICIO, String.valueOf(info.HoraInicio));
		values.put(Tarea.FEHCAFIN, String.valueOf(info.FechaFin));
		values.put(Tarea.FECHAINFO, String.valueOf(info.FechaInfo));
		values.put(Tarea.TIEMPOESTIMADO, info.TiempoEstimado);
		values.put(Tarea.IDUSUARIO, info.idUsuario);
		values.put(Tarea.IDDOC, info.id_doc);
		values.put(Tarea.IDRESOLUCION, info.idResolucion);
		values.put(Tarea.FECHAHORASINC, String.valueOf(info.FechaHoraSinc));
		values.put(Tarea.NOREALIZADO, info.NoRealizado);
		values.put(Tarea.DESNOREALIZADO, info.DescNoRealizado);
		values.put(Tarea.SYNCRO, false);
		values.put(Tarea.COMENTARIO, info.comentario);
        values.put(Tarea.FOTOINI, info.foto_ini);
        values.put(Tarea.FOTOFIN, info.foto_fin);
		values.put(Tarea.WKF, info.wkf);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Tarea.TABLENAME, Tarea.IDTAREA + "=" +  info.idTarea, null);
		long lRet = db.insert(Tarea.TABLENAME, null, values);
		if(lRet>0){
			for (int i = 0; i < info.Detalle.size(); i++) {
				insertTareaDetalle(info.Detalle.get(i));
			}
			if(info.Fotos!=null)
				for (int i = 0; i < info.Fotos.size(); i++) {
					insertTareaFoto(info.Fotos.get(i));
				}
		}

		return lRet;
	}

	public boolean setUpdateTareaClose(Tarea tarea){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ContentValues valores = new ContentValues();


		//db.update()
		Date fechafin = new Date();
		valores.put(Tarea.FEHCAFIN,fechafin.toString());
		valores.put(Tarea.IDSUBTIPOTAREA,tarea.idSubTipoTarea);
		int ret = db.update(Tarea.TABLENAME,valores,Tarea.IDTAREA+" = "+tarea.idTarea,null);
		return true;
	}

	public boolean setUpdateTareaSincro(Tarea tarea){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ContentValues valores = new ContentValues();


		//db.update()
		Date fechafin = new Date();
		valores.put(Tarea.SYNCRO,true);
		db.update(Tarea.TABLENAME,valores,Tarea.IDTAREA+" = "+tarea.idTarea,null);
		return true;
	}
	//insert function about Tareas class from the android sqlite db.
	public long DeleteTareas() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Tarea.TABLENAME,Tarea.FEHCAFIN + " = 'null'", null);
		//long lRet = db.insert(Tarea.TABLENAME, null, null);
		return 1;
		//return lRet;
	}

	//insert function about Tareas class from the android sqlite db.
	public ArrayList<Tarea> getTareas(int idsubtipotarea) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Tarea> infos = new ArrayList<Tarea>(); ///
		Cursor cursor = db.query(Tarea.TABLENAME, new String[]{
				Tarea.IDTAREA,
				Tarea.IDTIPOTAREA,
				Tarea.IDSUBTIPOTAREA,
				Tarea.FECHAINICIO,
				Tarea.HORAINICIO,
				Tarea.FEHCAFIN,
				Tarea.FECHAINFO,
				Tarea.TIEMPOESTIMADO,
				Tarea.IDUSUARIO,
				Tarea.IDDOC,
				Tarea.IDRESOLUCION,
				Tarea.FECHAHORASINC,
				Tarea.NOREALIZADO,
				Tarea.DESNOREALIZADO,
				Tarea.SYNCRO,
				Tarea.COMENTARIO,
                Tarea.FOTOINI,
                Tarea.FOTOFIN,
				Tarea.WKF
		}, "idResolucion=0 and idSubTipoTarea = " +idsubtipotarea , null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			try {
				Tarea info = new Tarea();
				info.idTarea = cursor.getInt(0);
				info.idTipoTarea = cursor.getInt(1);
				info.idSubTipoTarea = cursor.getInt(2);
				if(cursor.getString(3)!="null")
					info.FechaInicio =  formatoLong.parse(cursor.getString(3));
				if(cursor.getString(4)!="null")
					info.HoraInicio = formatoLong.parse(cursor.getString(4));
				String v = cursor.getString(5);
				info.FechaFin = null;
				info.FechaInfo = null;
				info.TiempoEstimado = cursor.getInt(7);
				info.idUsuario = cursor.getInt(8);
				info.id_doc = cursor.getInt(9);
				info.idResolucion = cursor.getInt(10);
				info.FechaHoraSinc = null;
				info.NoRealizado = cursor.getInt(12);
				info.DescNoRealizado = cursor.getString(13);
				info.Syncro = cursor.getInt(14)>0;
				info.comentario = cursor.getString(15);
                info.foto_ini = cursor.getInt(16)>0;
                info.foto_fin = cursor.getInt(17)>0;
				info.wkf = cursor.getInt(18)>0;
				info.setDetalle(getTareasDetalle(info.idTarea));
				infos.add(info);
			} catch (ParseException e) {
				e.printStackTrace();
			}


			cursor.moveToNext();
		}
		return infos;
	}

	public ArrayList<Tarea> getTareas2(int idsubtipotarea) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Tarea> infos = new ArrayList<Tarea>(); ///
		Cursor cursor = db.query(Tarea.TABLENAME, new String[]{
				Tarea.IDTAREA,
				Tarea.IDTIPOTAREA,
				Tarea.IDSUBTIPOTAREA,
				Tarea.FECHAINICIO,
				Tarea.HORAINICIO,
				Tarea.FEHCAFIN,
				Tarea.FECHAINFO,
				Tarea.TIEMPOESTIMADO,
				Tarea.IDUSUARIO,
				Tarea.IDDOC,
				Tarea.IDRESOLUCION,
				Tarea.FECHAHORASINC,
				Tarea.NOREALIZADO,
				Tarea.DESNOREALIZADO,
				Tarea.SYNCRO,
				Tarea.COMENTARIO
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			try {
				Tarea info = new Tarea();
				info.idTarea = cursor.getInt(0);
				info.idTipoTarea = cursor.getInt(1);
				info.idSubTipoTarea = cursor.getInt(2);
				if(!cursor.getString(3).equals("null"))
					info.FechaInicio =  formatoLong.parse(cursor.getString(3));
				if(!cursor.getString(4).equals("null"))
					info.HoraInicio = formatoLong.parse(cursor.getString(4));
				String v = cursor.getString(5);
				info.FechaFin = null;
				info.FechaInfo = null;
				info.TiempoEstimado = cursor.getInt(7);
				info.idUsuario = cursor.getInt(8);
				info.id_doc = cursor.getInt(9);
				info.idResolucion = cursor.getInt(10);
				info.FechaHoraSinc = null;
				info.NoRealizado = cursor.getInt(12);
				info.DescNoRealizado = cursor.getString(13);
				info.Syncro = cursor.getInt(14)>0;
				info.comentario = cursor.getString(15);
				info.setDetalle(getTareasDetalle(info.idTarea));
				infos.add(info);
			} catch (ParseException e) {
				e.printStackTrace();
			}


			cursor.moveToNext();
		}
		return infos;
	}


	//insert function about TareasDetalle class from the android sqlite db.
	public long insertTareaDetalle(TareaDetalle info) {
		ContentValues values = new ContentValues();
		values.put(TareaDetalle.IDTAREA, info.idTarea);
		values.put(TareaDetalle.IDCCAMPO, info.idCampo);
		values.put(TareaDetalle.VALORCAMPO, info.ValorCampo);
		values.put(TareaDetalle.SYNCRO,false);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TareaDetalle.TABLENAME, TareaDetalle.IDTAREA + "=" +  info.idTarea +" and "+TareaDetalle.IDCCAMPO+ "='" +  info.idCampo +"'" , null);
		long lRet = db.insert(TareaDetalle.TABLENAME, null, values);

		return lRet;
	}

	//insert function about Tareas class from the android sqlite db.
	public ArrayList<TareaDetalle> getTareasDetalle(int idTarea) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TareaDetalle> infos = new ArrayList<TareaDetalle>(); ///
		Cursor cursor = db.query(TareaDetalle.TABLENAME, new String[]{
				TareaDetalle.IDTAREA,
				TareaDetalle.IDCCAMPO,
				TareaDetalle.VALORCAMPO,
				TareaDetalle.SYNCRO
		}, TareaDetalle.IDTAREA + "=" +  idTarea, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TareaDetalle info = new TareaDetalle();
			info.idTarea = cursor.getInt(0);
			info.idCampo = cursor.getString(1);
			info.ValorCampo = cursor.getString(2);
			info.Syncro = cursor.getInt(3)>0;
			infos.add(info);
			cursor.moveToNext();
		}
		return infos;
	}


    //insert function about fotos class from the android sqlite db.
    public long insertTareaFoto(Fotos info) {
        ContentValues values = new ContentValues();
        values.put(Fotos.IDTAREA, info.idTarea);
        values.put(Fotos.NOMBREFOTO, info.fotoNombre);
        values.put(Fotos.TIPO, info.tipo);
        values.put(Fotos.SYNCRO,false);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(Fotos.TABLENAME, Fotos.IDTAREA + "=" +  info.idTarea +" and "+Fotos.NOMBREFOTO+ "='" +  info.fotoNombre +"'" , null);
        long lRet = db.insert(Fotos.TABLENAME, null, values);

        return lRet;
    }

    //insert function about Fotos class from the android sqlite db.
    public ArrayList<Fotos> getTareasFotos(int idTarea) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        ArrayList<Fotos> infos = new ArrayList<Fotos>(); ///
        Cursor cursor = db.query(Fotos.TABLENAME, new String[]{
                Fotos.IDTAREA,
                Fotos.NOMBREFOTO,
                Fotos.TIPO,
                Fotos.SYNCRO
        }, Fotos.IDTAREA + "=" +  idTarea, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Fotos info = new Fotos();
            info.idTarea = cursor.getInt(0);
            info.fotoNombre = cursor.getString(1);
            info.tipo = cursor.getInt(2);
            info.Syncro = cursor.getInt(3)>0;
            infos.add(info);
            cursor.moveToNext();
        }
        return infos;
    }

	public ArrayList<Fotos> getTareasFotosTipo(int idTarea,int Tipo) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Fotos> infos = new ArrayList<Fotos>(); ///
		Cursor cursor = db.query(Fotos.TABLENAME, new String[]{
				Fotos.IDTAREA,
				Fotos.NOMBREFOTO,
				Fotos.TIPO,
				Fotos.SYNCRO
		}, Fotos.IDTAREA + "=" +  idTarea + " and "+Fotos.TIPO + " = "+Tipo, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Fotos info = new Fotos();
			info.idTarea = cursor.getInt(0);
			info.fotoNombre = cursor.getString(1);
			info.tipo = cursor.getInt(2);
			info.Syncro = cursor.getInt(3)>0;
			infos.add(info);
			cursor.moveToNext();
		}
		return infos;
	}
    //insert function about TablasMaestra class from the android sqlite db.
	public long insertTablasMaestras(TablasMaestras info) {
		ContentValues values = new ContentValues();
		values.put(TablasMaestras.IDTABLA, info.idTabla);
		values.put(TablasMaestras.TABLA, info.Tabla);
		values.put(TablasMaestras.IDCAMPO, info.idCampo);
		values.put(TablasMaestras.NOMBRECAMPO, info.NombreCampo);
		values.put(TablasMaestras.VALORCAMPO, info.ValorCampo);
		values.put(TablasMaestras.TABLAPADRE, info.TablaPadre);
		values.put(TablasMaestras.IDCAMPOPADRE, info.idCampoPadre);
		values.put(TablasMaestras.FILTRO2, info.Filtro2);
		values.put(TablasMaestras.IDCAMPOPADRE2, info.idCampoPadre2);
		values.put(TablasMaestras.VIGENCIA,false);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TablasMaestras.TABLENAME, TablasMaestras.TABLA + "=" +  info.idTabla +" and "+TablasMaestras.IDCAMPO+ "='" +  info.idCampo +"'" , null);
		long lRet = db.insert(TablasMaestras.TABLENAME, null, values);

		return lRet;
	}

	//insert function about TablasMaestra class from the android sqlite db.
	public long DeleteTablasMaestras() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int lRet = db.delete(TablasMaestras.TABLENAME, null, null);
		return lRet;
	}

	//select function about TablaMaestras class from the android sqlite db.
	public ArrayList<Tabla> getTablas(String tabla,String tablapadre,  String idcampopadre,String  idcampopadre2) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Tabla> infos = new ArrayList<Tabla>(); ///
		String sqlwhere;
		sqlwhere = TablasMaestras.TABLA + "='" +  tabla +  "'";
		if(!idcampopadre.equals(""))
			sqlwhere +=  " and "+TablasMaestras.IDCAMPOPADRE + "='" +  idcampopadre+"'";
		if(!idcampopadre2.equals(""))
			sqlwhere +=  " and "+TablasMaestras.IDCAMPOPADRE2 + "='" +  idcampopadre2+"'";
		if(!tablapadre.equals(""))
			sqlwhere +=  " and "+TablasMaestras.TABLAPADRE + "='" +  tablapadre+"'";

		Cursor cursor = db.query(TablasMaestras.TABLENAME, new String[]{

				TablasMaestras.IDCAMPO,
				TablasMaestras.VALORCAMPO
		}, sqlwhere, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tabla info = new Tabla();
			info.idCampo = cursor.getString(0);
			info.ValorCampo = cursor.getString(1);
			infos.add(info);
			cursor.moveToNext();
		}
		return infos;
	}

	//insert function about TablasMaestra class from the android sqlite db.
	public long insertLogEvents(logEvents info) {
		ContentValues values = new ContentValues();
		values.put(logEvents.USERID, info.userid);
		values.put(logEvents.IDTAREA, info.idTarea);
		values.put(logEvents.FECHA, info.fecha.toString());
		values.put(logEvents.DESCRIPTION, info.description);
		values.put(logEvents.LATITUDE, info.latitude);
		values.put(logEvents.LONGITUDE, info.longitude);
		values.put(logEvents.SYNCRO,false);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long lRet = db.insert(logEvents.TABLENAME, null, values);

		return lRet;
	}

	public boolean setUpdatelogEvents(logEvents loge){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ContentValues valores = new ContentValues();
		//db.update()
		Date fechafin = new Date();
		valores.put(Tarea.SYNCRO,true);
		db.update(logEvents.TABLENAME,valores,logEvents.ID+" = "+loge.id,null);
		return true;
	}

	public long DeleteLogEvents() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int lRet = db.delete(logEvents.TABLENAME, logEvents.SYNCRO + " = 1", null);
		return lRet;
	}

	public ArrayList<logEvents> getLogEvents() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<logEvents> infos = new ArrayList<logEvents>(); ///
		String sqlwhere;
		sqlwhere = logEvents.SYNCRO+ "=0";
		Cursor cursor = db.query(logEvents.TABLENAME, new String[]{
				logEvents.ID,
				logEvents.USERID,
				logEvents.IDTAREA,
				logEvents.FECHA,
				logEvents.DESCRIPTION,
				logEvents.LATITUDE,
				logEvents.LONGITUDE,
				logEvents.SYNCRO
		}, sqlwhere, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			try {
				logEvents info = new logEvents();
				info.id = cursor.getInt(0);
				info.userid = cursor.getInt(1);
				info.idTarea = cursor.getInt(2);
				info.fecha = formatoLong.parse(cursor.getString(3));
				info.description = cursor.getString(4);
				info.latitude = cursor.getString(5);
				info.longitude = cursor.getString(6);
				info.syncro = cursor.getInt(7)>0;;
				infos.add(info);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			cursor.moveToNext();
		}
		return infos;
	}

	public long insertfoto(Fotos info) {
		ContentValues values = new ContentValues();
		values.put(Fotos.IDTAREA, info.idTarea);
		values.put(Fotos.NOMBREFOTO, info.fotoNombre);
		values.put(Fotos.TIPO, info.tipo);
		values.put(Fotos.SYNCRO, info.Syncro);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long lRet = db.insert(Fotos.TABLENAME, null, values);

		return lRet;
	}

	public boolean setUpdateFoto(Fotos fotos){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ContentValues valores = new ContentValues();
		//db.update()
		valores.put(Fotos.SYNCRO,true);
		db.update(Fotos.TABLENAME,valores,Fotos.IDTAREA+" = "+fotos.idTarea,null);
		return true;
	}

	public long DeleteFotos() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int lRet = db.delete(Fotos.TABLENAME, Fotos.SYNCRO + " = 1", null);
		return lRet;
	}

	public long DeleteFotosTarea(int idTarea) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int lRet = db.delete(Fotos.TABLENAME, Fotos.SYNCRO + " = 1 and "+Fotos.IDTAREA + " = "+idTarea , null);
		return lRet;
	}


	public ArrayList<Fotos> getFotos() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Fotos> infos = new ArrayList<Fotos>(); ///
		String sqlwhere;
		sqlwhere = Fotos.SYNCRO+ "=0";
		Cursor cursor = db.query(Fotos.TABLENAME, new String[]{
				Fotos.IDTAREA,
				Fotos.NOMBREFOTO,
				Fotos.TIPO,
				Fotos.SYNCRO
		}, sqlwhere, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			Fotos info = new Fotos();
			info.idTarea = cursor.getInt(0);
			info.fotoNombre = cursor.getString(1);
			info.tipo = cursor.getInt(2);
			info.Syncro = cursor.getInt(3)>0;;
			infos.add(info);

			cursor.moveToNext();
		}
		return infos;
	}

}