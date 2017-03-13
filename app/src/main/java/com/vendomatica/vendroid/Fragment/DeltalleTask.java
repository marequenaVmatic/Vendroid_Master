package com.vendomatica.vendroid.Fragment;
/*
This is the pending task list of the main screen.
when the user click the pending task, the event appears in this files.
 */
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.Model.Estado;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.LogService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class DeltalleTask extends Fragment {
    Context mContext;
    private LinearLayout lnTasks;
    private ComponentName mService;
    private List<String> itemsEstado = new ArrayList<String>();
    private List<String> itemsClasificacion = new ArrayList<String>();
    private List<String> itemsFalla = new ArrayList<String>();
    private Button btnSend;
    private Button btnPhoto;
    private Tickets ticket;
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

        ticket = (Tickets) getArguments().getSerializable("task");
        String rrr = getArguments().getString("taskid");

        TextView txtRuta = (TextView) view.findViewById(R.id.txtRutaA);
        TextView txtSerieM = (TextView) view.findViewById(R.id.txtSerieM);
        TextView txtLlamador = (TextView) view.findViewById(R.id.txtLlamador);
        TextView txtTlf = (TextView) view.findViewById(R.id.txtTlf);
        TextView txtTicket = (TextView) view.findViewById(R.id.txtTicket);
        TextView txtcoment = (TextView) view.findViewById(R.id.txtComent);
        final EditText txtComent = (EditText) view.findViewById(R.id.txtObserva);
        txtRuta.setText(ticket.RutaAbastecimiento);
        txtSerieM.setText(ticket.SerieMaquina);
        txtLlamador.setText(ticket.ContactoLlamada);
        txtTlf.setText(ticket.ContactoFono);
        txtTicket.setText(String.valueOf(ticket.id_doc));
        txtcoment.setText(ticket.Observacion);
        id_bitacora = String.valueOf(ticket.id_bitacora);
        id_wkf = ticket.id_wkf;
        id_usuario =Integer.parseInt(Common.getInstance().getLoginUser().getUserId());
        id_doc = ticket.id_doc;
        id_resolucion = 0;//Integer.parseInt(ticket.id_resolucion);
        bp1 = 0;
        bp2 = 0;
        id_archivo =0;

        btnPhoto = (Button) view.findViewById(R.id.photobtntn) ;
        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
                //((MainActivity)getActivity()).dispatchTakePictureIntent();


            }
        });


        btnSend = (Button) view.findViewById(R.id.sendbtn) ;
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                comentarios = txtComent.getText().toString();
                if (comentarios.equals("")) {
                    View contentView = getActivity().findViewById(android.R.id.content);
                    Snackbar.make(contentView, "Debe indicar un comentario..", Snackbar.LENGTH_LONG).show();
                } else if (NetworkManager.getManager().saveBitacora(codFalla, id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2, comentarios, id_archivo, id_bitacora, latitud, longitud) > 0) {
                    Common.getInstance().updateTab(getActivity());
                    PendingTask fragment = new PendingTask();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();

                }
            }
        });


        Common.getInstance().arrEstado.clear();
        NetworkManager.getManager().loadEstado(Common.getInstance().arrEstado,ticket.id_estado,ticket.id_wkf);
        NetworkManager.getManager().loadClasificacion(Common.getInstance().arrClasificacion);
        DBManager.getManager().insertEstados(Common.getInstance().arrEstado);
        //DBManager.getManager().insertClasificacion(Common.getInstance().arrClasificacion);


        Spinner ddpEstado = (Spinner)view.findViewById(R.id.cboEstado);
        for(int i=0; i<Common.getInstance().arrEstado.size(); i++) {
            itemsEstado.add(Common.getInstance().arrEstado.get(i).Estado);
        }

        CustomAdapterDll customAdapter=new CustomAdapterDll(getContext(),itemsEstado);
        ddpEstado.setAdapter(customAdapter);

        ddpEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                id_estado =Common.getInstance().arrEstado.get(position).id_estado;
                //Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final Spinner ddpfalla = (Spinner)view.findViewById(R.id.cboFalla);

        Spinner ddpClasifcacion = (Spinner)view.findViewById(R.id.cboClasificacion);
        for(int i=0; i<Common.getInstance().arrClasificacion.size(); i++) {
            itemsClasificacion.add(Common.getInstance().arrClasificacion.get(i).ClasificacionFalla);
        }

        CustomAdapterDll customAdapterCla=new CustomAdapterDll(getContext(),itemsClasificacion);
        ddpClasifcacion.setAdapter(customAdapterCla);

        ddpClasifcacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String vvv = itemsClasificacion.get(position).toString();
                Common.getInstance().arrFalla.clear();
                NetworkManager.getManager().loadFalla(Common.getInstance().arrFalla,Common.getInstance().arrClasificacion.get(position).CodClasificacionFalla);
                itemsFalla.clear();
                for(int i=0; i<Common.getInstance().arrFalla.size(); i++) {
                    itemsFalla.add(Common.getInstance().arrFalla.get(i).Nombre);
                }
                CustomAdapterDll customAdapterFalla=new CustomAdapterDll(getContext(),itemsFalla);
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
                String vvv = itemsFalla.get(position).toString();
                codFalla = Common.getInstance().arrFalla.get(position).CodFalla;
                //NetworkManager.getManager().loadFalla(Common.getInstance().arrFalla,Common.getInstance().arrClasificacion.get(position).CodClasificacionFalla);
                //Log.v("item", (String) parent.getItemAtPosition(position));
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
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String loca= mCurrentPhotoPath;

            ImageView mImageView = (ImageView)view.findViewById(R.id.photoImage);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),"com.vendomatica.vendroid.provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".jpg";
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        //int targetW = mImageView.getWidth();
        //int targetH = mImageView.getHeight();
        int targetW = 100;
        int targetH = 100;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //mImageView.setImageBitmap(bitmap);
    }

}
