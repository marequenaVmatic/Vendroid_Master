package com.vendomatica.vendroid.db;
/*
This is the database table design for the android sqlite.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vendomatica.vendroid.Model.Category;
import com.vendomatica.vendroid.Model.CommentError;
import com.vendomatica.vendroid.Model.CompleteDetailCounter;
import com.vendomatica.vendroid.Model.CompleteTask;
import com.vendomatica.vendroid.Model.CompltedTinTask;
import com.vendomatica.vendroid.Model.DetailCounter;
import com.vendomatica.vendroid.Model.LogEvent;
import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.MachineCounter;
import com.vendomatica.vendroid.Model.PendingTasks;
import com.vendomatica.vendroid.Model.Producto;
import com.vendomatica.vendroid.Model.Producto_RutaAbastecimento;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.Model.TaskType;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.Model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "VendroidWkf.db";
	private final static int DB_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase db) {

		//tablet generic
		String strQueryGeneric = "CREATE TABLE IF NOT EXISTS TABLEGENERIC (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "TableName TEXT, "
				+ "ValueTable TEXT );";
		db.execSQL(strQueryGeneric);


		// TODO Auto-generated method stub
		String strQuery = "CREATE TABLE IF NOT EXISTS " + CompleteTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CompleteTask.USERID + " TEXT, "
				+ CompleteTask.TASKID + " INTEGER, "
				+ CompleteTask.DATE + " TEXT, "
				+ CompleteTask.TASKTYPE + " TEXT, "
				+ CompleteTask.RUTAABASTEIMIENTO + " TEXT, "
				+ CompleteTask.TASKBUSINESSKEY + " TEXT, "
				+ CompleteTask.CUSTOMER + " TEXT, "
				+ CompleteTask.ADRESS + " TEXT, "
				+ CompleteTask.LOCATIONDESC + " TEXT, "
				+ CompleteTask.MODEL + " TEXT, "
				+ CompleteTask.LATITUDE + " TEXT, "
				+ CompleteTask.LONGITUDE + " TEXT, "
				+ CompleteTask.EPV + " TEXT, "
				+ CompleteTask.LOGLATITUDE + " TEXT, "
				+ CompleteTask.LOGLONGITUDE + " TEXT, "
				+ CompleteTask.ACTIONDATE + " TEXT, "
				+ CompleteTask.FILE1 + " TEXT, "
				+ CompleteTask.FILE2 + " TEXT, "
				+ CompleteTask.FILE3 + " TEXT, "
				+ CompleteTask.FILE4 + " TEXT, "
				+ CompleteTask.FILE5 + " TEXT, "
				+ CompleteTask.MACHINETYPE + " TEXT, "
				+ CompleteTask.SIGNATURE + " TEXT, "
				+ CompleteTask.NUMEROGUIA + " TEXT, "
				+ CompleteTask.GLOSA + " TEXT, "
				+ CompleteTask.AUX_VALOR1 + " TEXT, "
				+ CompleteTask.AUX_VALOR2 + " TEXT, "
				+ CompleteTask.AUX_VALOR3 + " TEXT, "
				+ CompleteTask.AUX_VALOR4 + " TEXT, "
				+ CompleteTask.AUX_VALOR5 + " TEXT, "
				+ CompleteTask.COMPLETED + " INTEGER, "
				+ CompleteTask.COMMENT + " TEXT, "
				+ CompleteTask.AUX_VALOR6 + " TEXT, "
				+ CompleteTask.QUANTITYRESUMEN + " INTEGER, "
				+ CompleteTask.Comment_Notcap + " TEXT);";
		db.execSQL(strQuery);

		String strQueryTin = "CREATE TABLE IF NOT EXISTS " + TinTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TinTask.USERID + " TEXT, "
				+ TinTask.TASKID + " INTEGER, "
				+ TinTask.TASKTYPE + " TEXT, "
				+ TinTask.RUTAABASTEIMIENTO + " TEXT, "
				+ TinTask.CUS + " TEXT, "
				+ TinTask.NUS + " TEXT, "
				+ TinTask.QUANTITY + " TEXT);";
		db.execSQL(strQueryTin);

		String strQueryCompleteTin = "CREATE TABLE IF NOT EXISTS " + CompltedTinTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CompltedTinTask.USERID + " TEXT, "
				+ CompltedTinTask.TASKID + " INTEGER, "
				+ CompltedTinTask.TASKTYPE + " TEXT, "
				+ CompltedTinTask.RUTAABASTEIMIENTO + " TEXT, "
				+ CompltedTinTask.CUS + " TEXT, "
				+ CompltedTinTask.NUS + " TEXT, "
				+ CompltedTinTask.QUANTITY + " TEXT);";
		db.execSQL(strQueryCompleteTin);

		String strQueryLog = "CREATE TABLE IF NOT EXISTS " + LogEvent.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LogEvent.USERID + " TEXT, "
				+ LogEvent.TASKID + " TEXT, "
				+ LogEvent.DATETIME + " TEXT, "
				+ LogEvent.DESCRIPTION + " TEXT, "
				+ LogEvent.LATITUDE + " TEXT, "
				+ LogEvent.LONGITUDE + " TEXT, "
				+ LogEvent.BATTERY_LEVEL + " TEXT, "
				+ LogEvent.PHONE_FREESPACE + " TEXT, "
				+ LogEvent.IS_CHARGING_USB + " INTEGER, "
				+ LogEvent.IS_CHARGING_OTHER + " INTEGER);";
		db.execSQL(strQueryLog);

		String strQueryIncomplete = "CREATE TABLE IF NOT EXISTS " + TaskInfo.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TaskInfo.USERID + " TEXT, "
				+ TaskInfo.TASKID + " INTEGER, "
				+ TaskInfo.DATE + " TEXT, "
				+ TaskInfo.TASKTYPE + " TEXT, "
				+ TaskInfo.RUATABASTECIMIENTO + " TEXT, "
				+ TaskInfo.TASKBUSINESSKEY + " TEXT, "
				+ TaskInfo.CUSTOMER + " TEXT, "
				+ TaskInfo.ADRESS + " TEXT, "
				+ TaskInfo.LOCATIONDESC + " TEXT, "
				+ TaskInfo.MODEL + " TEXT, "
				+ TaskInfo.LATITUDE + " TEXT, "
				+ TaskInfo.LONGITUDE + " TEXT, "
				+ TaskInfo.EPV + " TEXT, "
				+ TaskInfo.DISTANCE + " TEXT, "
				+ TaskInfo.MACHINETYPE + " TEXT, "
				+ TaskInfo.AUX_VALOR1 + " TEXT, "
				+ TaskInfo.AUX_VALOR2 + " TEXT, "
				+ TaskInfo.AUX_VALOR3 + " TEXT, "
				+ TaskInfo.AUX_VALOR4 + " TEXT, "
				+ TaskInfo.AUX_VALOR5 + " TEXT, "
				+ TaskInfo.AUX_VALOR6 + " TEXT);";
		db.execSQL(strQueryIncomplete);

		String strQueryPending = "CREATE TABLE IF NOT EXISTS " + PendingTasks.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PendingTasks.USERID + " TEXT, "
				+ PendingTasks.TASKID + " INTEGER, "
				+ PendingTasks.DATE + " TEXT, "
				+ PendingTasks.TASKTYPE + " TEXT, "
				+ PendingTasks.RUTAABASTEIMIENTO + " TEXT, "
				+ PendingTasks.TASKBUSINESSKEY + " TEXT, "
				+ PendingTasks.CUSTOMER + " TEXT, "
				+ PendingTasks.ADRESS + " TEXT, "
				+ PendingTasks.LOCATIONDESC + " TEXT, "
				+ PendingTasks.MODEL + " TEXT, "
				+ PendingTasks.LATITUDE + " TEXT, "
				+ PendingTasks.LONGITUDE + " TEXT, "
				+ PendingTasks.EPV + " TEXT, "
				+ PendingTasks.LOGLATITUDE + " TEXT, "
				+ PendingTasks.LOGLONGITUDE + " TEXT, "
				+ PendingTasks.ACTIONDATE + " TEXT, "
				+ PendingTasks.FILE1 + " TEXT, "
				+ PendingTasks.FILE2 + " TEXT, "
				+ PendingTasks.FILE3 + " TEXT, "
				+ PendingTasks.FILE4 + " TEXT, "
				+ PendingTasks.FILE5 + " TEXT, "
				+ PendingTasks.MACHINETYPE + " TEXT, "
				+ PendingTasks.SIGNATURE + " TEXT, "
				+ PendingTasks.NUMEROGUIA + " TEXT, "
				+ PendingTasks.GLOSA + " TEXT, "
				+ PendingTasks.AUX_VALOR1 + " TEXT, "
				+ PendingTasks.AUX_VALOR2 + " TEXT, "
				+ PendingTasks.AUX_VALOR3 + " TEXT, "
				+ PendingTasks.AUX_VALOR4 + " TEXT, "
				+ PendingTasks.AUX_VALOR5 + " TEXT, "
				+ PendingTasks.COMPLETED + " INTEGER, "
				+ PendingTasks.COMMENT + " TEXT, "
				+ PendingTasks.AUX_VALOR6 + " TEXT, "
				+ PendingTasks.QUANTITYRESUMEN + " INTEGER, "
				+ PendingTasks.Comment_Notcap + " TEXT);";

		db.execSQL(strQueryPending);

		String strQueryUser = "CREATE TABLE IF NOT EXISTS " + User.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ User.USERID + " TEXT, "
				+ User.PASSWORD + " TEXT, "
				+ User.FIRSTNAME + " TEXT, "
				+ User.LASTNAME + " TEXT);";
		db.execSQL(strQueryUser);

		String strQueryProducto = "CREATE TABLE IF NOT EXISTS " + Producto.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Producto.CUS + " TEXT, "
				+ Producto.NUS + " TEXT);";
		db.execSQL(strQueryProducto);

		String strQueryProductoRuta = "CREATE TABLE IF NOT EXISTS " + Producto_RutaAbastecimento.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Producto_RutaAbastecimento.TASKTYPE + " TEXT, "
				+ Producto_RutaAbastecimento.TASKBUSINESSKEY + " TEXT, "
				+ Producto_RutaAbastecimento.RUTAABASTECIMENTO + " TEXT, "
				+ Producto_RutaAbastecimento.CUS + " TEXT);";
		db.execSQL(strQueryProductoRuta);

		String strQueryCategory = "CREATE TABLE IF NOT EXISTS " + Category.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Category.CATEGORY + " TEXT);";
		db.execSQL(strQueryCategory);

		String strQuerytype = "CREATE TABLE IF NOT EXISTS " + TaskType.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TaskType.TYPE + " TEXT, "
				+ TaskType.NAME + " TEXT);";
		db.execSQL(strQuerytype);

		String strQueryLogFile = "CREATE TABLE IF NOT EXISTS " + LogFile.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LogFile.TASKID + " INTEGER, "
				+ LogFile.FILE_PATH + " TEXT, "
				+ LogFile.FILE_TYPE + " TEXT, "
				+ LogFile.CAPTURE_FILE + " TEXT);";

		db.execSQL(strQueryLogFile);

		String strQueryMachineCounter = "CREATE TABLE IF NOT EXISTS " + MachineCounter.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ MachineCounter.TASKBUSINESSKEY + " TEXT, "
				+ MachineCounter.CODCONTADOR + " TEXT, "
				+ MachineCounter.STARTVALUE + " TEXT, "
				+ MachineCounter.ENDVALUE + " TEXT, "
				+ MachineCounter.STARTDATE + " TEXT, "
				+ MachineCounter.ENDDATE + " TEXT);";

		db.execSQL(strQueryMachineCounter);

		String strQueryDetailCounter = "CREATE TABLE IF NOT EXISTS " + DetailCounter.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DetailCounter.TASKID + " INTEGER, "
				+ DetailCounter.CODCOUNTER + " TEXT, "
				+ DetailCounter.QUANTITY + " TEXT);";

		db.execSQL(strQueryDetailCounter);

		String strQueryCompleteDetailCounter = "CREATE TABLE IF NOT EXISTS " + CompleteDetailCounter.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CompleteDetailCounter.TASKID + " INTEGER, "
				+ CompleteDetailCounter.CODCOUNTER + " TEXT, "
				+ CompleteDetailCounter.QUANTITY + " TEXT);";

		db.execSQL(strQueryCompleteDetailCounter);

		String strQueryError = "CREATE TABLE IF NOT EXISTS " + CommentError.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CommentError.ID + " TEXT, "
				+ CommentError.ERROR + " TEXT);";

		db.execSQL(strQueryError);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS tb_task");
		db.execSQL("DROP TABLE IF EXISTS " + LogFile.TABLENAME);
		onCreate(db);
	}

}
