package com.vendomatica.vendroid.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vendomatica.vendroid.Model.Tabla;
import com.vendomatica.vendroid.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterDll extends BaseAdapter {
    Context context;
    int flags[];
    ArrayList<Tabla> Tabla;
    LayoutInflater inflter;

    public CustomAdapterDll(Context applicationContext, ArrayList<Tabla> tablas) {
        this.context = applicationContext;
        this.Tabla = tablas;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Tabla.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_ddl, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(Tabla.get(i).ValorCampo);
        return view;
    }
}