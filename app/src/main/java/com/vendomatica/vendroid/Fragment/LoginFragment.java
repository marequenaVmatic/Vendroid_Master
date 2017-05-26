package com.vendomatica.vendroid.Fragment;


import android.content.ComponentName;
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

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.net.Sincro;

public class LoginFragment extends SlideFragment {

    private EditText fakeUsername;

    private EditText fakePassword;

    private Button fakeLogin;

    private boolean loggedIn = false;

    private Handler loginHandler = new Handler();

    private View contentView;

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
        contentView = getActivity().findViewById(android.R.id.content);
        fakeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fakeUsername.setEnabled(false);
                //fakePassword.setEnabled(false);
                //fakeLogin.setEnabled(false);
                //fakeLogin.setText(R.string.label_fake_login_loading);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //Common.getInstance().setLoginUser(NetworkManager.getManager().login(fakeUsername.getText().toString(), fakePassword.getText().toString()));
                User muser = new User();
                //muser = NetworkManager.getManager().login("guillermo.rodriguez", "guillermo");
                Snackbar.make(contentView, "Validando su información...", Snackbar.LENGTH_INDEFINITE).show();
                muser = NetworkManager.getManager().login(fakeUsername.getText().toString(), fakePassword.getText().toString());
                if(muser!=null) {
                    if (muser.userid > 0) {

                        //muser.saveObject(muser);
                        Common.getInstance().setUser(muser);
                        loggedIn = true;

                        Sincro.getManager().setContext(getContext());
                        if (Sincro.getManager().SincronizarDownload(muser, false)) {
                            Snackbar.make(contentView, "Sincronización Exitosa..", Snackbar.LENGTH_LONG).show();
                            nextSlide();
                            //   updateNavigation();
                            loggedIn = true;
                        } else {
                            Snackbar.make(contentView, "Falla en Sincronización consultar al departamento de TI.", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(contentView, "Usuario o contraseña incorrecto.", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else{
                    Snackbar.make(contentView, "Sin acceso a los servidores, trabajo local", Snackbar.LENGTH_INDEFINITE).show();
                    muser = NetworkManager.getManager().loginLocal(fakeUsername.getText().toString(), fakePassword.getText().toString());
                    if (muser.userid > 0) {

                        //muser.saveObject(muser);
                        Common.getInstance().setUser(muser);
                        loggedIn = true;


                        Sincro.getManager().setContext(getContext());
                        if (Sincro.getManager().SincronizarDownload(muser, false)) {
                            Snackbar.make(contentView, "Sincronización Exitosa..", Snackbar.LENGTH_LONG).show();
                            nextSlide();
                            //   updateNavigation();
                            loggedIn = true;
                        } else {
                            Snackbar.make(contentView, "Falla en Sincronización consultar al departamento de TI.", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(contentView, "Usuario o contraseña incorrecto.", Snackbar.LENGTH_SHORT).show();
                    }
                }
//                loginHandler.postDelayed(loginRunnable, 2000);
            }
        });

        return root;
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
