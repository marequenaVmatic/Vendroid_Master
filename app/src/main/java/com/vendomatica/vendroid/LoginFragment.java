package com.vendomatica.vendroid;


import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.db.Sincro;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.UploadService;

public class LoginFragment extends SlideFragment {

    private EditText fakeUsername;

    private EditText fakePassword;

    private Button fakeLogin;

    private boolean loggedIn = false;

    private Handler loginHandler = new Handler();

    SharedPreferences.Editor ed;
    SharedPreferences sp;

    private ComponentName mUploadService;


    private Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
//            fakeLogin.setText(R.string.label_fake_login_success);
//            Toast.makeText(getContext(), R.string.label_fake_login_success_message, Toast.LENGTH_SHORT).show();
//
            loggedIn = true;
            updateNavigation();
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        fakeUsername = (EditText) root.findViewById(R.id.fakeUsername);
        fakePassword = (EditText) root.findViewById(R.id.fakePassword);
        fakeLogin = (Button) root.findViewById(R.id.fakeLogin);

        fakeUsername.setEnabled(!loggedIn);
        fakePassword.setEnabled(!loggedIn);
        fakeLogin.setEnabled(!loggedIn);

        fakeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fakeUsername.setEnabled(false);
                //fakePassword.setEnabled(false);
                //fakeLogin.setEnabled(false);
                //fakeLogin.setText(R.string.label_fake_login_loading);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Common.getInstance().setLoginUser(NetworkManager.getManager().login(fakeUsername.getText().toString(), fakePassword.getText().toString()));
                //fakeUsername.setText("miguel.ortega");
                //fakePassword.setText("suzukialto");
                //Common.getInstance().setLoginUser(NetworkManager.getManager().login("miguel.ortega", "suzukialto"));
                if(Common.getInstance().getLoginUser() != null) {
                    loggedIn = true;
                    View contentView =getActivity().findViewById(android.R.id.content);
                    Snackbar.make(contentView, "Cargando su información...", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getContext(), "Cargando su información.", Toast.LENGTH_SHORT).show();
                    //SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Common.PREF_KEY_USERDATA, getContext().MODE_PRIVATE).edit();

                    //SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("login", true);
                    editor.putString("userid", fakeUsername.getText().toString());
                    editor.putString("password", fakePassword.getText().toString());
                    editor.putString("firstname", Common.getInstance().getLoginUser().getFirstName());
                    editor.putString("lastname", Common.getInstance().getLoginUser().getLastName());
                    editor.putLong(Common.PREF_KEY_CLOSEDTIME, System.currentTimeMillis());
                    editor.commit();
                    Common.getInstance().arrTickets.clear();
                    Common.getInstance().arrCompleteTasks.clear();
                    Common.getInstance().arrEstado.clear();
                    Common.getInstance().arrClasificacion.clear();
                    Common.getInstance().arrFalla.clear();
                    Common.getInstance().arrCompleteTinTasks.clear();
                    Common.getInstance().arrCategory.clear();
                    Common.getInstance().arrProducto.clear();
                    Common.getInstance().arrProducto_Ruta.clear();
                    Common.getInstance().arrUsers.clear();
                    Common.getInstance().arrTaskTypes.clear();
                    Common.getInstance().arrMachineCounters.clear();
                    Common.getInstance().arrCompleteDetailCounters.clear();
                    Common.getInstance().arrCommentErrors.clear();
                    //                   DBManager.setContext(getContext());
//                    int nRet = NetworkManager.getManager().loadPublicArray(Common.getInstance().arrTickets);
//                    DBManager.getManager().deleteAlltickets();
//                    DBManager.getManager().insertTickets(Common.getInstance().arrTickets);

                    Sincro.getManager().setContext(getContext());
                    if(Sincro.getManager().Sincronizar()) {
                        Snackbar.make(contentView, "Sincronización Exitosa..", Snackbar.LENGTH_LONG).show();
                        nextSlide();
                     //   updateNavigation();
                        loggedIn = true;
                    }else{
                        Toast.makeText(getContext(), "Falla en Sincronización consultar al departamento de TI.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Usuario o contraseña incorrecto.", Toast.LENGTH_SHORT).show();
                }

//                loginHandler.postDelayed(loginRunnable, 2000);
            }
        });

        return root;
    }
    class UploadThread extends Thread {

        @Override
        public void run() {
            Intent service = new Intent(getContext(), UploadService.class);
            service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
            service.putExtra("userid", "1239");
            Common.getInstance().isUpload = true;
            mUploadService = getContext().startService(service);
        }
    }



    @Override
    public void onDestroy() {
        loginHandler.removeCallbacks(loginRunnable);
        super.onDestroy();
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }
}
