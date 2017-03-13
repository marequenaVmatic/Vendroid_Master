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
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.GpsInfo;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.db.DBManager;
import com.vendomatica.vendroid.net.NetworkManager;
import com.vendomatica.vendroid.services.LogService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class DeltalleTaskComplete extends Fragment {
    Context mContext;
    private LinearLayout lnTasks;
    private ComponentName mService;
    private List<String> itemsEstado = new ArrayList<String>();
    private List<String> itemsClasificacion = new ArrayList<String>();
    private List<String> itemsFalla = new ArrayList<String>();
    private Button btnSend;

    public DeltalleTaskComplete(Context context) {
        mContext = context;
    }

    public DeltalleTaskComplete() {
        this.mContext = null;
    }


    private String codFalla,comentarios,  id_bitacora, latitud,longitud;
    private int id_wkf, id_estado, id_usuario, id_doc, id_resolucion, bp1, bp2,id_archivo;

    public static DeltalleTaskComplete getInstance(Context context) {
        DeltalleTaskComplete pendingtask = new DeltalleTaskComplete();
        pendingtask.mContext = context;
        return pendingtask;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.detalle_ticketcerrado, null);
        Tickets ticket = (Tickets) getArguments().getSerializable("task");
        String rrr = getArguments().getString("taskid");

        TextView txtRuta = (TextView) view.findViewById(R.id.txtRutaA);
        TextView txtSerieM = (TextView) view.findViewById(R.id.txtSerieM);
        TextView txtLlamador = (TextView) view.findViewById(R.id.txtLlamador);
        TextView txtTlf = (TextView) view.findViewById(R.id.txtTlf);
        TextView txtTicket = (TextView) view.findViewById(R.id.txtTicket);

        txtRuta.setText(ticket.RutaAbastecimiento);
        txtSerieM.setText(ticket.SerieMaquina);
        txtLlamador.setText(ticket.ContactoLlamada);
        txtTlf.setText(ticket.ContactoFono);
        txtTicket.setText(String.valueOf(ticket.id_doc));

        id_bitacora = String.valueOf(ticket.id_bitacora);
        TextView txtFalla = (TextView) view.findViewById(R.id.txtFalla);
        txtFalla.setText(ticket.Falla);
        TextView txtObsv = (TextView) view.findViewById(R.id.txtObserva);
        txtObsv.setText(ticket.Observacion);
        TextView txtEstado = (TextView) view.findViewById(R.id.txtEstado);
        txtEstado.setText(ticket.EstadoInicial);



        btnSend = (Button) view.findViewById(R.id.closedbtn) ;
        btnSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                   CompletedTask fragment = new CompletedTask();
                   FragmentTransaction ft = getFragmentManager().beginTransaction();
                   ft.replace(R.id.content_frame, fragment);
                   ft.commit();
            }
        });


        Common.getInstance().arrEstado.clear();
        NetworkManager.getManager().loadEstado(Common.getInstance().arrEstado,ticket.id_estado,ticket.id_wkf);
        NetworkManager.getManager().loadClasificacion(Common.getInstance().arrClasificacion);
        DBManager.getManager().insertEstados(Common.getInstance().arrEstado);
        //DBManager.getManager().insertClasificacion(Common.getInstance().arrClasificacion);




        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            Intent i = new Intent();
            i.setComponent(mService);
            mContext.stopService(i);
        }
    }

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


}
