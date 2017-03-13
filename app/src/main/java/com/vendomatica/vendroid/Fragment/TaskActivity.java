package com.vendomatica.vendroid.Fragment;

/**
 * Created by shevchenko on 2015-11-26.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.maps.MapsInitializer;
import com.vendomatica.vendroid.Common.BaseActivity;
import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.Model.CompleteTask;
import com.vendomatica.vendroid.Model.CompltedTinTask;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.LocationLoader;
import com.vendomatica.vendroid.Model.PendingTasks;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.services.LogService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TaskActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<String> arraylist;
    private Spinner spEst, spMon, spBill, spTar, spNiv, spExt, spInt, spSer, spSel, spIlu;
    private LinearLayout lnSpEst, lnSpMon, lnSpBill, lnSpTar, lnSpNiv, lnSpExt, lnSpInt, lnSpSer, lnSpSel, lnSpIlu;
    private TextView txtSpEst, txtSpMon, txtSpBill, txtSpTar, txtSpNiv, txtSpExt, txtSpInt, txtSpSer, txtSpSel, txtSpIlu;
    private TextView txtPicture;
    private ArrayList<String> estList, monList, billList, tarList, nivList, extList, intList, serList, selList, iluList;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri = null;
    private int nCurIndex = 0;
    private LinearLayout lnImages;
    private ProgressDialog mProgDlg;
    private TextView txtTitle, txtSummary, txtMachine, txtTaskBusiness;
    private String[] mArrPhotos;
    private String strFileName = "";
    private int nSelEst, nSelMon, nSelBill, nSelTar, nSelNiv, nSelExt, nSelInt, nSelSer, nSelSel, nSelIlu;
    private ComponentName mService;
    private int nTaskID;
    private String date;
    private String tasktype;
    private String latitude, longitude;
    LocationLoader mLocationLoader;
    private Location mNewLocation;
    private ImageView imgWaze;
    private int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Common.getInstance().signaturePath = "";
        nTaskID = getIntent().getIntExtra("taskid", 0);
        date = getIntent().getStringExtra("date");
        tasktype = getIntent().getStringExtra("tasktype");
        txtPicture = (TextView) findViewById(R.id.txtPic);
        txtPicture.setOnClickListener(this);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtSummary = (TextView) findViewById(R.id.txtSummary);
        txtMachine = (TextView) findViewById(R.id.txtMachine);
        txtTaskBusiness = (TextView) findViewById(R.id.txtTaskBusiness);
        //imgWaze = (ImageView) findViewById(R.id.waze);
        imgWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //String url = "waze://?q=Hawaii";
                    String url = "waze://?ll=" + latitude + "," + longitude + "&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.btnSendForm).setOnClickListener(this);

        lnImages = (LinearLayout) findViewById(R.id.lnImages);
        lnSpEst = (LinearLayout) findViewById(R.id.lnSpEst);
        lnSpMon = (LinearLayout) findViewById(R.id.lnSpMon);
        lnSpBill = (LinearLayout) findViewById(R.id.lnSpBill);
        lnSpTar = (LinearLayout) findViewById(R.id.lnSpTar);
        lnSpNiv = (LinearLayout) findViewById(R.id.lnSpNiv);
        lnSpExt = (LinearLayout) findViewById(R.id.lnSpExt);
        lnSpInt = (LinearLayout) findViewById(R.id.lnSpInt);
        lnSpSer = (LinearLayout) findViewById(R.id.lnSpSer);
        lnSpSel = (LinearLayout) findViewById(R.id.lnSpSel);
        lnSpIlu = (LinearLayout) findViewById(R.id.lnSpIlu);

        lnSpEst.setOnClickListener(this);
        lnSpMon.setOnClickListener(this);
        lnSpBill.setOnClickListener(this);
        lnSpTar.setOnClickListener(this);
        lnSpNiv.setOnClickListener(this);
        lnSpExt.setOnClickListener(this);
        lnSpInt.setOnClickListener(this);
        lnSpSer.setOnClickListener(this);
        lnSpSel.setOnClickListener(this);
        lnSpIlu.setOnClickListener(this);

        /*
        arraylist = new ArrayList<String>();
        arraylist.add("c1_value1011");
        arraylist.add("c1_value1012");
        arraylist.add("c1_value1013");
        arraylist.add("c1_value1014");
        arraylist.add("c1_value1015");
        arraylist.add("c1_value1016");
        arraylist.add("c1_value1017");
        arraylist.add("c1_value1018");
*/
        estList = new ArrayList<String>();
        estList.add("Seleccione");
        estList.add(Common.getInstance().arrCategory.get(0).category);
        estList.add(Common.getInstance().arrCategory.get(1).category);
        ArrayAdapter<String> adapterEst = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, estList);
        spEst = (Spinner) findViewById(R.id.spEst);
        spEst.setPrompt("Select value");
        spEst.setAdapter(adapterEst);
        spEst.setOnItemSelectedListener(mSpEstListener);

        monList = new ArrayList<String>();
        monList.add("Seleccione");
        monList.add(Common.getInstance().arrCategory.get(2).category);
        monList.add(Common.getInstance().arrCategory.get(3).category);
        monList.add(Common.getInstance().arrCategory.get(4).category);
        ArrayAdapter<String> adapterMon = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, monList);
        spMon = (Spinner) findViewById(R.id.spMon);
        spMon.setPrompt("Select value");
        spMon.setAdapter(adapterMon);
        spMon.setOnItemSelectedListener(mSpMonListener);

        billList = new ArrayList<String>();
        billList.add("Seleccione");
        billList.add(Common.getInstance().arrCategory.get(5).category);
        billList.add(Common.getInstance().arrCategory.get(6).category);
        billList.add(Common.getInstance().arrCategory.get(7).category);
        ArrayAdapter<String> adapterBill = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, billList);
        spBill = (Spinner) findViewById(R.id.spBill);
        spBill.setPrompt("Select value");
        spBill.setAdapter(adapterBill);
        spBill.setOnItemSelectedListener(mSpBillListener);

        tarList = new ArrayList<String>();
        tarList.add("Seleccione");
        tarList.add(Common.getInstance().arrCategory.get(8).category);
        tarList.add(Common.getInstance().arrCategory.get(9).category);
        tarList.add(Common.getInstance().arrCategory.get(10).category);
        ArrayAdapter<String> adapterTar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tarList);
        spTar = (Spinner) findViewById(R.id.spTar);
        spTar.setPrompt("Select value");
        spTar.setAdapter(adapterTar);
        spTar.setOnItemSelectedListener(mSpTarListener);

        nivList = new ArrayList<String>();
        nivList.add("Seleccione");
        nivList.add(Common.getInstance().arrCategory.get(11).category);
        nivList.add(Common.getInstance().arrCategory.get(12).category);
        nivList.add(Common.getInstance().arrCategory.get(13).category);
        ArrayAdapter<String> adapterNiv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nivList);
        spNiv = (Spinner) findViewById(R.id.spNiv);
        spNiv.setPrompt("Select value");
        spNiv.setAdapter(adapterNiv);
        spNiv.setOnItemSelectedListener(mSpNivListener);

        extList = new ArrayList<String>();
        extList.add("Seleccione");
        extList.add(Common.getInstance().arrCategory.get(14).category);
        extList.add(Common.getInstance().arrCategory.get(15).category);
        extList.add(Common.getInstance().arrCategory.get(16).category);
        ArrayAdapter<String> adapterExt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, extList);
        spExt = (Spinner) findViewById(R.id.spExt);
        spExt.setPrompt("Select value");
        spExt.setAdapter(adapterExt);
        spExt.setOnItemSelectedListener(mSpExtListener);

        intList = new ArrayList<String>();
        intList.add("Seleccione");
        intList.add(Common.getInstance().arrCategory.get(17).category);
        intList.add(Common.getInstance().arrCategory.get(18).category);
        intList.add(Common.getInstance().arrCategory.get(19).category);
        ArrayAdapter<String> adapterInt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, intList);
        spInt = (Spinner) findViewById(R.id.spInt);
        spInt.setPrompt("Select value");
        spInt.setAdapter(adapterInt);
        spInt.setOnItemSelectedListener(mSpIntListener);

        serList = new ArrayList<String>();
        serList.add("Seleccione");
        serList.add(Common.getInstance().arrCategory.get(20).category);
        serList.add(Common.getInstance().arrCategory.get(21).category);
        ArrayAdapter<String> adapterSer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, serList);
        spSer = (Spinner) findViewById(R.id.spSer);
        spSer.setPrompt("Select value");
        spSer.setAdapter(adapterSer);
        spSer.setOnItemSelectedListener(mSpSerListener);

        selList = new ArrayList<String>();
        selList.add("Seleccione");
        selList.add(Common.getInstance().arrCategory.get(22).category);
        selList.add(Common.getInstance().arrCategory.get(23).category);
        ArrayAdapter<String> adapterSel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, selList);
        spSel = (Spinner) findViewById(R.id.spSel);
        spSel.setPrompt("Select value");
        spSel.setAdapter(adapterSel);
        spSel.setOnItemSelectedListener(mSpSelListener);

        iluList = new ArrayList<String>();
        iluList.add("Seleccione");
        iluList.add(Common.getInstance().arrCategory.get(24).category);
        iluList.add(Common.getInstance().arrCategory.get(25).category);
        ArrayAdapter<String> adapterIlu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, iluList);
        spIlu = (Spinner) findViewById(R.id.spIlu);
        spIlu.setPrompt("Select value");
        spIlu.setAdapter(adapterIlu);
        spIlu.setOnItemSelectedListener(mSpIluListener);

        txtSpEst = (TextView) findViewById(R.id.txtSpEst);
        txtSpEst.setText("");
        txtSpMon = (TextView) findViewById(R.id.txtSpMon);
        txtSpMon.setText("");
        txtSpBill = (TextView) findViewById(R.id.txtSpBill);
        txtSpBill.setText("");
        txtSpTar = (TextView) findViewById(R.id.txtSpTar);
        txtSpTar.setText("");
        txtSpNiv = (TextView) findViewById(R.id.txtSpNiv);
        txtSpNiv.setText("");
        txtSpExt = (TextView) findViewById(R.id.txtSpExt);
        txtSpExt.setText("");
        txtSpInt = (TextView) findViewById(R.id.txtSpInt);
        txtSpInt.setText("");
        txtSpSer = (TextView) findViewById(R.id.txtSpSer);
        txtSpSer.setText("");
        txtSpSel = (TextView) findViewById(R.id.txtSpSel);
        txtSpSel.setText("");
        txtSpIlu = (TextView) findViewById(R.id.txtSpIlu);
        txtSpIlu.setText("");

        mProgDlg = new ProgressDialog(this);
        mProgDlg.setCancelable(false);
        mProgDlg.setTitle("Posting Task!");
        mProgDlg.setMessage("Please Wait!");

        mArrPhotos = new String[]{"", "", "", "", ""};
        nSelEst = 0;
        nSelMon = 0;
        nSelBill = 0;
        nSelTar = 0;
        nSelNiv = 0;
        nSelExt = 0;
        nSelInt = 0;
        nSelSer = 0;
        nSelSel = 0;
        nSelIlu = 0;
        //MapsInitializer.initialize(getApplicationContext());
        mLocationLoader = new LocationLoader(this, false);
        mLocationLoader
                .SetOnLoadEventListener(new LocationLoader.OnLoadEventListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mNewLocation = location;
                        getLocation();
                        // mUpdateLocationHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onAddressChanged(String strAddress) {

                    }

                    @Override
                    public void onError(int iErrorCode) {
                        getLocation();
                    }
                });
        mLocationLoader.Start();

        setTitleAndSummary();



    }
    private void getLocation() {
        if (mNewLocation == null)
            return;
        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
    }
    private void setTitleAndSummary() {
        Tickets taskInfo;
        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
            taskInfo = Common.getInstance().arrTickets.get(i);
//            if (taskInfo.getTaskID() == nTaskID) {
//                txtTitle.setText(taskInfo.getCustomer());
//                txtSummary.setText("Direccion: " + taskInfo.getAdress() + ", " + taskInfo.getLocationDesc());
//                txtMachine.setText("Tipo Maquina: " + taskInfo.getMachineType());
//                txtTaskBusiness.setText("Serie Maquina: " + taskInfo.getTaskBusinessKey());
//                latitude = taskInfo.getLatitude();
//                longitude = taskInfo.getLongitude();
//                break;
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        //String strFilePath = Environment.getExternalStorageDirectory().getPath() + "/temp" + String.valueOf(nCurIndex) + ".jpg";
        String strFilePath =  Environment.getExternalStorageDirectory() + "/staffapp/"+ strFileName;
        String strFilePath1 =  Environment.getExternalStorageDirectory() + "/staffapp/"+ "strFileName.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, options);
        try {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){

        }

        File imgFile = new File(strFilePath);
        if (nCurIndex == 0)
            lnImages.setVisibility(View.VISIBLE);
        if (imgFile.exists()) {
            ImageView imgPhoto = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.space_80), (int) getResources().getDimension(R.dimen.space_80));
            params.leftMargin = (int) getResources().getDimension(R.dimen.space_10);
            params.gravity = Gravity.CENTER_VERTICAL;

            Bitmap myBitmap = loadLargeBitmapFromFile(imgFile.getAbsolutePath(), this);
            imgPhoto.setImageBitmap(myBitmap);
            imgPhoto.setLayoutParams(params);
            lnImages.addView(imgPhoto);
            mArrPhotos[nCurIndex] = strFilePath;
            nCurIndex++;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
