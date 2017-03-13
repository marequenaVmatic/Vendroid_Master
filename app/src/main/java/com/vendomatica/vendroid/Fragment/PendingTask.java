package com.vendomatica.vendroid.Fragment;
/*
This is the pending task list of the main screen.
when the user click the pending task, the event appears in this files.
 */
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.MainIntroActivity;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.services.LogService;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.fragment;

@SuppressLint("ValidFragment")
public class PendingTask extends Fragment {
    Context mContext;
    private LinearLayout lnTasks;
    private ComponentName mService;

    public PendingTask(Context context) {
        mContext = context;
    }

    public PendingTask() {
        this.mContext = null;
    }

    public static PendingTask getInstance(Context context) {
        PendingTask pendingtask = new PendingTask();
        pendingtask.mContext = context;
        return pendingtask;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pending, null);
        lnTasks = (LinearLayout) view.findViewById(R.id.lnTasks);
        lnTasks.removeAllViews();
        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++){
            Tickets task = Common.getInstance().arrTickets.get(i);
            double distance = 0;

            Location locationA = new Location("point A");

//            locationA.setLatitude(Double.parseDouble(task.latitud));
//            locationA.setLongitude(Double.parseDouble(task.longitud));

            Location locationB = new Location("point B");
            if (!Common.getInstance().latitude.equals("")) {
                locationB.setLatitude(Double.parseDouble(Common.getInstance().latitude));
                locationB.setLongitude(Double.parseDouble(Common.getInstance().longitude));

                distance = locationA.distanceTo(locationB);
            }
            int dis = (int)((distance / 1000) * 10);
            float result = (float)dis / 10;
            ///colocar distnance para indicar
            //task.distance = String.valueOf(result);
            //task.distance = String.valueOf(distance / 1000);

        }
        //Common.getInstance().arrTickets.clear();
        //Common.getInstance().arrTickets = DBManager.getManager().getInCompleteTask(Common.getInstance().getLoginUser().getUserId());
        Common.getInstance().updateTab(getActivity());

        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
            final Tickets task = Common.getInstance().arrTickets.get(i);

            LinearLayout aRow = (LinearLayout) View.inflate(getContext(), R.layout.row_pendingtask, null);
            LinearLayout.LayoutParams paramSet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramSet.topMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.leftMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.rightMargin = (int) getResources().getDimension(R.dimen.space_5);

            TextView txtTitle = (TextView) aRow.findViewById(R.id.title);
            TextView txtValue2 = (TextView) aRow.findViewById(R.id.txtField2);
            TextView txtValue3 = (TextView) aRow.findViewById(R.id.txtField3);
            TextView txtTaskType = (TextView) aRow.findViewById(R.id.txtTaskType);
            TextView txtDistance = (TextView) aRow.findViewById(R.id.txtDistance);
            txtTitle.setText(task.RazonSocial);
            txtValue2.setText(task.Direccion + ", " + task.Ubicacion);
            //SpannableString content = new SpannableString(task.getTaskBusinessKey());
            //content.setSpan(new UnderlineSpan(), 0, task.getTaskBusinessKey().length(), 0);
            txtValue3.setText(task.CodEstadoCliente);
            txtDistance.setText(task.SerieMaquina);
            //SpannableString content_epv = new SpannableString(task.getepv());
            //content_epv.setSpan(new UnderlineSpan(), 0, task.getepv().length(), 0);
            txtTaskType.setText(String.valueOf(task.Falla));

            aRow.setLayoutParams(paramSet);

            aRow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //setService("The user clicks the Incompleted Task list. taskid=" + String.valueOf(task.getTaskID()));
                    Intent intent;

                    setService(task.id_doc, "The user presses the pending task.");
                    String eee =String.valueOf(task.id_doc);
                    Log.d(String.valueOf(task.id_doc), "onClick: ");
//                    if (task.getTaskType().equals("1")) {
                    DeltalleTask fragment = new DeltalleTask();
                    Bundle parametro = new Bundle();
                    parametro.putSerializable("task", task);
                    parametro.putString("taskid", task.RazonSocial);
                    fragment.setArguments(parametro);
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            ft.replace(R.id.content_frame, fragment,"DetailTicket");
//                            ft.commit();
                     intent = new Intent(getActivity(), DeltalleTaskAct.class);
                    startActivity(intent);


