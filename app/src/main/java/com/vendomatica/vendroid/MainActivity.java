package com.vendomatica.vendroid;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Fragment.TabListFragment;
import com.vendomatica.vendroid.Model.ConfigPantalla;
import com.vendomatica.vendroid.Model.LocationLoader;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.TaskBackground.CtrlBackground;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.Sincro;
import com.vendomatica.vendroid.services.TBackground;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private  User muser;
    private  ArrayList<ConfigPantalla> Pants;
    private  TabLayout tabs;
    private ConfigPantalla cPant = new ConfigPantalla();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private String nombre="", apellido="";
    String mCurrentPhotoPath;

    private Location mNewLocation;
    LocationLoader mLocationLoader;

    //CTRL RUTa
    final Handler handlerCTR = new Handler();
    Timer timerCTR = new Timer();

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
        muser = new User();
        muser = muser.getObject(muser);
        if (muser==null) {
            startActivity(intent);
        } else {
            if (muser.userid==0)
                startActivity(intent);
            else {
                Snackbar.make(contentView, "Cargando su información...", Snackbar.LENGTH_LONG).show();
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.loading);
                progressDialog.setCancelable(false);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                progressDialog.dismiss();
            }
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
        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //GpsInfo info = new GpsInfo(getApplicationContext());
                            TBackground NotifyBack = new TBackground();
                            NotifyBack.mcontext = getApplicationContext();
                            NotifyBack.execute();
                        } catch (Exception e) {
                            //Log.e("error", e.getMessage());
                        }
                    }
                });
            }
        };
        //tarea background cada 30 minutos
        //timer.schedule(task, 0, 1800000);
        timer.schedule(task, 0, 60000);


        loadCTR();

        TimerTask taskCTR = new TimerTask() {
            @Override
            public void run() {
                handlerCTR.post(new Runnable() {
                    public void run() {
                        try {
                            //GpsInfo info = new GpsInfo(getApplicationContext());
                            loadCTR();

                        } catch (Exception e) {
                            //Log.e("error", e.getMessage());
                        }
                    }
                });
            }
        };
        //tarea background cada 30 minutos
        //timer.schedule(task, 0, 1800000);
        timerCTR.schedule(taskCTR, 0, 90000);
        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.appbar);
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        toolbar.setNavigationIcon(drawable);
        setSupportActionBar(toolbar);




        //setSupportActionBar(toolbar);

        // Tab
       tabs = (TabLayout) findViewById(R.id.tabs);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabs.addTab(tabs.newTab().setText("Pendientes"), true);
        //tabs.addTab(tabs.newTab().setText("Completados"));
       loadtabs();
        //set icon
        //tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_portátil));

        tabs.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        for (int n = 0; n <Pants.size(); n++) {
                             ConfigPantalla Pant = new ConfigPantalla();
                            Pant = Pants.get(n);
                            if(Pant.NombreTipoPantalla.equals(tab.getText())){
                                if(Pant.DescTipoPantalla.equals("TAB")){

                                    fragment = new TabListFragment();
                                    Bundle args = new Bundle();
                                    args.putSerializable("Config", Pant);
                                    fragment.setArguments(args);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame, fragment)
                                            .commit();
                                }
                            }
                        }
//                        if (tab.getText().equals("Completados")) {
//                            fragment = new CompletedTask();
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.content_frame, fragment)
//                                    .commit();
//                        }
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

        for (int n = 0; n <Pants.size(); n++) {
            ConfigPantalla Pant = new ConfigPantalla();
            Pant = Pants.get(n);
            if(Pant.NombreTipoPantalla.equals(tabs.getTabAt(0).getText())){
                if(Pant.DescTipoPantalla.equals("TAB")){
                    fragment = new TabListFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("Config", Pant);
                    fragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        }
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
                        Sincro.getManager().SincronizarDownload(muser,false);
                        Sincro.getManager().SincronizarFiles();
//                        fragment = new TabListFragment();
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.content_frame, fragment)
//                                .commit();
                        break;
                    case 1:
                        User muser = new User();
                            muser.saveObject(muser);

                        Iniciar();
                        //getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        break;
                }


                ndList.setItemChecked(pos, true);

                //getSupportActionBar().setTitle("sdfasfadsfasdfasdfasdf");

                drawerLayout.closeDrawer(sifl);
            }
        });

        //Drawer Layout

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary_dark));

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openDrawer,
                R.string.closeDrawer
        ){

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
        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

    }

    private void loadtabs(){
        //if(tabs.getTabCount()==0) {
            Pants = new ArrayList<ConfigPantalla>();
            DBManager.setContext(getApplicationContext());
            Pants = DBManager.getManager().getPantallaTAB();
            for (int n = 0; n < Pants.size(); n++) {
                ConfigPantalla Pant = new ConfigPantalla();
                Pant = Pants.get(n);
                boolean existPant = false;
                for(int i = 0; i < tabs.getTabCount(); i++) {
                    if(tabs.getTabAt(i).getText().equals(Pant.NombreTipoPantalla))
                        existPant=true;
                }
                if(!existPant) {
                    tabs.addTab(tabs.newTab().setText(Pant.NombreTipoPantalla), n == 0 ? true : false);
                }
            }
        //}
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

    public void onDestroy() {

        super.onDestroy();

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
//        if (numTab == 1) {
//            fragment = new PendingTask();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, fragment)
//                    .commit();
//        } else {
//            fragment = new CompletedTask();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, fragment)
//                    .commit();
//        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        apellido=Common.getInstance().getLoginUser()==null?"":Common.getInstance().getLoginUser().apepat;
        nombre=Common.getInstance().getLoginUser()==null?"":Common.getInstance().getLoginUser().nombre;
        txtnombre.setText(apellido+", " +nombre);
        loadtabs();

    }



    @Override
    public void onBackPressed() {
       // getSupportFragmentManager().beginTransaction()
       //         .replace(R.id.content_frame, fragment)
       //         .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DetailTicket");
//        fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void loadCTR(){
        MapsInitializer.initialize(this);
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

    }
    private void getLocation() {
        if (mNewLocation == null)
            return;
        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
        CtrlBackground CTRLBack = new CtrlBackground();
        CTRLBack.mcontext = getApplicationContext();
        CTRLBack.execute();
    }

}
