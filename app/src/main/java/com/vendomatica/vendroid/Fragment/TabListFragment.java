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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.LocationLoader;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.services.LogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("ValidFragment")
public class TabListFragment extends Fragment {
    Context mContext;
    private LinearLayout lnTasks;
    private ComponentName mService;
    private View view ;
    ConfigPantalla cPant = new ConfigPantalla();
    private Location mNewLocation;
    LocationLoader mLocationLoader;

    public TabListFragment(Context context) {
        mContext = context;
    }

    public TabListFragment() {
        this.mContext = null;
    }

    public static TabListFragment getInstance(Context context) {
        TabListFragment pendingtask = new TabListFragment();
        pendingtask.mContext = context;
        return pendingtask;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_pending, null);
        cPant = (ConfigPantalla) getArguments().getSerializable("Config");
        //view = LoadTareas(view);
        return view;
    }

    private void loadCTR(){
        MapsInitializer.initialize(getContext());
        mLocationLoader = new LocationLoader(getContext(), false);
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

    }
    private void getLocation() {
        if (mNewLocation == null)
            return;
        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
    }

    private View  LoadTareas(View view){
        lnTasks = (LinearLayout) view.findViewById(R.id.lnTasks);
        lnTasks.removeAllViews();

//        Common.getInstance().updateTab(getActivity());
        ArrayList<Tarea> lstTareas = new ArrayList<Tarea>();
        DBManager.setContext(getContext());
        lstTareas = DBManager.getManager().getTareas2(cPant.idSubTipoTarea);
        lstTareas = DBManager.getManager().getTareas(cPant.idSubTipoTarea);
        //ArrayList<Tarea> lstTareas2 = new ArrayList<Tarea>();
        //lstTareas2 = DBManager.getManager().getTareas2(cPant.idSubTipoTarea);
        for (int i = 0; i <lstTareas.size(); i++) {
            final Tarea task = lstTareas.get(i);

            LinearLayout aRow = (LinearLayout) View.inflate(getContext(), R.layout.row_tablistfragment, null);
            LinearLayout.LayoutParams paramSet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramSet.topMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.leftMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.rightMargin = (int) getResources().getDimension(R.dimen.space_5);

            TextView txtfield1 = (TextView) aRow.findViewById(R.id.txtField1);
            TextView txtfield2 = (TextView) aRow.findViewById(R.id.txtField2);
            TextView txtfield3 = (TextView) aRow.findViewById(R.id.txtField3);
            TextView txtfield4 = (TextView) aRow.findViewById(R.id.txtField4);
            TextView txtfield5 = (TextView) aRow.findViewById(R.id.txtField5);

            ArrayList<ConfigPantalla> Pants = new ArrayList<ConfigPantalla>();
            Pants = DBManager.getManager().getPantalla();
            ArrayList<TareaDetalle> lstDet = new ArrayList<TareaDetalle>();
            lstDet = task.Detalle;
            for (int j = 0; j <lstDet.size(); j++) {
                TareaDetalle DT = new TareaDetalle();
                DT = lstDet.get(j);
                for (int n = 0; n <Pants.size(); n++) {
                    ConfigPantalla Pant = new ConfigPantalla();
                    Pant = Pants.get(n);
                    if (Pant.orderCard>0)
                        if(Pant.idCampo.equals(DT.idCampo)) {
                            if (Pant.orderCard == 1) txtfield1.setText(DT.ValorCampo);
                            if (Pant.orderCard == 2) txtfield2.setText(DT.ValorCampo);
                            if (Pant.orderCard == 3) txtfield3.setText(DT.ValorCampo);
                            if (Pant.orderCard == 4) txtfield4.setText(DT.ValorCampo);
                            if (Pant.orderCard == 5) txtfield5.setText(DT.ValorCampo);
                        }
                }
            }
            aRow.setLayoutParams(paramSet);

            aRow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //setService("The user clicks the Incompleted Task list. taskid=" + String.valueOf(task.getTaskID()));
                    Intent intent;


                    //setService(task.id_doc, "The user presses the pending task.");
                    String eee =String.valueOf(task.id_doc);
                    DeltalleTask fragment = new DeltalleTask();
                    Bundle parametro = new Bundle();
                    parametro.putSerializable("task", task);
                    parametro.putSerializable("Config", cPant);
                    fragment.setArguments(parametro);
                    //getActivity().getSupportFragmentManager().beginTransaction()
                    //        .replace(R.id.content_frame, fragment)
                    //        .commit();

                    intent = new Intent(getActivity(), DeltalleTaskAct.class);
                    intent.putExtra("task",task);
                    intent.putExtra("Config",cPant);
                    startActivity(intent);
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

        super.onResume();
        view = LoadTareas(view);
    }

    public void setService(int nTaskID, String description) {

        GpsInfo info = new GpsInfo(getContext());
        Intent service = new Intent(getContext(), LogService.class);
        service.putExtra("userid", "");
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = getContext().startService(service);
    }


}