//                    } else if (task.getTaskType().equals("2")) {
////                        intent = new Intent(mContext, TinTaskActivity.class);
////                        intent.putExtra("taskid", task.getTaskID());
////                        intent.putExtra("date", task.getDate());
////                        intent.putExtra("tasktype", task.getTaskType());
////                        intent.putExtra("RutaAbastecimiento", task.getRutaAbastecimiento());
////                        intent.putExtra("Taskbusinesskey", task.getTaskBusinessKey());
////                        startActivity(intent);
//                    } else if (task.getTaskType().equals("4")) {
//                        //intent = new Intent(getContext(), AbaTaskActivity.class);
//                        //Fragment abatask = new AbaTaskActivity();
////                        Bundle args = new Bundle();
////
////                        args.putInt("taskid", task.getTaskID());
////                        args.putString("date", task.getDate().toString());
////                        args.putString("tasktype", task.getTaskType());
////                        args.putString("RutaAbastecimiento", task.getRutaAbastecimiento());
////                        args.putString("Taskbusinesskey", task.getTaskBusinessKey());
////                        args.putString("MachineType", task.getMachineType());
//                        //abatask.setArguments(args);
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .commit();
//                        //startActivity(intent);
//                    }
                }
            });
            lnTasks.addView(aRow);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
        }
    }

    @Override
    public void onResume() {
        lnTasks.removeAllViews();
        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++){
            Tickets task = Common.getInstance().arrTickets.get(i);
            double distance = 0;

            Location locationA = new Location("point A");

//            locationA.setLatitude(Double.parseDouble(task.latitud));
//            locationA.setLongitude(Double.parseDouble(task.longitud));

            Location locationB = new Location("point B");
            if (!Common.getInstance().latitude.equals("")) {
                locationB.setLatitude(Double.parseDouble(Common.getInstance().latitude));
                locationB.setLongitude(Double.parseDouble(Common.getInstance().longitude));

                distance = locationA.distanceTo(locationB);
            }
            int dis = (int)((distance / 1000) * 10);
            float result = (float)dis / 10;
            ///colocar distnance para indicar
            //task.distance = String.valueOf(result);
            //task.distance = String.valueOf(distance / 1000);

        }
        //Common.getInstance().arrTickets.clear();
        //Common.getInstance().arrTickets = DBManager.getManager().getInCompleteTask(Common.getInstance().getLoginUser().getUserId());

        for (int i = 0; i < Common.getInstance().arrTickets.size(); i++) {
            final Tickets task = Common.getInstance().arrTickets.get(i);

            LinearLayout aRow = (LinearLayout) View.inflate(getContext(), R.layout.row_pendingtask, null);
            LinearLayout.LayoutParams paramSet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramSet.topMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.leftMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.rightMargin = (int) getResources().getDimension(R.dimen.space_5);

            TextView txtTitle = (TextView) aRow.findViewById(R.id.title);
            TextView txtValue2 = (TextView) aRow.findViewById(R.id.txtField2);
            TextView txtValue3 = (TextView) aRow.findViewById(R.id.txtField3);
            TextView txtTaskType = (TextView) aRow.findViewById(R.id.txtTaskType);
            TextView txtDistance = (TextView) aRow.findViewById(R.id.txtDistance);
            txtTitle.setText(task.RazonSocial);
            txtValue2.setText(task.Direccion + ", " + task.Ubicacion);
            //SpannableString content = new SpannableString(task.getTaskBusinessKey());
            //content.setSpan(new UnderlineSpan(), 0, task.getTaskBusinessKey().length(), 0);
            txtValue3.setText(task.CodEstadoCliente);
            txtDistance.setText(task.SerieMaquina);
            //SpannableString content_epv = new SpannableString(task.getepv());
            //content_epv.setSpan(new UnderlineSpan(), 0, task.getepv().length(), 0);
            txtTaskType.setText(String.valueOf(task.Falla));

            aRow.setLayoutParams(paramSet);

            aRow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //setService("The user clicks the Incompleted Task list. taskid=" + String.valueOf(task.getTaskID()));
                    Intent intent;

                    setService(task.id_doc, "The user presses the pending task.");
                    String eee =String.valueOf(task.id_doc);
                    Log.d(String.valueOf(task.id_doc), "onClick: ");
                    DeltalleTask fragment = new DeltalleTask();
                    Bundle parametro = new Bundle();
                    parametro.putSerializable("task", task);
                    parametro.putString("taskid", task.RazonSocial);
                    fragment.setArguments(parametro);
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, fragment);
//                    ft.commit();
                    intent = new Intent(getActivity(), DeltalleTaskAct.class);
                    intent.putExtra("task", task);
                    getActivity().startActivity(intent);
                }
            });
            lnTasks.addView(aRow);
        }
        super.onResume();

    }

    public void setService(int nTaskID, String description) {

        GpsInfo info = new GpsInfo(getContext());
        Intent service = new Intent(getContext(), LogService.class);
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = getContext().startService(service);
    }


}
