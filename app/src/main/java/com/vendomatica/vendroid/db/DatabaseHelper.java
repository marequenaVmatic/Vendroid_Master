package com.vendomatica.vendroid.db;
/*
This is the database table design for the android sqlite.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.Fotos;
import com.vendomatica.vendroid.Model.PlantillaTipoTarea;
import com.vendomatica.vendroid.Model.TablasMaestras;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.Model.logEvents;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "VendroidDB.db";
	private final static int DB_VERSION = 17;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase db) {

		//tablet generic
		String strQuery;

		String strQueryGeneric = "CREATE TABLE IF NOT EXISTS TABLEGENERIC (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "TableName TEXT, "
				+ "ValueTable TEXT );";
		db.execSQL(strQueryGeneric);

		strQuery= "CREATE TABLE IF NOT EXISTS " + User.TABLENAME + " ("
				+ User.USERID + " TEXT, "
				+ User.USERLOGIN + " TEXT, "
				+ User.PASSWORD + " TEXT, "
				+ User.APEMAT + " TEXT, "
				+ User.APEPAT + " TEXT, "
				+ User.NOMBRE + " TEXT, "
				+ User.RUT + " TEXT, "
				+ User.MAIL + " TEXT, "
				+ User.TELEFONO+ " TEXT);";
		db.execSQL(strQuery);


		strQuery= "CREATE TABLE IF NOT EXISTS " + ConfigPantalla.TABLENAME + " ("
				+ ConfigPantalla.IDTIPOPANATLLA + " INTENGER, "
				+ ConfigPantalla.DESCTIPOPANATLLA + " TEXT, "
				+ ConfigPantalla.NOMBRETIPOPANATLLA + " TEXT, "
				+ ConfigPantalla.IDTIPOTAREA + " INTENGER, "
				+ ConfigPantalla.IDSUBTIPOTAREA + " INTENGER, "
				+ ConfigPantalla.IDCAMPO + " TEXT, "
				+ ConfigPantalla.NOMBRECAMPO + " TEXT, "
				+ ConfigPantalla.ORDERCARD+ " INTENGER, "
				+ ConfigPantalla.ACCIONCAMPO+ " TEXT,"
				+ ConfigPantalla.COLORPANTALLA+ " TEXT);";
		db.execSQL(strQuery);

		strQuery= "CREATE TABLE IF NOT EXISTS " + PlantillaTipoTarea.TABLENAME + " ("
				+ PlantillaTipoTarea.IDSUBTIPOTAREA + " INTENGER, "
				+ PlantillaTipoTarea.IDCCAMPO + " TEXT, "
				+ PlantillaTipoTarea.NOMBRECAMPO + " TEXT, "
				+ PlantillaTipoTarea.ORDERCARD+ " INTENGER, "
				+ PlantillaTipoTarea.IDTIPOPANTALLA+ " INTENGER, "
				+ PlantillaTipoTarea.ACCIONCAMPO+ " TEXT, "
				+ PlantillaTipoTarea.TIPOCAMPO+ " TEXT, "
				+ PlantillaTipoTarea.TABLACAMPO+ " TEXT);";
		db.execSQL(strQuery);


		strQuery = "CREATE TABLE IF NOT EXISTS " + Tarea.TABLENAME + " ("
				+ Tarea.IDTAREA + " INTEGER, "
				+ Tarea.IDTIPOTAREA + " INTEGER, "
				+ Tarea.IDSUBTIPOTAREA + " INTEGER, "
				+ Tarea.FECHAINICIO + " DATETIME, "
				+ Tarea.HORAINICIO + " DATETIME, "
				+ Tarea.FEHCAFIN + " DATETIME, "
				+ Tarea.FECHAINFO + " DATETIME, "
				+ Tarea.TIEMPOESTIMADO + " INTEGER, "
				+ Tarea.IDUSUARIO + " INTEGER, "
				+ Tarea.IDDOC + " INTEGER, "
				+ Tarea.IDRESOLUCION + " INTEGER, "
				+ Tarea.FECHAHORASINC + " DATETIME, "
				+ Tarea.NOREALIZADO + " TEXT, "
				+ Tarea.DESNOREALIZADO + " INTEGER, "
				+ Tarea.SYNCRO + " INTEGER, "
				+ Tarea.COMENTARIO + " TEXT,"
				+ Tarea.FOTOINI + " INTEGER,"
				+ Tarea.FOTOFIN + " INTEGER,"
				+ Tarea.WKF + " INTEGER);";
		db.execSQL(strQuery);

		strQuery = "CREATE TABLE IF NOT EXISTS " + TareaDetalle.TABLENAME + " ("
				+ TareaDetalle.IDTAREA + " INTEGER, "
				+ TareaDetalle.IDCCAMPO + " TEXT, "
				+ TareaDetalle.VALORCAMPO + " TEXT, "
				+ TareaDetalle.SYNCRO + " INTEGER);";
		db.execSQL(strQuery);

		strQuery = "CREATE TABLE IF NOT EXISTS " + TablasMaestras.TABLENAME + " ("
				+ TablasMaestras.IDTABLA + " INTEGER, "
				+ TablasMaestras.TABLA + " TEXT, "
				+ TablasMaestras.IDCAMPO + " TEXT, "
				+ TablasMaestras.NOMBRECAMPO + " TEXT, "
				+ TablasMaestras.VALORCAMPO + " TEXT, "
				+ TablasMaestras.TABLAPADRE + " TEXT, "
				+ TablasMaestras.IDCAMPOPADRE + " TEXT, "
				+ TablasMaestras.FILTRO2 + " TEXT, "
				+ TablasMaestras.IDCAMPOPADRE2 + " TEXT, "
				+ TablasMaestras.VIGENCIA + " INTEGER);";
		db.execSQL(strQuery);

		strQuery = "CREATE TABLE IF NOT EXISTS " + logEvents.TABLENAME + " ("
				+ logEvents.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ logEvents.USERID + " INTEGER, "
				+ logEvents.IDTAREA + " INTEGER, "
				+ logEvents.FECHA + " DATETIME, "
				+ logEvents.DESCRIPTION + " TEXT, "
				+ logEvents.LATITUDE + " TEXT, "
				+ logEvents.LONGITUDE + " TEXT, "
				+ logEvents.SYNCRO + " INTEGER);";
		db.execSQL(strQuery);

        strQuery = "CREATE TABLE IF NOT EXISTS " + Fotos.TABLENAME + " ("
                + Fotos.IDTAREA + " INTEGER, "
                + Fotos.NOMBREFOTO + " TEXT, "
                + Fotos.TIPO + " INTEGER, "
                + TareaDetalle.SYNCRO + " INTEGER);";
        db.execSQL(strQuery);
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Tarea.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + TareaDetalle.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + User.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + ConfigPantalla.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + PlantillaTipoTarea.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + TablasMaestras.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + logEvents.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + Fotos.TABLENAME);
		onCreate(db);
	}



}
