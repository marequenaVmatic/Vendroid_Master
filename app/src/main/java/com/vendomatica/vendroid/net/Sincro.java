package com.vendomatica.vendroid.net;
/*
	This file has the manage functions for the android sqlite database..
	for example, insert, get, delete operation about the android sqlite database.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vendomatica.vendroid.Model.Fotos;
import com.vendomatica.vendroid.Model.PlantillaTipoTarea;
import com.vendomatica.vendroid.Model.TablasMaestras;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.Model.logEvents;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;

public class Sincro {
	/*
	private Context mContext;
	public DBManager(Context context) {
		mContext = context;
	}*/
	//////shijin//////////
	private DatabaseHelper mDBHelper = null;

	private static Context s_context;

	private static ConnectivityManager manager;

	public static boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager) s_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
	}

	public static void setContext(Context context) {
		s_context = context.getApplicationContext();
	}

	private static Sincro s_instance = null;

	public static Sincro getManager() {
		if (s_instance == null) {
			s_instance = new Sincro(s_context);
		}
		synchronized (s_instance) {
			return s_instance;
		}
	}

	private Sincro(Context context) {

	}

	public boolean SincronizarDownload(User muser,boolean backgrondT) {
		//Toast.makeText(s_context, "Iniciando Sincronización", Toast.LENGTH_LONG).show();


		try {
			if (isOnline()) {

                if(NetworkManager.getManager().getActive()) {
                    DBManager.setContext(s_context);

                    if (muser.pantallas.size() > 0)
                        DBManager.getManager().deletePantalla();
                    DBManager.getManager().insertUser(muser);
                    ArrayList<Tarea> LstTareas = new ArrayList<Tarea>();
                    NetworkManager.getManager().loadTareas(LstTareas, muser);
                    DBManager.getManager().DeleteTareas();
                    if (LstTareas.size() > 0) {
                        for (int j = 0; j < LstTareas.size(); j++) {
                            Tarea TT = new Tarea();
                            TT = LstTareas.get(j);
                            if (TT.idTarea > 0)
                                DBManager.getManager().insertTarea(TT);
                        }
                    }

                    ArrayList<TablasMaestras> lstTablas = new ArrayList<TablasMaestras>();
                    NetworkManager.getManager().loadTablas(lstTablas);

                    if (lstTablas.size() > 0) {
                        DBManager.getManager().DeleteTablasMaestras();
                        for (int j = 0; j < lstTablas.size(); j++) {
                            TablasMaestras TT = new TablasMaestras();
                            TT = lstTablas.get(j);
                            DBManager.getManager().insertTablasMaestras(TT);
                        }
                    }

                    ArrayList<PlantillaTipoTarea> lstPlantillas = new ArrayList<PlantillaTipoTarea>();
                    NetworkManager.getManager().loadPlantillas(lstPlantillas);

                    if (lstPlantillas.size() > 0) {
                        for (int j = 0; j < lstPlantillas.size(); j++) {
                            PlantillaTipoTarea TT = new PlantillaTipoTarea();
                            TT = lstPlantillas.get(j);
                            DBManager.getManager().insertPlantillaTipoTarea(TT);
                        }
                    }

                }
				//DBManager.getManager().deleteAllticketsCerrados();
				//comentado mientras traemos de base de datos los completados.
			}
			//Common.getInstance().arrTickets.addAll(DBManager.getManager().getAlltickets());
		}
		catch (Exception ex){

			//	Toast.makeText(s_context, "Falla al Sincronizar", Toast.LENGTH_LONG).show();
			return false;
		}
		//Toast.makeText(s_context, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
		return true;
	}

	public boolean SincronizarUpload() {
		//Toast.makeText(s_context, "Iniciando Sincronización", Toast.LENGTH_LONG).show();
		try {
			if (isOnline()) {
				ArrayList<Tarea> tareas  = new ArrayList<Tarea>();
				tareas = DBManager.getManager().getTareas(2);
				for(Tarea TT : tareas){
					if(!TT.Syncro)
						NetworkManager.getManager().UploadTarea(TT);
				}
				ArrayList<logEvents> logs  = new ArrayList<logEvents>();
				logs = DBManager.getManager().getLogEvents();
				for(logEvents TT : logs){
					if(!TT.syncro)
						NetworkManager.getManager().UploadLogEvent(TT);
				}
			}
		}
		catch (Exception ex){

			//	Toast.makeText(s_context, "Falla al Sincronizar", Toast.LENGTH_LONG).show();
			return false;
		}
		//Toast.makeText(s_context, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
		return true;
	}

	public boolean SincronizarFiles() {
		//Toast.makeText(s_context, "Iniciando Sincronización", Toast.LENGTH_LONG).show();
		try {
			if (isOnline()) {
				ArrayList<Fotos> fotos  = new ArrayList<Fotos>();
				fotos = DBManager.getManager().getFotos();
				for(Fotos FF : fotos){
					if(!FF.Syncro) {
						String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + FF.idTarea + ".jpg";
						if(NetworkManager.getManager().uploadFileMro(timeStamp, FF.fotoNombre))
						    DBManager.getManager().setUpdateFoto(FF);
					}
				}
			}
		}
		catch (Exception ex){

			//	Toast.makeText(s_context, "Falla al Sincronizar", Toast.LENGTH_LONG).show();
			return false;
		}
		//Toast.makeText(s_context, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
		return true;
	}

}