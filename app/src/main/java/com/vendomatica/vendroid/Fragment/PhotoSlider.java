package com.vendomatica.vendroid.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.Fotos;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by miguel_r on 22-05-2017.
 */
public class PhotoSlider extends FragmentActivity {
    static final int NUM_ITEMS = 6;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;

    //foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    static boolean capturePhoto = false;

    private static Tarea ticket;
    private Tarea ticketCerrar;
    private ConfigPantalla cPant;
    private int cTipo;

    private Button btnBack;
    private Button btnPhoto,btnVolver;

    private View contentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo);
        ticket = (Tarea)getIntent().getExtras().getSerializable("task");
        cPant = (ConfigPantalla) getIntent().getExtras().getSerializable("Config");
        cTipo = (int) getIntent().getExtras().getSerializable("Tipo");
//        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
//        contentView = this.findViewById(android.R.id.content);
//        viewPager = (ViewPager) findViewById(R.id.pager);
//        viewPager.setAdapter(imageFragmentPagerAdapter);
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        contentView = this.findViewById(android.R.id.content);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        btnPhoto = (Button) findViewById(R.id.photosave_frag) ;
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dispatchTakePictureIntent();
                //fr.setArguments(args);
            }
        });

        btnVolver = (Button) findViewById(R.id.photoback_frag) ;
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //fr.setArguments(args);
            }
        });
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if(ticket.Fotos.size()>0)
                return ticket.Fotos.size();
            else
                return 1;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int position) {
            return super.instantiateItem(pager, position);
        }


    }
    public void onBackPressed() {
        int vvv = 1;

        DBManager.setContext(getApplicationContext());
        DBManager.getManager().DeleteFotosTarea(ticket.idTarea);
        for (int i = 0; i <ticket.Fotos.size(); i++) {
            DBManager.getManager().insertTareaFoto(ticket.Fotos.get(i));
        }
        super.onBackPressed();
    }
    public static class SwipeFragment extends Fragment {
        private ImageView imageView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            //setImage(position);
            //String imageFileName = IMAGE_NAME[position];
            //int imgResId = getResources().getIdentifier(imageFileName, "drawable", "com.vendomatica.vendroid");
            //imageView.setImageResource(imgResId);
            if(ticket.Fotos.size()>0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                int targetW = 900;
                int targetH = 900;
                // Get the dimensions of the bitmap
                bmOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(ticket.Fotos.get(position).fotoNombre, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                Bitmap bitmap2 = BitmapFactory.decodeFile(ticket.Fotos.get(position).fotoNombre, bmOptions);
                //Bitmap  bitmap = Bitmap.createScaledBitmap(bitmap2,100,100,true);
                imageView.setImageBitmap(bitmap2);
            }
            return swipeView;
        }

        @Override
        public void onResume(){
            super.onResume();

        }
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            int vvv= this.getId();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int vvv= this.getId();
            int mNum = getArguments() != null ? getArguments().getInt("position") : 1;
        }






        private void setImage(int position){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            //bmOptions.inJustDecodeBounds = true;
            if(ticket.Fotos.size()>0) {

                    int targetW = 900;
                    int targetH = 900;
                    // Get the dimensions of the bitmap
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(ticket.Fotos.get(position).fotoNombre, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                    Bitmap bitmap2 = BitmapFactory.decodeFile(ticket.Fotos.get(position).fotoNombre, bmOptions);
                    //Bitmap  bitmap = Bitmap.createScaledBitmap(bitmap2,100,100,true);
                    imageView.setImageBitmap(bitmap2);
                }
                //imageView.setImageBitmap(bitmap);
        }
        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
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
                String mex = ex.getMessage();
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
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +"";
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        //ImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String loca= mCurrentPhotoPath;
            Fotos mfoto = new Fotos(ticket.idTarea,mCurrentPhotoPath,cTipo,false);
            ticket.Fotos.add(mfoto);
            capturePhoto=true;
            imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
            contentView = this.findViewById(android.R.id.content);
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(imageFragmentPagerAdapter);
            //ImageView mImageView = (ImageView)findViewById(R.id.photoImage);
            //mImageView.setImageBitmap(imageBitmap);
        }
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
}