//                if (nTaskID == Common.getInstance().arrTickets.get(i).getTaskID())
//                    Common.getInstance().arrTickets.remove(i);
            }
            if (msg.what == 0) {
                Toast.makeText(TaskActivity.this, "Task completed!", Toast.LENGTH_LONG).show();
                TaskActivity.this.finish();
            } else {
                Toast.makeText(TaskActivity.this, "Post incompleted!", Toast.LENGTH_LONG).show();
                TaskActivity.this.finish();
            }
        }

    };
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            String strField1 = txtSpEst.getText().toString();
            String strField2 = txtSpMon.getText().toString();
            String strField3 = txtSpBill.getText().toString();
            String strField4 = txtSpTar.getText().toString();
            /*
            boolean bRet = NetworkManager.getManager().postTask(nTaskID, latitude, longitude, strField1, strField2, strField3, strField4, mArrPhotos, nCurIndex);
            if(bRet)
            {
                mHandler.sendEmptyMessage(0);
            }
            else
            {
                mHandler.sendEmptyMessage(-1);
            }*/

        }
    };

    private boolean checkAllField() {
        String strTitle = txtTitle.getText().toString();
        String strSummary = txtSummary.getText().toString();
        return !(strTitle.isEmpty() || strSummary.isEmpty() || nCurIndex == 0);
    }

    private void postTask() {
        if (!checkAllField()) {
            Toast.makeText(this, "Please insert all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean bOnline = getConnectivityStatus();
        if (!bOnline) {
            Toast.makeText(this, "Your internet unavailable, This photo will be uploaded automatically. Once your phone regains internet!", Toast.LENGTH_LONG).show();
            new Thread(mCheckNetWorkRunnable).start();
            addPendingTask();
            for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
//                if (nTaskID == Common.getInstance().arrTickets.get(i).getTaskID())
//                    Common.getInstance().arrTickets.remove(i);
            }
            startActivity(new Intent(TaskActivity.this, MainActivity.class));
            return;
        }
        new Thread(mRunnable).start();
    }

    private void addPendingTask() {
        String strTitle = txtTitle.getText().toString();
        String strSummary = txtSummary.getText().toString();
        String strMachine = txtMachine.getText().toString();
        String strTaskBusinessKey = txtTaskBusiness.getText().toString();
        String strEst = txtSpEst.getText().toString();
        String strMon = txtSpMon.getText().toString();
        String strBill = txtSpBill.getText().toString();
        String strTar = txtSpTar.getText().toString();
        String strNiv = txtSpNiv.getText().toString();
        String strExt = txtSpExt.getText().toString();
        String strInt = txtSpInt.getText().toString();
        String strSer = txtSpSer.getText().toString();
        String strSel = txtSpSel.getText().toString();
        String strIlu = txtSpIlu.getText().toString();

        Tickets taskInfo;
        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
            taskInfo = Common.getInstance().arrTickets.get(i);
//            if (taskInfo.getTaskID() == nTaskID) {
//                String actiondate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                PendingTasks task = new PendingTasks(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getDate(), taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), taskInfo.getTaskBusinessKey(), taskInfo.getCustomer(), taskInfo.getAdress(), taskInfo.getLocationDesc(), taskInfo.getModel(), taskInfo.getLatitude(), taskInfo.getLongitude(), taskInfo.getepv(), Common.getInstance().latitude, Common.getInstance().longitude, actiondate, mArrPhotos[0], mArrPhotos[1], mArrPhotos[2], mArrPhotos[3], mArrPhotos[4], taskInfo.getMachineType(), Common.getInstance().signaturePath, "", "", taskInfo.getAux_valor1(), taskInfo.getAux_valor2(), taskInfo.getAux_valor3(), taskInfo.getAux_valor4(), taskInfo.getAux_valor5(), 1, "", taskInfo.getAux_valor6(), 0, "0");
//                CompleteTask comtask = new CompleteTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getDate(), taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), taskInfo.getTaskBusinessKey(), taskInfo.getCustomer(), taskInfo.getAdress(), taskInfo.getLocationDesc(), taskInfo.getModel(), taskInfo.getLatitude(), taskInfo.getLongitude(), taskInfo.getepv(), Common.getInstance().latitude, Common.getInstance().longitude, actiondate, mArrPhotos[0], mArrPhotos[1], mArrPhotos[2], mArrPhotos[3], mArrPhotos[4], taskInfo.getMachineType(), Common.getInstance().signaturePath, "", "", taskInfo.getAux_valor1(), taskInfo.getAux_valor2(), taskInfo.getAux_valor3(), taskInfo.getAux_valor4(), taskInfo.getAux_valor5(), 1, "", taskInfo.getAux_valor6(), 0, "0");
//                DBManager.getManager().insertPendingTask(task);
//                Common.getInstance().arrPendingTasks.add(task);
//                DBManager.getManager().insertCompleteTask(comtask);
//                Common.getInstance().arrCompleteTasks.add(comtask);
//
//                TinTask tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "estMaq", strEst, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                CompltedTinTask comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "estMaq", strEst, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "moned", strMon, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "moned", strMon, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "billeter", strBill, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "billeter", strBill, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "tarjet", strTar, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "tarjet", strTar, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "nivAb", strNiv, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "nivAb", strNiv, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "higEx", strExt, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "higEx", strExt, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "higIn", strInt, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "higIn", strInt, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrSm", strSer, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrSm", strSer, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrSen", strSel, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrSen", strSel, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//
//                tinInfo = new TinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrIlu", strIlu, "0");
//                DBManager.getManager().insertPendingTinTask(tinInfo);
//                Common.getInstance().arrTinTasks.add(tinInfo);
//
//                comtinInfo = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), "atrIlu", strIlu, "0");
//                DBManager.getManager().insertCompleteTinTask(comtinInfo);
//                Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
//                break;
//            }
        }
        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
//            if (Common.getInstance().arrTickets.get(i).getTaskID() == nTaskID) {
//                DBManager.getManager().deleteInCompleteTask(Common.getInstance().getLoginUser().getUserId(), nTaskID);
//                Common.getInstance().arrTickets.remove(i);
//            }
        }
        Intent intentMain = new Intent(TaskActivity.this, MainActivity.class);
        intentMain.putExtra("position", 0);
        startActivity(intentMain);
        //onBackPressed();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.txtPic:
                setService("The user takes some pictures.");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = createSaveCropFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
                break;
            case R.id.lnSpEst:
                setService("The user clicks the ComboBox1.");
                spEst.performClick();
                break;
            case R.id.lnSpMon:
                setService("The user clicks the ComboBox2.");
                spMon.performClick();
                break;
            case R.id.lnSpBill:
                setService("The user clicks the ComboBox3.");
                spBill.performClick();
                break;
            case R.id.lnSpTar:
                setService("The user clicks the ComboBox4.");
                spTar.performClick();
                break;
            case R.id.lnSpNiv:
                setService("The user clicks the ComboBox1.");
                spNiv.performClick();
                break;
            case R.id.lnSpExt:
                setService("The user clicks the ComboBox2.");
                spExt.performClick();
                break;
            case R.id.lnSpInt:
                setService("The user clicks the ComboBox3.");
                spInt.performClick();
                break;
            case R.id.lnSpSer:
                setService("The user clicks the ComboBox4.");
                spSer.performClick();
                break;
            case R.id.lnSpSel:
                setService("The user clicks the ComboBox1.");
                spSel.performClick();
                break;
            case R.id.lnSpIlu:
                setService("The user clicks the ComboBox2.");
                spIlu.performClick();
                break;
            case R.id.btnSendForm:
                if((txtSpEst.getText().toString().equals("Seleccione")) || (txtSpMon.getText().toString().equals("Seleccione")) || (txtSpBill.getText().toString().equals("Seleccione")) || (txtSpTar.getText().toString().equals("Seleccione")) || (txtSpNiv.getText().toString().equals("Seleccione")) || (txtSpExt.getText().toString().equals("Seleccione")) || (txtSpInt.getText().toString().equals("Seleccione")) || (txtSpSer.getText().toString().equals("Seleccione")) || (txtSpSel.getText().toString().equals("Seleccione")) || (txtSpIlu.getText().toString().equals("Seleccione"))){
                    Toast.makeText(TaskActivity.this, "Please input the all fields.", Toast.LENGTH_SHORT).show();
                }else {
                    setService("The user clicks the Send Form Button");
                    addPendingTask();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            Intent i = new Intent();
            i.setComponent(mService);
            stopService(i);
        }
    }

    public void setService(String description) {

        GpsInfo info = new GpsInfo(TaskActivity.this);
        Intent service = new Intent(TaskActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }
    private void DialogSelectOption() {
        final String items[] = { "item1", "item2", "item3" };
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Title");
        ab.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 각 리스트를 선택했을때
                    }
                }).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancel 버튼 클릭시
                    }
                });
        ab.show();
    }
    private Handler mCheckHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if (msg.what == 0) {
                Toast.makeText(TaskActivity.this, "Your phone regains interent!", Toast.LENGTH_LONG).show();
                postAllPendingTask();
            } else {

            }
        }

    };

    private void postAllPendingTask() {
        ArrayList<PendingTasks> tasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{null, null, null, null, null};
            int nCurIndex = 0;
            if (!tasks.get(i).file1.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (!tasks.get(i).file2.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (!tasks.get(i).file3.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (!tasks.get(i).file4.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (!tasks.get(i).file5.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }
            //Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).field1, tasks.get(i).field2, tasks.get(i).field3, tasks.get(i).field4, arrPhotos, nCurIndex);
            //if (bRet1)
            DBManager.getManager().deletePendingTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
        }
    }

    private Runnable mCheckNetWorkRunnable = new Runnable() {

        @Override
        public void run() {
            //while(!checkOnline())
            while (!getConnectivityStatus()) {
                try {
                    Thread.sleep(1000);
                } catch (Throwable t) {
                }
            }
            mCheckHandler.sendEmptyMessage(0);
        }
    };

    private boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    private AdapterView.OnItemSelectedListener mSpEstListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelEst = position;
            txtSpEst.setText(estList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpMonListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelMon = position;
            txtSpMon.setText(monList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpBillListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelBill = position;
            txtSpBill.setText(billList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpTarListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelTar = position;
            txtSpTar.setText(tarList.get(position));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpNivListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelNiv = position;
            txtSpNiv.setText(nivList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpExtListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelExt = position;
            txtSpExt.setText(extList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpIntListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelInt = position;
            txtSpInt.setText(intList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpSerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelSer = position;
            String txt = txtSpSer.getText().toString();
            txtSpSer.setText(serList.get(position));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelSel = position;
            String txt = txtSpSel.getText().toString();
            //if(!txt.equals(""))
            txtSpSel.setText(selList.get(position));
            //else
            //  txtSpSel.setText("Seleccione");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mSpIluListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            nSelIlu = position;
            String txt = txtSpIlu.getText().toString();
            //if(!txt.equals(""))
            txtSpIlu.setText(iluList.get(position));
            //else
            //txtSpIlu.setText("Seleccione");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private Bitmap loadLargeBitmapFromFile(String strPath, Context context) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, opt);

        int REQUIRED_SIZE = 480;
        int width_tmp = opt.outWidth, height_tmp = opt.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options opt1 = new BitmapFactory.Options();
        opt1.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(strPath, opt1);
        return bitmap;

    }

    private Uri createSaveCropFile() {
        Uri uri;
        strFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + String.valueOf(nCurIndex) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/staffapp", strFileName));
        return uri;
    }


    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }


    public boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
