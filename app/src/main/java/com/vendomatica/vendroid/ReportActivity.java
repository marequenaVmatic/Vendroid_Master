package com.vendomatica.vendroid;
/*
This screen is about the report part in menu.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.net.NetworkManager;

import java.util.ArrayList;

public class ReportActivity extends Activity implements View.OnClickListener {

    private ProgressDialog mProgDlgLoading;
    private LinearLayout lnContainer;
    TextView txtTaskCount, txtCompleteTaskCount, txtPendingTaskCount, txtAbastecimiento, txtRecaudacion, txtTotalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        lnContainer = (LinearLayout) findViewById(R.id.lnContainer);

        txtTaskCount = (TextView) findViewById(R.id.txtTaskCount);
        int taskCount = Common.getInstance().arrTickets.size() + Common.getInstance().arrCompleteTasks.size();
        txtTaskCount.setText(String.valueOf(taskCount));

        txtCompleteTaskCount = (TextView) findViewById(R.id.txtCompleteTaskCount);
        txtCompleteTaskCount.setText(String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtPendingTaskCount = (TextView) findViewById(R.id.txtPendingTaskCount);
        txtPendingTaskCount.setText(String.valueOf(Common.getInstance().arrTickets.size()));

        txtAbastecimiento = (TextView) findViewById(R.id.txtAbastecimiento);
        ArrayList<Integer> arrTaskId = new ArrayList<>();
        if (Common.getInstance().arrCompleteTinTasks.size() > 0)
            arrTaskId.add(Common.getInstance().arrCompleteTinTasks.get(0).taskid);

        boolean equal = false;
        for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
            for (int j = 0; j < arrTaskId.size(); j++) {
                if (arrTaskId.get(j) == Common.getInstance().arrCompleteTinTasks.get(i).taskid) {
                    equal = true;
                }
            }
            if(equal == false)
                arrTaskId.add(Common.getInstance().arrCompleteTinTasks.get(i).taskid);
            equal = false;

        }
        txtAbastecimiento.setText(String.valueOf(arrTaskId.size()) + " de " + String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtRecaudacion = (TextView) findViewById(R.id.txtRecaudacion);
        ArrayList<String> arrTaskId_rac = new ArrayList<>();
        if (Common.getInstance().arrDetailCounters.size() > 0)
            arrTaskId_rac.add(Common.getInstance().arrDetailCounters.get(0).taskid);

        for (int i = 0; i < Common.getInstance().arrDetailCounters.size(); i++) {
            for (int j = 0; j < arrTaskId_rac.size(); j++) {
                if (!arrTaskId_rac.get(j).equals(Common.getInstance().arrDetailCounters.get(i).taskid)) {
                    arrTaskId_rac.add(Common.getInstance().arrDetailCounters.get(i).taskid);
                    break;
                }
            }
        }
        int count = 0;
        for(int i = 0; i < Common.getInstance().arrCompleteTasks.size(); i++){
            if(Common.getInstance().arrCompleteTasks.get(i).Aux_valor5.equals("1"))
                count++;
        }
        //txtRecaudacion.setText(String.valueOf(arrTaskId_rac.size()) + " de " + String.valueOf(Common.getInstance().arrCompleteTasks.size()));
        txtRecaudacion.setText(String.valueOf(count) + " de " + String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtTotalQuantity = (TextView) findViewById(R.id.txtTotalQuantity);
        int quantity_aba = 0;
        for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
            if(!Common.getInstance().arrCompleteTinTasks.get(i).quantity.equals(""))
                quantity_aba += Integer.parseInt(Common.getInstance().arrCompleteTinTasks.get(i).quantity);
        }
        txtTotalQuantity.setText(String.valueOf(quantity_aba));

        mProgDlgLoading = new ProgressDialog(this);
        mProgDlgLoading.setCancelable(false);
        mProgDlgLoading.setTitle("Reporte");
        mProgDlgLoading.setMessage("Loading Now!");
        Common.getInstance().arrReports.clear();
        if (getConnectivityStatus()) {
            mProgDlgLoading.show();
            new Thread(mRunnable_report).start();
        }else{
            Toast.makeText(ReportActivity.this, "Por favor conectese a interne", Toast.LENGTH_SHORT).show();
        }
    }
    //get the internet status.
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
    //send the request for the report to the Network Manager.
    private Runnable mRunnable_report = new Runnable() {
        @Override
        public void run() {
            int ret = NetworkManager.getManager().report(Common.getInstance().getLoginUser().getUserId());
            mHandler_report.sendEmptyMessage(ret);

        }
    };
    //display the report information to the screen.
    private Handler mHandler_report = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            mProgDlgLoading.hide();
            if (msg.what == 1) {
                if (Common.getInstance().arrReports.size() > 0) {
                    lnContainer.setVisibility(View.VISIBLE);
                    for (int i = 0; i < Common.getInstance().arrReports.size(); i++) {
                        View v = LayoutInflater.from(ReportActivity.this).inflate(R.layout.report_item, null);
                        ((TextView) v.findViewById(R.id.txtNus)).setText(Common.getInstance().arrReports.get(i).nus);
                        ((TextView) v.findViewById(R.id.txtQuantity)).setText(Common.getInstance().arrReports.get(i).quantity);
                        lnContainer.addView(v);
                    }
                }
            } else if (msg.what == 0) {
                Toast.makeText(ReportActivity.this, "Getting report was failed!!!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }
}
