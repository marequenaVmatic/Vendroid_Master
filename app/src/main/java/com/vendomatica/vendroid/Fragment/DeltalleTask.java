package com.vendomatica.vendroid.Fragment;
/*
This is the pending task list of the main screen.
when the user click the pending task, the event appears in this files.
 */
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.Tabla;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.services.LogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class DeltalleTask extends Fragment {
    Context mContext;
    private LinearLayout lnDet;
    private ComponentName mService;
    private List<String> itemsEstado = new ArrayList<String>();
    private List<String> itemsClasificacion = new ArrayList<String>();
    private List<String> itemsFalla = new ArrayList<String>();
    private Button btnSend;
    private Button btnPhoto;
    private Tarea ticket;
    private ConfigPantalla cPant;
    private ArrayList<Tabla> tabFalla;
    private ArrayList<Tabla> tabEstado;
    //foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    public DeltalleTask(Context context) {
        mContext = context;
    }

    public DeltalleTask() {
        this.mContext = null;
    }


    private String codFalla,comentarios,  id_bitacora, latitud,longitud;
    private int id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2,id_archivo;

    private  View view;

    public static DeltalleTask getInstance(Context context) {
        DeltalleTask pendingtask = new DeltalleTask();
        pendingtask.mContext = context;
        return pendingtask;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detalle_ticket, null);
        DBManager.setContext(getContext());
        ticket = (Tarea) getArguments().getSerializable("task");
        cPant = (ConfigPantalla) getArguments().getSerializable("Config");

        lnDet = (LinearLayout) view.findViewById(R.id.lnDet);
        lnDet.removeAllViews();


        //Detalle generado
       ArrayList<TareaDetalle> TDs = new ArrayList<TareaDetalle>();
        TDs = ticket.Detalle;
        for (int i = 0; i <TDs.size(); i++) {
            TareaDetalle DetTask = TDs.get(i);
            LinearLayout aRow = (LinearLayout) View.inflate(getContext(), R.layout.row_formfragment, null);
            LinearLayout.LayoutParams paramSet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramSet.topMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.leftMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.rightMargin = (int) getResources().getDimension(R.dimen.space_5);

            TextView txtfield1 = (TextView) aRow.findViewById(R.id.NombreCampo);
            TextView txtfield2 = (TextView) aRow.findViewById(R.id.ValorCampo);
            txtfield1.setText(DetTask.idCampo);
            txtfield2.setText(DetTask.ValorCampo);

           lnDet.addView(aRow);
        }


        id_doc = ticket.id_doc;
        id_resolucion = 0;//Integer.parseInt(ticket.id_resolucion);
        bp1 = 0;
        bp2 = 0;
        id_archivo =0;




        btnSend = (Button) view.findViewById(R.id.sendbtn) ;
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                comentarios = txtComent.getText().toString();
//                if (comentarios.equals("")) {
//                    View contentView = getActivity().findViewById(android.R.id.content);
//                    Snackbar.make(contentView, "Debe indicar un comentario..", Snackbar.LENGTH_LONG).show();
//                } else if (NetworkManager.getManager().saveBitacora(codFalla, id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2, comentarios, id_archivo, id_bitacora, latitud, longitud) > 0) {
//                    Common.getInstance().updateTab(getActivity());
//                    PendingTask fragment = new PendingTask();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, fragment);
//                    ft.commit();
//
//                }
            }
        });

        String idestado  = "";
        String idwkf  = "";
        for (int i = 0; i <ticket.Detalle.size(); i++) {
            if(ticket.Detalle.get(i).idCampo.equals("id_estado"))
                idestado=ticket.Detalle.get(i).ValorCampo;
            if(ticket.Detalle.get(i).idCampo.equals("id_wkf"))
                idwkf=ticket.Detalle.get(i).ValorCampo;
        }


        Spinner ddpEstado = (Spinner)view.findViewById(R.id.cboEstado);
        tabEstado = new ArrayList<Tabla>();

        tabEstado = DBManager.getManager().getTablas("Estados","id_wkf",idwkf,idestado);
        CustomAdapterDll customAdapter=new CustomAdapterDll(getContext(),tabEstado);
        ddpEstado.setAdapter(customAdapter);

        ddpEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Tabla selectC = tabEstado.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final Spinner ddpfalla = (Spinner)view.findViewById(R.id.cboFalla);

        Spinner ddpClasifcacion = (Spinner)view.findViewById(R.id.cboClasificacion);

        ArrayList<Tabla> tabClasificacion = new ArrayList<Tabla>();

        tabClasificacion = DBManager.getManager().getTablas("Clasificacion","","","");

            CustomAdapterDll customAdapterCla=new CustomAdapterDll(getContext(),tabClasificacion);
        ddpClasifcacion.setAdapter(customAdapterCla);

        final ArrayList<Tabla> finalTabClasificacion = tabClasificacion;
        ddpClasifcacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Tabla selectC = finalTabClasificacion.get(position);
                tabFalla = new ArrayList<Tabla>();
                DBManager.setContext(getContext());
                tabFalla = DBManager.getManager().getTablas("Falla","Clasificacion",selectC.idCampo.toString(),"");


                CustomAdapterDll customAdapterFalla=new CustomAdapterDll(getContext(),tabFalla);
                ddpfalla.setAdapter(customAdapterFalla);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        ddpfalla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Tabla selectC = tabFalla.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }






//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mService != null) {
//            Intent i = new Intent();
//            i.setComponent(mService);
//            mContext.stopService(i);
//        }
//    }

    public void setService(int nTaskID, String description) {

        GpsInfo info = new GpsInfo(getContext());
        Intent service = new Intent(getContext(), LogService.class);
        service.putExtra("userid","");
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = getContext().startService(service);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }




}
