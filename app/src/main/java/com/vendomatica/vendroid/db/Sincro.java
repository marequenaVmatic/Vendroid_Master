package com.vendomatica.vendroid.db;
/*
	This file has the manage functions for the android sqlite database..
	for example, insert, get, delete operation about the android sqlite database.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.Model.Category;
import com.vendomatica.vendroid.Model.Clasificacion;
import com.vendomatica.vendroid.Model.CommentError;
import com.vendomatica.vendroid.Model.CompleteDetailCounter;
import com.vendomatica.vendroid.Model.CompleteTask;
import com.vendomatica.vendroid.Model.CompltedTinTask;
import com.vendomatica.vendroid.Model.DetailCounter;
import com.vendomatica.vendroid.Model.Estado;
import com.vendomatica.vendroid.Model.Falla;
import com.vendomatica.vendroid.Model.LogEvent;
import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.MachineCounter;
import com.vendomatica.vendroid.Model.PendingTasks;
import com.vendomatica.vendroid.Model.Producto;
import com.vendomatica.vendroid.Model.Producto_RutaAbastecimento;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.Model.TaskType;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.net.NetworkManager;

import java.util.ArrayList;

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

	public boolean Sincronizar() {
		Toast.makeText(s_context, "Iniciando Sincronización", Toast.LENGTH_LONG).show();

		Common.getInstance().arrTickets.clear();
		//Common.getInstance().arrTicketsCerrados.clear();
		try {
			if (isOnline()) {

				DBManager.setContext(s_context);
				int nRet = NetworkManager.getManager().loadPublicArray(Common.getInstance().arrTickets);
				DBManager.getManager().deleteAlltickets();
				DBManager.getManager().insertTickets(Common.getInstance().arrTickets);

				//cerrados
				DBManager.getManager().deleteAllticketsCerrados();
				Common.getInstance().arrTicketsCerrados.addAll(DBManager.getManager().getAllticketsCerrados());

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

	//insert function about the CompleteCounter class.
	public long insertCompleteDetailCounter(CompleteDetailCounter detail) {
		ContentValues values = new ContentValues();
		values.put(CompleteDetailCounter.TASKID, detail.taskid);
		values.put(CompleteDetailCounter.CODCOUNTER, detail.CodCounter);
		values.put(CompleteDetailCounter.QUANTITY, detail.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			return db.insert(CompleteDetailCounter.TABLENAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//get function about the CompleteDetailCounter class from the android sqlite db.
	public ArrayList<CompleteDetailCounter> getCompleteDetailCounter() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CompleteDetailCounter> lstTasks = new ArrayList<CompleteDetailCounter>();
		Cursor cursor = db.query(CompleteDetailCounter.TABLENAME, new String[]{
				CompleteDetailCounter.TASKID,
				CompleteDetailCounter.CODCOUNTER,
				CompleteDetailCounter.QUANTITY,
		}, null, null, null, null, CompleteDetailCounter.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CompleteDetailCounter task = new CompleteDetailCounter();
			task.taskid = cursor.getString(0);
			task.CodCounter = cursor.getString(1);
			task.quantity = cursor.getString(2);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//insert function about the DetailCounter class
	public long insertDetailCounter(DetailCounter detail) {
		ContentValues values = new ContentValues();
		values.put(DetailCounter.TASKID, detail.taskid);
		values.put(DetailCounter.CODCOUNTER, detail.CodCounter);
		values.put(DetailCounter.QUANTITY, detail.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			return db.insert(DetailCounter.TABLENAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// insert function about the MachineCounter class.
	public long insertMachineCounter(MachineCounter machine) {
		ContentValues values = new ContentValues();
		values.put(MachineCounter.TASKBUSINESSKEY, machine.TaskBusinessKey);
		values.put(MachineCounter.CODCONTADOR, machine.CodContador);
		values.put(MachineCounter.STARTVALUE, machine.StartValue);
		values.put(MachineCounter.ENDVALUE, machine.EndValue);
		values.put(MachineCounter.STARTDATE, machine.StartDate);
		values.put(MachineCounter.ENDDATE, machine.EndDate);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			return db.insert(MachineCounter.TABLENAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the LogFile class
	public long insertLogFile(LogFile logFile) {
		ContentValues values = new ContentValues();
		values.put(LogFile.TASKID, logFile.getTaskID());
		values.put(LogFile.CAPTURE_FILE, logFile.getCaptureFile());
		values.put(LogFile.FILE_PATH, logFile.getFilePath());
		values.put(LogFile.FILE_TYPE, logFile.getFileType());

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			return db.insert(LogFile.TABLENAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the logevent class.
	public long insertLogEvent(String userid, String taskid, String strDateTime, String strDescription, String strLatitude, String strLongitude, String strBatteryLevel, String strFreeSpace, int iChargeUSB, int iChargeOther) {
		ContentValues values = new ContentValues();
		values.put(LogEvent.USERID, userid);
		values.put(LogEvent.TASKID, taskid);
		values.put(LogEvent.DATETIME, strDateTime);
		values.put(LogEvent.DESCRIPTION, strDescription);
		values.put(LogEvent.LATITUDE, strLatitude);
		values.put(LogEvent.LONGITUDE, strLongitude);
		values.put(LogEvent.BATTERY_LEVEL, strBatteryLevel);
		values.put(LogEvent.PHONE_FREESPACE, strFreeSpace);
		values.put(LogEvent.IS_CHARGING_USB, iChargeUSB);
		values.put(LogEvent.IS_CHARGING_OTHER, iChargeOther);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(LogEvent.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//update function about the TaskInfo class for distance.
	public long updateInCompleteTaskDistance(TaskInfo task) {
		ContentValues values = new ContentValues();
		values.put(TaskInfo.DISTANCE, task.distance);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.update(TaskInfo.TABLENAME, values, TaskInfo.USERID + "=" + task.userid + " AND " + TaskInfo.TASKID + "=" + task.taskID, null);
			//long lRet = db.insert(TaskInfo.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the Ticket class.
	public long insertTickets(ArrayList<Tickets> ListTickets) {
		ContentValues values = new ContentValues();
		values.put("TableName", "Tickets");
		values.put("ValueTable", ListTickets.toString());

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert("TABLEGENERIC", null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the Estados class.
	public long insertEstados(ArrayList<Estado> List) {
		ContentValues values = new ContentValues();
		values.put("TableName", "Estados");
		values.put("ValueTable", List.toString());

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert("TABLEGENERIC", null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the Clasificacion class.
	public long insertClasificacion(ArrayList<Clasificacion> List) {
		ContentValues values = new ContentValues();
		values.put("TableName", "Clasificacion");
		values.put("ValueTable", List.toString());

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert("TABLEGENERIC", null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the Falla class.
	public long insertFalla(ArrayList<Falla> List) {
		ContentValues values = new ContentValues();
		values.put("TableName", "Falla");
		values.put("ValueTable", List.toString());

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert("TABLEGENERIC", null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}


	//insert function about the CompleteTask class
	public long insertCompleteTask(CompleteTask task) {
		ContentValues values = new ContentValues();
		values.put(CompleteTask.USERID, task.userid);
		values.put(CompleteTask.TASKID, task.taskid);
		values.put(CompleteTask.DATE, task.date);
		values.put(CompleteTask.TASKTYPE, task.tasktype);
		values.put(CompleteTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(CompleteTask.TASKBUSINESSKEY, task.TaskBusinessKey);
		values.put(CompleteTask.CUSTOMER, task.Customer);
		values.put(CompleteTask.ADRESS, task.Adress);
		values.put(CompleteTask.LOCATIONDESC, task.LocationDesc);
		values.put(CompleteTask.MODEL, task.Model);
		values.put(CompleteTask.LATITUDE, task.latitude);
		values.put(CompleteTask.LONGITUDE, task.longitude);
		values.put(CompleteTask.EPV, task.epv);
		values.put(CompleteTask.LOGLATITUDE, task.logLatitude);
		values.put(CompleteTask.LOGLONGITUDE, task.logLongitude);
		values.put(CompleteTask.ACTIONDATE, task.ActionDate);
		values.put(CompleteTask.FILE1, task.file1);
		values.put(CompleteTask.FILE2, task.file2);
		values.put(CompleteTask.FILE3, task.file3);
		values.put(CompleteTask.FILE4, task.file4);
		values.put(CompleteTask.FILE5, task.file5);
		values.put(CompleteTask.MACHINETYPE, task.MachineType);
		values.put(CompleteTask.SIGNATURE, task.Signature);
		values.put(CompleteTask.NUMEROGUIA, task.NumeroGuia);
		values.put(CompleteTask.GLOSA, task.Glosa);
		values.put(CompleteTask.AUX_VALOR1, task.Aux_valor1);
		values.put(CompleteTask.AUX_VALOR2, task.Aux_valor2);
		values.put(CompleteTask.AUX_VALOR3, task.Aux_valor3);
		values.put(CompleteTask.AUX_VALOR4, task.Aux_valor4);
		values.put(CompleteTask.AUX_VALOR5, task.Aux_valor5);
		values.put(CompleteTask.COMPLETED, task.Completed);
		values.put(CompleteTask.COMMENT, task.Comment);
		values.put(CompleteTask.AUX_VALOR6, task.Aux_valor6);
		values.put(CompleteTask.QUANTITYRESUMEN, task.QuantityResumen);
		values.put(CompleteTask.Comment_Notcap, task.comment_notcap);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(CompleteTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the PendingTasks class.
	public long insertPendingTask(PendingTasks task) {
		ContentValues values = new ContentValues();
		values.put(PendingTasks.USERID, task.userid);
		values.put(PendingTasks.TASKID, task.taskid);
		values.put(PendingTasks.DATE, task.date);
		values.put(PendingTasks.TASKTYPE, task.tasktype);
		values.put(PendingTasks.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(PendingTasks.TASKBUSINESSKEY, task.TaskBusinessKey);
		values.put(PendingTasks.CUSTOMER, task.Customer);
		values.put(PendingTasks.ADRESS, task.Adress);
		values.put(PendingTasks.LOCATIONDESC, task.LocationDesc);
		values.put(PendingTasks.MODEL, task.Model);
		values.put(PendingTasks.LATITUDE, task.latitude);
		values.put(PendingTasks.LONGITUDE, task.longitude);
		values.put(PendingTasks.EPV, task.epv);
		values.put(PendingTasks.LOGLATITUDE, task.logLatitude);
		values.put(PendingTasks.LOGLONGITUDE, task.logLongitude);
		values.put(PendingTasks.ACTIONDATE, task.ActionDate);
		values.put(PendingTasks.FILE1, task.file1);
		values.put(PendingTasks.FILE2, task.file2);
		values.put(PendingTasks.FILE3, task.file3);
		values.put(PendingTasks.FILE4, task.file4);
		values.put(PendingTasks.FILE5, task.file5);
		values.put(PendingTasks.MACHINETYPE, task.MachineType);
		values.put(PendingTasks.SIGNATURE, task.Signature);
		values.put(PendingTasks.NUMEROGUIA, task.NumeroGuia);
		values.put(PendingTasks.GLOSA, task.Glosa);
		values.put(PendingTasks.AUX_VALOR1, task.Aux_valor1);
		values.put(PendingTasks.AUX_VALOR2, task.Aux_valor2);
		values.put(PendingTasks.AUX_VALOR3, task.Aux_valor3);
		values.put(PendingTasks.AUX_VALOR4, task.Aux_valor4);
		values.put(PendingTasks.AUX_VALOR5, task.Aux_valor5);
		values.put(PendingTasks.COMPLETED, task.Completed);
		values.put(PendingTasks.COMMENT, task.Comment);
		values.put(PendingTasks.AUX_VALOR6, task.Aux_valor6);
		values.put(PendingTasks.QUANTITYRESUMEN, task.QuantityResumen);
		values.put(PendingTasks.Comment_Notcap, task.comment_notcap);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(PendingTasks.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the TinTask class.
	public long insertPendingTinTask(TinTask task) {
		ContentValues values = new ContentValues();
		values.put(TinTask.USERID, task.userid);
		values.put(TinTask.TASKID, task.taskid);
		values.put(TinTask.TASKTYPE, task.tasktype);
		values.put(TinTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(TinTask.CUS, task.cus);
		values.put(TinTask.NUS, task.nus);
		values.put(TinTask.QUANTITY, task.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(TinTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the CompleteTinTask class
	public long insertCompleteTinTask(CompltedTinTask task) {
		ContentValues values = new ContentValues();
		values.put(CompltedTinTask.USERID, task.userid);
		values.put(CompltedTinTask.TASKID, task.taskid);
		values.put(CompltedTinTask.TASKTYPE, task.tasktype);
		values.put(CompltedTinTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(CompltedTinTask.CUS, task.cus);
		values.put(CompltedTinTask.NUS, task.nus);
		values.put(CompltedTinTask.QUANTITY, task.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(CompltedTinTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//insert function about the Category class.
	public long insertCategory(Category cat) {
		ContentValues values = new ContentValues();
		values.put(Category.CATEGORY, cat.category);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Category.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the TaskType class
	public long insertType(TaskType info) {
		ContentValues values = new ContentValues();
		values.put(TaskType.TYPE, info.type);
		values.put(TaskType.NAME, info.name);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(TaskType.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the Producto class
	public long insertProducto(Producto info) {
		ContentValues values = new ContentValues();
		values.put(Producto.CUS, info.cus);
		values.put(Producto.NUS, info.nus);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Producto.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the Producto_RutaAbastecimento class
	public long insertProducto_Ruta(Producto_RutaAbastecimento info) {
		ContentValues values = new ContentValues();
		values.put(Producto_RutaAbastecimento.TASKTYPE, info.TaskType);
		values.put(Producto_RutaAbastecimento.TASKBUSINESSKEY, info.TaskBusinessKey);
		values.put(Producto_RutaAbastecimento.RUTAABASTECIMENTO, info.RutaAbastecimento);
		values.put(Producto_RutaAbastecimento.CUS, info.cus);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Producto_RutaAbastecimento.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//Insert function about the User class
	public long insertUser(User info) {
		ContentValues values = new ContentValues();
		values.put(User.USERID, info.userid);
		values.put(User.PASSWORD, info.password);
		values.put(User.FIRSTNAME, info.firstName);
		values.put(User.LASTNAME, info.lastName);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(User.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public long insertError(CommentError info) {
		ContentValues values = new ContentValues();
		values.put(CommentError.ID, info.id);
		values.put(CommentError.ERROR, info.error);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(CommentError.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//get function about User class from the android sqlite db.
	public User getUser(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		User info = new User();
		Cursor cursor = db.query(User.TABLENAME, new String[]{
				User.USERID,
				User.PASSWORD,
				User.FIRSTNAME,
				User.LASTNAME
		}, User.USERID + "=" + userid, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			info.userid = cursor.getString(0);
			info.password = cursor.getString(1);
			info.firstName = cursor.getString(2);
			info.lastName = cursor.getString(3);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return info;
	}

	//get function about the CompleteTinTask class
	public ArrayList<CompltedTinTask> getCompleteTinTask(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CompltedTinTask> lstTasks = new ArrayList<CompltedTinTask>();
		Cursor cursor = db.query(CompltedTinTask.TABLENAME, new String[]{
				CompltedTinTask.USERID,
				CompltedTinTask.TASKID,
				CompltedTinTask.TASKTYPE,
				CompltedTinTask.RUTAABASTEIMIENTO,
				CompltedTinTask.CUS,
				CompltedTinTask.NUS,
				CompltedTinTask.QUANTITY,
		}, CompltedTinTask.USERID + "=" + userid, null, null, null, CompltedTinTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CompltedTinTask task = new CompltedTinTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.tasktype = cursor.getString(2);
			task.RutaAbastecimiento = cursor.getString(3);
			task.cus = cursor.getString(4);
			task.nus = cursor.getString(5);
			task.quantity = cursor.getString(6);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the DetailCounter
	public ArrayList<DetailCounter> getDetailCounter() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<DetailCounter> lstTasks = new ArrayList<DetailCounter>();
		Cursor cursor = db.query(DetailCounter.TABLENAME, new String[]{
				DetailCounter.TASKID,
				DetailCounter.CODCOUNTER,
				DetailCounter.QUANTITY,
		}, null, null, null, null, DetailCounter.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DetailCounter task = new DetailCounter();
			task.taskid = cursor.getString(0);
			task.CodCounter = cursor.getString(1);
			task.quantity = cursor.getString(2);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the TinTask
	public ArrayList<TinTask> getTinPendingTask(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TinTask> lstTasks = new ArrayList<TinTask>();
		Cursor cursor = db.query(TinTask.TABLENAME, new String[]{
				TinTask.USERID,
				TinTask.TASKID,
				TinTask.TASKTYPE,
				TinTask.RUTAABASTEIMIENTO,
				TinTask.CUS,
				TinTask.NUS,
				TinTask.QUANTITY,
		}, TinTask.USERID + "=" + userid, null, null, null, TinTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TinTask task = new TinTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.tasktype = cursor.getString(2);
			task.RutaAbastecimiento = cursor.getString(3);
			task.cus = cursor.getString(4);
			task.nus = cursor.getString(5);
			task.quantity = cursor.getString(6);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the TaskInfo
	public ArrayList<TaskInfo> getInCompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TaskInfo> lstTasks = new ArrayList<TaskInfo>();
		Cursor cursor = db.query(TaskInfo.TABLENAME, new String[]{
				TaskInfo.USERID,
				TaskInfo.TASKID,
				TaskInfo.DATE,
				TaskInfo.TASKTYPE,
				TaskInfo.RUATABASTECIMIENTO,
				TaskInfo.TASKBUSINESSKEY,
				TaskInfo.CUSTOMER,
				TaskInfo.ADRESS,
				TaskInfo.LOCATIONDESC,
				TaskInfo.MODEL,
				TaskInfo.LATITUDE,
				TaskInfo.LONGITUDE,
				TaskInfo.EPV,
				TaskInfo.DISTANCE,
				TaskInfo.MACHINETYPE,
				TaskInfo.AUX_VALOR1,
				TaskInfo.AUX_VALOR2,
				TaskInfo.AUX_VALOR3,
				TaskInfo.AUX_VALOR4,
				TaskInfo.AUX_VALOR5,
				TaskInfo.AUX_VALOR6,
		}, TaskInfo.USERID + "=" + userid, null, null, null, TaskInfo.DISTANCE + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TaskInfo task = new TaskInfo();
			task.userid = cursor.getString(0);
			task.taskID = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.taskType = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.distance = cursor.getString(13);
			task.MachineType = cursor.getString(14);
			task.Aux_valor1 = cursor.getString(15);
			task.Aux_valor2 = cursor.getString(16);
			task.Aux_valor3 = cursor.getString(17);
			task.Aux_valor4 = cursor.getString(18);
			task.Aux_valor5 = cursor.getString(19);
			task.Aux_valor6 = cursor.getString(20);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the CompleteTask
	public ArrayList<CompleteTask> getCompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CompleteTask> lstTasks = new ArrayList<CompleteTask>();
		Cursor cursor = db.query(CompleteTask.TABLENAME, new String[]{
				CompleteTask.USERID,
				CompleteTask.TASKID,
				CompleteTask.DATE,
				CompleteTask.TASKTYPE,
				CompleteTask.RUTAABASTEIMIENTO,
				CompleteTask.TASKBUSINESSKEY,
				CompleteTask.CUSTOMER,
				CompleteTask.ADRESS,
				CompleteTask.LOCATIONDESC,
				CompleteTask.MODEL,
				CompleteTask.LATITUDE,
				CompleteTask.LONGITUDE,
				CompleteTask.EPV,
				CompleteTask.LOGLATITUDE,
				CompleteTask.LOGLONGITUDE,
				CompleteTask.ACTIONDATE,
				CompleteTask.FILE1,
				CompleteTask.FILE2,
				CompleteTask.FILE3,
				CompleteTask.FILE4,
				CompleteTask.FILE5,
				CompleteTask.MACHINETYPE,
				CompleteTask.SIGNATURE,
				CompleteTask.NUMEROGUIA,
				CompleteTask.GLOSA,
				CompleteTask.AUX_VALOR1,
				CompleteTask.AUX_VALOR2,
				CompleteTask.AUX_VALOR3,
				CompleteTask.AUX_VALOR4,
				CompleteTask.AUX_VALOR5,
				CompleteTask.COMPLETED,
				CompleteTask.COMMENT,
				CompleteTask.AUX_VALOR6,
				CompleteTask.QUANTITYRESUMEN,
				CompleteTask.Comment_Notcap
		}, CompleteTask.USERID + "=" + userid, null, null, null, CompleteTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CompleteTask task = new CompleteTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.tasktype = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.logLatitude = cursor.getString(13);
			task.logLongitude = cursor.getString(14);
			task.ActionDate = cursor.getString(15);
			task.file1 = cursor.getString(16);
			task.file2 = cursor.getString(17);
			task.file3 = cursor.getString(18);
			task.file4 = cursor.getString(19);
			task.file5 = cursor.getString(20);
			task.MachineType = cursor.getString(21);
			task.Signature = cursor.getString(22);
			task.NumeroGuia = cursor.getString(23);
			task.Glosa = cursor.getString(24);
			task.Aux_valor1 = cursor.getString(25);
			task.Aux_valor2 = cursor.getString(26);
			task.Aux_valor3 = cursor.getString(27);
			task.Aux_valor4 = cursor.getString(28);
			task.Aux_valor5 = cursor.getString(29);
			task.Completed = cursor.getInt(30);
			task.Comment = cursor.getString(31);
			task.Aux_valor6 = cursor.getString(32);
			task.QuantityResumen = cursor.getInt(33);
			task.comment_notcap = cursor.getString(34);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the PendingTask
	public ArrayList<PendingTasks> getPendingTask(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<PendingTasks> lstTasks = new ArrayList<PendingTasks>();
		Cursor cursor = db.query(PendingTasks.TABLENAME, new String[]{
				PendingTasks.USERID,
				PendingTasks.TASKID,
				PendingTasks.DATE,
				PendingTasks.TASKTYPE,
				PendingTasks.RUTAABASTEIMIENTO,
				PendingTasks.TASKBUSINESSKEY,
				PendingTasks.CUSTOMER,
				PendingTasks.ADRESS,
				PendingTasks.LOCATIONDESC,
				PendingTasks.MODEL,
				PendingTasks.LATITUDE,
				PendingTasks.LONGITUDE,
				PendingTasks.EPV,
				PendingTasks.LOGLATITUDE,
				PendingTasks.LOGLONGITUDE,
				PendingTasks.ACTIONDATE,
				PendingTasks.FILE1,
				PendingTasks.FILE2,
				PendingTasks.FILE3,
				PendingTasks.FILE4,
				PendingTasks.FILE5,
				PendingTasks.MACHINETYPE,
				PendingTasks.SIGNATURE,
				PendingTasks.NUMEROGUIA,
				PendingTasks.GLOSA,
				PendingTasks.AUX_VALOR1,
				PendingTasks.AUX_VALOR2,
				PendingTasks.AUX_VALOR3,
				PendingTasks.AUX_VALOR4,
				PendingTasks.AUX_VALOR5,
				PendingTasks.COMPLETED,
				PendingTasks.COMMENT,
				PendingTasks.AUX_VALOR6,
				PendingTasks.QUANTITYRESUMEN,
				PendingTasks.Comment_Notcap
		}, PendingTasks.USERID + "=" + userid, null, null, null, PendingTasks.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PendingTasks task = new PendingTasks();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.tasktype = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.logLatitude = cursor.getString(13);
			task.logLongitude = cursor.getString(14);
			task.ActionDate = cursor.getString(15);
			task.file1 = cursor.getString(16);
			task.file2 = cursor.getString(17);
			task.file3 = cursor.getString(18);
			task.file4 = cursor.getString(19);
			task.file5 = cursor.getString(20);
			task.MachineType = cursor.getString(21);
			task.Signature = cursor.getString(22);
			task.NumeroGuia = cursor.getString(23);
			task.Glosa = cursor.getString(24);
			task.Aux_valor1 = cursor.getString(25);
			task.Aux_valor2 = cursor.getString(26);
			task.Aux_valor3 = cursor.getString(27);
			task.Aux_valor4 = cursor.getString(28);
			task.Aux_valor5 = cursor.getString(29);
			task.Completed = cursor.getInt(30);
			task.Comment = cursor.getString(31);
			task.Aux_valor6 = cursor.getString(32);
			task.QuantityResumen = cursor.getInt(33);
			task.comment_notcap = cursor.getString(34);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about all Catergory data.
	public ArrayList<Category> getAllCategory() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Category> lstTasks = new ArrayList<Category>();
		Cursor cursor = db.query(Category.TABLENAME, new String[]{
				Category.CATEGORY,
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category task = new Category();
			task.category = cursor.getString(0);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the all type data
	public ArrayList<TaskType> getAllTypes() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TaskType> lstTasks = new ArrayList<TaskType>();
		Cursor cursor = db.query(TaskType.TABLENAME, new String[]{
				TaskType.TYPE,
				TaskType.NAME
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TaskType task = new TaskType();
			task.type = cursor.getString(0);
			task.name = cursor.getString(1);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the all Producto
	public ArrayList<Producto> getAllProducto() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Producto> lstTasks = new ArrayList<Producto>();
		Cursor cursor = db.query(Producto.TABLENAME, new String[]{
				Producto.CUS,
				Producto.NUS,
		}, null, null, null, null, Producto.CUS + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Producto task = new Producto();
			task.cus = cursor.getString(0);
			task.nus = cursor.getString(1);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the all Producto_RutaAbastecimento
	public ArrayList<Producto_RutaAbastecimento> getAllProducto_Ruta() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Producto_RutaAbastecimento> lstTasks = new ArrayList<Producto_RutaAbastecimento>();
		Cursor cursor = db.query(Producto_RutaAbastecimento.TABLENAME, new String[]{
				Producto_RutaAbastecimento.TASKTYPE,
				Producto_RutaAbastecimento.TASKBUSINESSKEY,
				Producto_RutaAbastecimento.RUTAABASTECIMENTO,
				Producto_RutaAbastecimento.CUS,
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Producto_RutaAbastecimento task = new Producto_RutaAbastecimento();
			task.TaskType = cursor.getString(0);
			task.TaskBusinessKey = cursor.getString(1);
			task.RutaAbastecimento = cursor.getString(2);
			task.cus = cursor.getString(3);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the logevent
	public ArrayList<LogEvent> getLogEvents(String userid) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<LogEvent> lstTasks = new ArrayList<LogEvent>();
		Cursor cursor = db.query(LogEvent.TABLENAME, new String[]{
				LogEvent.USERID,
				LogEvent.TASKID,
				LogEvent.DATETIME,
				LogEvent.DESCRIPTION,
				LogEvent.LATITUDE,
				LogEvent.LONGITUDE,
				LogEvent.BATTERY_LEVEL,
				LogEvent.PHONE_FREESPACE,
				LogEvent.IS_CHARGING_USB,
				LogEvent.IS_CHARGING_OTHER
		}, LogEvent.USERID + "=" + userid, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogEvent log = new LogEvent();
			log.userid = cursor.getString(0);
			log.taskid = cursor.getString(1);
			log.datetime = cursor.getString(2);
			log.description = cursor.getString(3);
			log.latitude = cursor.getString(4);
			log.longitude = cursor.getString(5);
			log.batteryLevel = cursor.getString(6);
			log.freespace = cursor.getString(7);
			log.isChargingUSB = cursor.getInt(8);
			log.isChargingOther = cursor.getInt(9);

			lstTasks.add(log);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the all LogFile
	public ArrayList<LogFile> getLogFiles() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<LogFile> lstTasks = new ArrayList<LogFile>();
		Cursor cursor = db.query(LogFile.TABLENAME, new String[]{
				LogFile.TASKID,
				LogFile.FILE_PATH,
				LogFile.FILE_TYPE,
				LogFile.CAPTURE_FILE,
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogFile log = new LogFile(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

			lstTasks.add(log);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the all logFile
	public ArrayList<LogFile> getLogs(int taskId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<LogFile> lstTasks = new ArrayList<LogFile>();
		Cursor cursor = db.query(LogFile.TABLENAME, new String[]{
				LogFile.TASKID,
				LogFile.FILE_PATH,
				LogFile.FILE_TYPE,
				LogFile.CAPTURE_FILE,
		}, LogFile.TASKID + "=" + taskId, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogFile log = new LogFile(taskId, cursor.getString(1), cursor.getString(2), cursor.getString(3));
			lstTasks.add(log);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the MachineCounter
	public ArrayList<MachineCounter> getMachineCounters(String TaskBusinessKey) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<MachineCounter> lstTasks = new ArrayList<MachineCounter>();
		Cursor cursor = db.query(MachineCounter.TABLENAME, new String[]{
				MachineCounter.TASKBUSINESSKEY,
				MachineCounter.CODCONTADOR,
				MachineCounter.STARTVALUE,
				MachineCounter.ENDVALUE,
				MachineCounter.STARTDATE,
				MachineCounter.ENDDATE,
		}, MachineCounter.TASKBUSINESSKEY + "=" + "'" + TaskBusinessKey + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MachineCounter machine = new MachineCounter();
			machine.TaskBusinessKey = cursor.getString(0);
			machine.CodContador = cursor.getString(1);
			machine.StartValue = cursor.getString(2);
			machine.EndValue = cursor.getString(3);
			machine.StartDate = cursor.getString(4);
			machine.EndDate = cursor.getString(5);

			lstTasks.add(machine);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//get function about the Productos_CUS
	public ArrayList<String> getProductos_CUS(String RutaAbastecimiento, String Taskbusinesskey, String tasktype) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<String> lstCUS = new ArrayList<String>();
		Cursor cursor = db.query(Producto_RutaAbastecimento.TABLENAME, new String[]{
				Producto_RutaAbastecimento.TASKTYPE,
				Producto_RutaAbastecimento.TASKBUSINESSKEY,
				Producto_RutaAbastecimento.RUTAABASTECIMENTO,
				Producto_RutaAbastecimento.CUS,
		}, Producto_RutaAbastecimento.RUTAABASTECIMENTO + "=" + "'" + RutaAbastecimiento + "'" + " AND " + Producto_RutaAbastecimento.TASKBUSINESSKEY + "=" + "'" + Taskbusinesskey + "'" + " AND " + Producto_RutaAbastecimento.TASKTYPE + "=" + "'" + tasktype + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String cus = "";
			cus = cursor.getString(3);
			lstCUS.add(cus);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstCUS;
	}

	public ArrayList<CommentError> getAllErrors() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CommentError> lstTasks = new ArrayList<CommentError>();
		Cursor cursor = db.query(CommentError.TABLENAME, new String[]{
				CommentError.ID,
				CommentError.ERROR
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CommentError task = new CommentError();
			task.id = cursor.getString(0);
			task.error = cursor.getString(1);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}

	//delete function about the LogEvent table.
	public void deleteLogEvent(String userid, String dateTime) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(LogEvent.TABLENAME, LogEvent.USERID + "=" + "'" + userid + "'" + " AND " + LogEvent.DATETIME + "=" + "'" + dateTime + "'", null);
		//db.close();
	}

	//delete function about the LogFile table.
	public void deleteLogFile(LogFile log) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(LogFile.TABLENAME, LogFile.CAPTURE_FILE + "=" + "'" + log.getCaptureFile() + "'" + " AND " + LogFile.FILE_PATH + "=" + "'" + log.getFilePath() + "'", null);
		//db.close();
	}

	//delete function about the PendingTask table.
	public void deletePendingTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(PendingTasks.TABLENAME, PendingTasks.USERID + "=" + userid + " AND " + PendingTasks.TASKID + "=" + taskid, null);
		//db.close();		
	}

	//delete function about the TaskInfo table.
	public void deleteInCompleteTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskInfo.TABLENAME, TaskInfo.USERID + "=" + userid + " AND " + TaskInfo.TASKID + "=" + taskid, null);
		//db.close();
	}

	//delete function about the TinTask table.
	public void deletePendingTinTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TinTask.TABLENAME, TinTask.USERID + "=" + userid + " AND " + TinTask.TASKID + "=" + taskid, null);
		//db.close();
	}

	//delete function about the DetailCounter table.
	public void deleteDetailTask(String taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(DetailCounter.TABLENAME, DetailCounter.TASKID + "=" + taskid, null);
		//db.close();
	}

	//delete function about the TaskInfo table.\
	public void deleteAllIncompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskInfo.TABLENAME, TaskInfo.USERID + "=" + userid, null);
		//db.close();
	}

	//delete function about the CompleteTask table.\
	public void deleteAllCompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CompleteTask.TABLENAME, CompleteTask.USERID + "=" + userid, null);
		//db.close();
	}

	//delete function about the CompltedTinTask table.\
	public void deleteAllCompleteTinTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CompltedTinTask.TABLENAME, CompltedTinTask.USERID + "=" + userid, null);
		//db.close();
	}

	//delete function about the CompleteDetailCounter table.\
	public void deleteAllCompleteDetailCounter() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CompleteDetailCounter.TABLENAME, null, null);
		//db.close();
	}

	//delete function about the MachineCounter table.\
	public void deleteAllMachineCounter() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(MachineCounter.TABLENAME, null, null);
		//db.close();
	}

	//delete function about the Producto table.\
	public void deleteAllProducto() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Producto.TABLENAME, null, null);
		//db.close();
	}

	//delete function about the Producto_RutaAbastecimento table.\
	public void deleteAllProducto_Ruta() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Producto_RutaAbastecimento.TABLENAME, null, null);
		//db.close();
	}

	//delete function about the Category table.\
	public void deleteAllCategory() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Category.TABLENAME, null, null);
		//db.close();
	}

	//delete function about the TaskType table.\
	public void deleteAllTypes() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskType.TABLENAME, null, null);
		//db.close();
	}

	public void deleteAllErrors() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CommentError.TABLENAME, null, null);
	}

	//delete function about the User table.\
	public void deleteAllUser() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(User.TABLENAME, null, null);
		//db.close();
	}

	//delete generic Tickets
	public void deleteAlltickets() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete("TABLEGENERIC", "TableName ='Tickets'", null);
		//db.close();
	}

	//delete generic Estados
	public void deleteAllEstados() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete("TABLEGENERIC", "TableName ='Estados'", null);
		//db.close();
	}

	//delete generic Clasificacion
	public void deleteAllClasificacion() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete("TABLEGENERIC", "TableName ='Clasificacion'", null);
		//db.close();
	}

	//delete generic Falla
	public void deleteAllFalla() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete("TABLEGENERIC", "TableName ='Falla'", null);
		//db.close();
	}

}