package com.vendomatica.vendroid;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Fragment.CompletedTask;
import com.vendomatica.vendroid.Fragment.Fragment1;
import com.vendomatica.vendroid.Fragment.Fragment2;
import com.vendomatica.vendroid.Fragment.Fragment3;
import com.vendomatica.vendroid.Fragment.PendingTask;
import com.vendomatica.vendroid.Fragment.PhotoDialog;
import com.vendomatica.vendroid.db.Sincro;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.UploadService;
import com.vendomatica.vendroid.viewholder.CaptureViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vendomatica.vendroid.R.id.lnImages;

public class MainActivity extends AppCompatActivity {

    //@BindView(R.id.toolbar)
    private Toolbar toolbar;
    private ScrimInsetsFrameLayout sifl;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView ndList;
    private Fragment fragment = null;
    private ComponentName mUploadService;
    private int numTab = 1;
    private ProgressDialog mProgDlg;
    private TextView txtnombre;
    private View contentView; // =this.findViewById(android.R.id.content);


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void Iniciar() {
        Intent intent = new Intent(MainActivity.this, MainIntroActivity.class);
        intent.putExtra(MainIntroActivity.EXTRA_FULLSCREEN, true);
        intent.putExtra(MainIntroActivity.EXTRA_SCROLLABLE, true);
        intent.putExtra(MainIntroActivity.EXTRA_CUSTOM_FRAGMENTS, true);
        intent.putExtra(MainIntroActivity.EXTRA_PERMISSIONS, true);
        intent.putExtra(MainIntroActivity.EXTRA_SKIP_ENABLED, true);
        intent.putExtra(MainIntroActivity.EXTRA_SHOW_BACK, false);
        intent.putExtra(MainIntroActivity.EXTRA_SHOW_NEXT, false);
        intent.putExtra(MainIntroActivity.EXTRA_FINISH_ENABLED, true);
        intent.putExtra(MainIntroActivity.EXTRA_GET_STARTED_ENABLED, true);

        SharedPreferences prefs = getSharedPreferences(Common.PREF_KEY_USERDATA, MODE_PRIVATE);

        if (!prefs.getBoolean("login", false) || prefs.getString("userid", "") == "" || prefs.getString("password", "") == "") {
            startActivity(intent);
        } else {

            Snackbar.make(contentView, "Cargando su información...", Snackbar.LENGTH_LONG).show();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.loading);
            progressDialog.setCancelable(false);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Common.getInstance().setLoginUser(NetworkManager.getManager().login(prefs.getString("userid", ""), prefs.getString("password", "")));
            Sincro.getManager().setContext(this.getApplication().getApplicationContext());
            if (Sincro.getManager().Sincronizar()) {
                //txtnombre.setText(Common.getInstance().getLoginUser().getLastName()+", " +Common.getInstance().getLoginUser().getFirstName());
                fragment = new PendingTask();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
            }
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);
        contentView = this.findViewById(android.R.id.content);
        txtnombre = (TextView) findViewById(R.id.txtNombre);
        setSupportActionBar(toolbar);
        if (isConnected()) {
            Iniciar();
        }
        sifl = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        // Tab
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.addTab(tabs.newTab().setText("Pendientes"), true);
        tabs.addTab(tabs.newTab().setText("Completados"));

        //set icon
        //tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_portátil));

        tabs.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getText() == "Pendientes") {
                            fragment = new PendingTask();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();
                        }
                        if (tab.getText() == "Completados") {
                            fragment = new CompletedTask();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        // ...
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }


                }
        );

        //Menu del Navigation Drawer

        ndList = (ListView) findViewById(R.id.navdrawerlist);

        final String[] opciones = new String[]{"Sincronizar", "Salir"};

        ArrayAdapter<String> ndMenuAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_activated_1, opciones);

        ndList.setAdapter(ndMenuAdapter);

        ndList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {


                switch (pos) {
                    case 0:
                        Snackbar.make(contentView, "Sincronizando..", Snackbar.LENGTH_LONG).show();
                        Sincro.getManager().setContext(getApplicationContext());
                        Sincro.getManager().Sincronizar();
                        fragment = new PendingTask();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();
                        break;
                    case 1:
                        Snackbar.make(contentView, "Cerrando sessión..", Snackbar.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = getSharedPreferences(Common.PREF_KEY_USERDATA, MODE_PRIVATE).edit();

                        //SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove("login");
                        editor.remove("userid");
                        editor.remove("password");
                        editor.remove("firstname");
                        editor.remove("lastname");
                        editor.clear();
                        editor.commit();
                        Iniciar();
                        //getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        break;
                }


                ndList.setItemChecked(pos, true);

                //getSupportActionBar().setTitle(opciones[pos]);

                drawerLayout.closeDrawer(sifl);
            }
        });

        //Drawer Layout

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary_dark));

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        /*startIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainIntroActivity.class);
                intent.putExtra(MainIntroActivity.EXTRA_FULLSCREEN, optionFullscreen.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_SCROLLABLE, optionScrollable.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_CUSTOM_FRAGMENTS, optionCustomFragments.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_PERMISSIONS, optionPermissions.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_SKIP_ENABLED, optionSkipEnabled.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_SHOW_BACK, optionShowBack.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_SHOW_NEXT, optionShowNext.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_FINISH_ENABLED, optionFinishEnabled.isChecked());
                intent.putExtra(MainIntroActivity.EXTRA_GET_STARTED_ENABLED, optionGetStartedEnabled.isChecked());
                startActivity(intent);
            }
        });

        startCanteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CanteenIntroActivity.class);
                startActivity(intent);
            }
        });

        startSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });*/
    }

    class UploadThread extends Thread {

        @Override
        public void run() {
            Sincro.getManager().setContext(getApplicationContext());
            Sincro.getManager().Sincronizar();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {
        if (numTab == 1) {
            fragment = new PendingTask();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else {
            fragment = new CompletedTask();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        //txtnombre.setText(Common.getInstance().getLoginUser().getLastName()+", " +Common.getInstance().getLoginUser().getFirstName());
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DetailTicket");
//        fragment.onActivityResult(requestCode, resultCode, data);
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
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".jpg";
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


}
