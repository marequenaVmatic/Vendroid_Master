package com.vendomatica.vendroid.Fragment;
/*
This is the pending task list of the main screen.
when the user click the pending task, the event appears in this files.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.MainActivity;
import com.vendomatica.vendroid.MainIntroActivity;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.ScrimInsetsFrameLayout;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.db.Sincro;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.LogService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class DeltalleTaskAct extends Activity {

    //@BindView(R.id.toolbar)
    Context mContext;
    private LinearLayout lnTasks;
    private ComponentName mService;
    private List<String> itemsEstado = new ArrayList<String>();
    private List<String> itemsClasificacion = new ArrayList<String>();
    private List<String> itemsFalla = new ArrayList<String>();
    private Button btnSend;
    private Button btnPhoto;
    private Tickets ticket;
    private ImageView mImageView;
    //foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath="";
    private String ba1;

    private String codFalla,comentarios,  id_bitacora, latitud,longitud;
    private int id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2,id_archivo;

    private  View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_ticket);
        //ButterKnife.bind(this);
        ticket = (Tickets)getIntent().getExtras().getSerializable("task");
        //String rrr = getArguments().getString("taskid");

        TextView txtRuta = (TextView) findViewById(R.id.txtRutaA);
        TextView txtSerieM = (TextView) findViewById(R.id.txtSerieM);
        TextView txtLlamador = (TextView) findViewById(R.id.txtLlamador);
        TextView txtTlf = (TextView) findViewById(R.id.txtTlf);
        TextView txtTicket = (TextView) findViewById(R.id.txtTicket);
        TextView txtcoment = (TextView) findViewById(R.id.txtComent);
        mImageView = (ImageView)findViewById(R.id.photoImage);
        final EditText txtComent = (EditText) findViewById(R.id.txtObserva);
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

        btnPhoto = (Button) findViewById(R.id.photobtntn) ;
        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
                //((MainActivity)getActivity()).dispatchTakePictureIntent();


            }
        });


        btnSend = (Button) findViewById(R.id.sendbtn) ;
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView = findViewById(android.R.id.content);
//                setPic();
//
//                upload();

                comentarios = txtComent.getText().toString();
                if (comentarios.equals("")) {
                    Snackbar.make(contentView, "Debe indicar un comentario..", Snackbar.LENGTH_LONG).show();
                }else if(mCurrentPhotoPath.equals("") ) {
                    Snackbar.make(contentView, "Debe tomar una fotografía", Snackbar.LENGTH_LONG).show();
                } else if (NetworkManager.getManager().saveBitacora(codFalla, id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2, comentarios, id_archivo, id_bitacora, latitud, longitud) > 0) {
                    upload();
                    finish();
                }

            }
        });


        Common.getInstance().arrEstado.clear();
        NetworkManager.getManager().loadEstado(Common.getInstance().arrEstado,ticket.id_estado,ticket.id_wkf);
        NetworkManager.getManager().loadClasificacion(Common.getInstance().arrClasificacion);
        DBManager.getManager().insertEstados(Common.getInstance().arrEstado);
        //DBManager.getManager().insertClasificacion(Common.getInstance().arrClasificacion);


        Spinner ddpEstado = (Spinner)findViewById(R.id.cboEstado);
        for(int i=0; i<Common.getInstance().arrEstado.size(); i++) {
            itemsEstado.add(Common.getInstance().arrEstado.get(i).Estado);
        }

        CustomAdapterDll customAdapter=new CustomAdapterDll(this,itemsEstado);
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

        final Spinner ddpfalla = (Spinner)findViewById(R.id.cboFalla);

        Spinner ddpClasifcacion = (Spinner)findViewById(R.id.cboClasificacion);
        for(int i=0; i<Common.getInstance().arrClasificacion.size(); i++) {
            itemsClasificacion.add(Common.getInstance().arrClasificacion.get(i).ClasificacionFalla);
        }

        CustomAdapterDll customAdapterCla=new CustomAdapterDll(getApplicationContext(),itemsClasificacion);
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
                CustomAdapterDll customAdapterFalla=new CustomAdapterDll(getApplicationContext(),itemsFalla);
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

    }




    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            try {

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                setPic();

            }catch (Exception e){
                e.printStackTrace();
                Log.e("AbaTask", "Failed to scale image!");
            }
            //String loca= mCurrentPhotoPath;


            //mImageView.setImageBitmap(imageBitmap);
        }
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,"com.vendomatica.vendroid.provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +"_"+ticket.id_bitacora+".jpg";
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
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
        bmOptions.inSampleSize = 8;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inScaled=true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
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

        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ticket.id_bitacora+".jpg";
        NetworkManager.getManager().uploadFileMro(timeStamp,mCurrentPhotoPath);

        // Upload image to server
        //new uploadToServer().execute();

    }


    //uploda file mro
    public class uploadToServer extends AsyncTask<Object, Object, Boolean> {



        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", ba1));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(NetworkManager.getManager().URL_UPLOADMROFILE);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return true;

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(true);
        }
    }



}
