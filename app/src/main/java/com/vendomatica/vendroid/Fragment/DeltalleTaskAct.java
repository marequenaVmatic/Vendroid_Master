package com.vendomatica.vendroid.Fragment;
/*
This is the pending task list of the main screen.
when the user click the pending task, the event appears in this files.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.PlantillaTipoTarea;
import com.vendomatica.vendroid.Model.Tabla;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.controles.TextEditCustom;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.LogService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class DeltalleTaskAct extends Activity {
    Context mContext;
    private LinearLayout lnDet;
    private ComponentName mService;
    private List<String> itemsEstado = new ArrayList<String>();
    private List<String> itemsClasificacion = new ArrayList<String>();
    private List<String> itemsFalla = new ArrayList<String>();
    private Button btnSend;
    private Button btnPhoto,btnPhotoFin;
    private Tarea ticket;
    private Tarea ticketCerrar;
    private ConfigPantalla cPant;
    private ArrayList<Tabla> tabFalla;
    private ArrayList<Tabla> tabEstado;
    private ArrayList<PlantillaTipoTarea> Plantilla;
    //foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    public DeltalleTaskAct(Context context) {
        mContext = context;
    }

    public DeltalleTaskAct() {
        this.mContext = null;
    }


    private String codFalla,comentarios,  id_bitacora, latitud,longitud;
    private int id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2,id_archivo;

    private  View view, contentView;


    public static DeltalleTask getInstance(Context context) {
        DeltalleTask pendingtask = new DeltalleTask();
        pendingtask.mContext = context;
        return pendingtask;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_ticket);
        DBManager.setContext(this);
        ticket = (Tarea)getIntent().getExtras().getSerializable("task");
        ticketCerrar = (Tarea)getIntent().getExtras().getSerializable("task");
        cPant = (ConfigPantalla) getIntent().getExtras().getSerializable("Config");
        Plantilla = new ArrayList<PlantillaTipoTarea>();
        Plantilla = DBManager.getManager().getPlantillaTT(cPant.idSubTipoTarea);
        final EditText txtComent = (EditText) findViewById(R.id.txtObserva);
        lnDet = (LinearLayout) findViewById(R.id.lnDet);
        lnDet.removeAllViews();


        //Detalle generado
        ArrayList<TareaDetalle> TDs = new ArrayList<TareaDetalle>();
        TDs = ticket.Detalle;
        for (int i = 0; i <TDs.size(); i++) {
            TareaDetalle DetTask = TDs.get(i);
            LinearLayout aRow = null;
            String VistaCampo="V";
            PlantillaTipoTarea CamposConfig = new PlantillaTipoTarea();
            for (int j = 0; j <Plantilla.size(); j++) {
                if(DetTask.idCampo.equals(Plantilla.get(j).idCampo)){
                    VistaCampo = Plantilla.get(j).accionCampo;
                    CamposConfig = Plantilla.get(j);
                }
            }
            if(VistaCampo.equals("E"))
                aRow = (LinearLayout) View.inflate(this, R.layout.row_formfragmenttext, null);
            if(VistaCampo.equals("C"))
                aRow = (LinearLayout) View.inflate(this, R.layout.row_formfragmentddl, null);
            if(VistaCampo.equals("V"))
                aRow = (LinearLayout) View.inflate(this, R.layout.row_formfragment, null);
            if(VistaCampo.equals("S"))
                aRow = (LinearLayout) View.inflate(this, R.layout.row_formfragmentswitch, null);

            LinearLayout.LayoutParams paramSet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramSet.topMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.leftMargin = (int) getResources().getDimension(R.dimen.space_5);
            paramSet.rightMargin = (int) getResources().getDimension(R.dimen.space_5);
            TextView txtfield2;
            EditText txtfield2Edit;
            Switch mSwitchGen;

            TextView txtfield1 = (TextView) aRow.findViewById(R.id.NombreCampo);

            txtfield1.setText(CamposConfig.NombreCampo);
            if(VistaCampo.equals("E")) {
                txtfield2Edit = (EditText) aRow.findViewById(R.id.ValorCampotxt);
                txtfield2Edit.setText(DetTask.ValorCampo);
                txtfield2Edit.addTextChangedListener(new TextEditCustom(txtfield2Edit,DetTask.idCampo){
                    @Override
                    public void afterTextChanged(Editable s) {
                        for (TareaDetalle DT : ticketCerrar.Detalle) {
                            if (DT.idCampo.equals(this.idcampo))
                                DT.ValorCampo = s.toString();
                        }
                    }
                });
                if(CamposConfig.tipoCampo.equals("N"))
                    txtfield2Edit.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if(VistaCampo.equals("C")) {
                Spinner ddlGen = (Spinner)aRow.findViewById(R.id.ddlgen);
                ArrayList<Tabla> tabGen = new ArrayList<Tabla>();

                tabGen = DBManager.getManager().getTablas(CamposConfig.tablaCampo,"","","");
                CustomAdapterDll customAdapterGen=new CustomAdapterDll(this,tabGen);
                ddlGen.setAdapter(customAdapterGen);

                ddlGen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Tabla selectC = tabEstado.get(position);
                        for (TareaDetalle DT : ticketCerrar.Detalle) {
                            if (DT.idCampo.equals("id_estado"))
                                DT.ValorCampo = selectC.idCampo;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
            }

            if(VistaCampo.equals("V"))  {
                txtfield2 = (TextView) aRow.findViewById(R.id.ValorCampo);
                txtfield2.setText(DetTask.ValorCampo);

            }
            if(VistaCampo.equals("S"))  {
                mSwitchGen = (Switch) aRow.findViewById(R.id.swgen);
                mSwitchGen.setChecked(Boolean.parseBoolean(DetTask.ValorCampo));
                mSwitchGen.setText(DetTask.idCampo);
                mSwitchGen.setShowText(false);
                mSwitchGen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        Switch switchButton = (Switch) buttonView;
                        for (TareaDetalle DT : ticketCerrar.Detalle) {
                            if (DT.idCampo.equals(switchButton.getText()))
                                DT.ValorCampo = isChecked?"true":"false";
                        }
                    }
                });
            }
            lnDet.addView(aRow);
        }


        id_doc = ticket.id_doc;
        id_resolucion = 0;//Integer.parseInt(ticket.id_resolucion);
        bp1 = 0;
        bp2 = 0;
        id_archivo =0;

        btnPhoto = (Button) findViewById(R.id.photobtntnini) ;
        btnPhotoFin = (Button) findViewById(R.id.photobtntnfin) ;

        if(ticket.foto_ini)
            btnPhoto.setVisibility(View.VISIBLE);
        else
            btnPhoto.setVisibility(View.INVISIBLE);

        if(ticket.foto_fin)
            btnPhotoFin.setVisibility(View.VISIBLE);
        else
            btnPhotoFin.setVisibility(View.INVISIBLE);

        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //dispatchTakePictureIntent();
                //fr.setArguments(args);
                Intent intent = new Intent(getApplication(), PhotoSlider.class);
                ticket.Fotos = DBManager.getManager().getTareasFotosTipo(ticket.idTarea,1);
                intent.putExtra("task",ticket);
                intent.putExtra("Config",cPant);
                intent.putExtra("Tipo",1);
                startActivity(intent);
            }
        });

        btnPhotoFin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //dispatchTakePictureIntent();
                //fr.setArguments(args);
                Intent intent = new Intent(getApplication(), PhotoSlider.class);
                ticket.Fotos = DBManager.getManager().getTareasFotosTipo(ticket.idTarea,2);
                intent.putExtra("task",ticket);
                intent.putExtra("Config",cPant);
                intent.putExtra("Tipo",2);
                startActivity(intent);


            }
        });

        btnSend = (Button) findViewById(R.id.sendbtn) ;
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardar
                boolean valid = false;
                if(ticket.foto_ini) {
                    if (ticket.Fotos.size() == 0)
                        Snackbar.make(contentView, "Debe Tomar Fotografía Inicial..", Snackbar.LENGTH_LONG).show();
                    else
                        valid = true;
                }
                if(ticket.foto_fin ){
                    if(ticket.Fotos.size()==0)
                        Snackbar.make(contentView, "Debe Tomar Fotografía Final..", Snackbar.LENGTH_LONG).show();
                    else
                        valid = true;
                }
                if(valid) {
                    if (ticket.wkf) {
                        comentarios = txtComent.getText().toString();
                        if (comentarios.equals("")) {
                            contentView = findViewById(android.R.id.content);
                            ticket.comentario = comentarios;
                            Snackbar.make(contentView, "Debe indicar un comentario..", Snackbar.LENGTH_LONG).show();
                        } else {
                            ticketCerrar.comentario = comentarios;
                            ticketCerrar.idSubTipoTarea = 2;
                            DBManager.getManager().insertTarea(ticketCerrar);
                            TareaDetalle TD = new TareaDetalle(ticket.idTarea, "id_usuario", String.valueOf(Common.getInstance().getLoginUser().userid), false);
                            ticketCerrar.Detalle.add(TD);
                            DBManager.getManager().setUpdateTareaClose(ticketCerrar);
                            setService(ticket.idTarea, "CTRL RUTA");
                            NetworkManager.getManager().UploadTarea(ticketCerrar);
                            finish();
                        }
                    } else {
                        ticketCerrar.idSubTipoTarea = 2;
                        DBManager.getManager().insertTarea(ticketCerrar);
                        TareaDetalle TD = new TareaDetalle(ticket.idTarea, "id_usuario", String.valueOf(Common.getInstance().getLoginUser().userid), false);
                        ticketCerrar.Detalle.add(TD);
                        DBManager.getManager().setUpdateTareaClose(ticketCerrar);
                        setService(ticket.idTarea, "CTRL RUTA");
                        NetworkManager.getManager().UploadTarea(ticketCerrar);
                        finish();
                    }
                }
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


        LinearLayout LL = (LinearLayout) findViewById(R.id.cont_resp_wkf);
        if(ticket.wkf)
            LL.setVisibility(View.VISIBLE);
        else
            LL.setVisibility(View.INVISIBLE);
        Spinner ddpEstado = (Spinner)findViewById(R.id.cboEstado);
        tabEstado = new ArrayList<Tabla>();

        tabEstado = DBManager.getManager().getTablas("Estados","id_wkf",idwkf,idestado);
        CustomAdapterDll customAdapter=new CustomAdapterDll(this,tabEstado);
        ddpEstado.setAdapter(customAdapter);

        ddpEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Tabla selectC = tabEstado.get(position);
                for (TareaDetalle DT : ticketCerrar.Detalle) {
                    if (DT.idCampo.equals("id_estado"))
                        DT.ValorCampo = selectC.idCampo;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final Spinner ddpfalla = (Spinner)findViewById(R.id.cboFalla);

        Spinner ddpClasifcacion = (Spinner)findViewById(R.id.cboClasificacion);

        ArrayList<Tabla> tabClasificacion = new ArrayList<Tabla>();

        tabClasificacion = DBManager.getManager().getTablas("Clasificacion","","","");

        CustomAdapterDll customAdapterCla=new CustomAdapterDll(this,tabClasificacion);
        ddpClasifcacion.setAdapter(customAdapterCla);

        final ArrayList<Tabla> finalTabClasificacion = tabClasificacion;
        ddpClasifcacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Tabla selectC = finalTabClasificacion.get(position);
                for (TareaDetalle DT : ticketCerrar.Detalle) {
                    if (DT.idCampo.equals("idClasificacion"))
                        DT.ValorCampo = selectC.idCampo;
                }
                String vv ="";
                tabFalla = new ArrayList<Tabla>();
                DBManager.setContext(getApplicationContext());
                tabFalla = DBManager.getManager().getTablas("Falla","Clasificacion",selectC.idCampo.toString(),"");


                CustomAdapterDll customAdapterFalla=new CustomAdapterDll(getApplicationContext(),tabFalla);
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
                for (TareaDetalle DT : ticketCerrar.Detalle) {
                    if (DT.idCampo.equals("CodFalla"))
                        DT.ValorCampo = selectC.idCampo;
                }
                String vv ="";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onDestroy() {

        super.onDestroy();

    }

    private void upload() {
        // Image location URL

        // Image
        // Image
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //ByteArrayOutputStream bao = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        //byte[] ba = bao.toByteArray();
        //ba1 = Base64.encodeToString(ba,Base64.DEFAULT);

        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ticket.idTarea+".jpg";
        NetworkManager.getManager().uploadFileMro(timeStamp,mCurrentPhotoPath);

        // Upload image to server
        //new uploadToServer().execute();

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

        Intent service = new Intent(getApplicationContext(), LogService.class);
        service.putExtra("idTT", String.valueOf(nTaskID));
        service.putExtra("description", description);
        mService = getApplicationContext().startService(service);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }



}
