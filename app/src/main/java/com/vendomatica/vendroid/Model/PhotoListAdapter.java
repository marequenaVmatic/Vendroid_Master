package com.vendomatica.vendroid.Model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.vendomatica.vendroid.Dialog.PhotoDialog;
import com.vendomatica.vendroid.R;

import java.util.List;

public class PhotoListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public PhotoListAdapter(Context context, List<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        ViewGroup viewGroup = null;
        final View view;
        final ViewHolder holder;
        if (convertView == null) {
            viewGroup = (ViewGroup) LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.photo_item, null);
            holder = new ViewHolder();

            holder.imgPhoto = (ImageView) viewGroup.findViewById(R.id.imgPhoto);

            view = viewGroup;
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }

        final String postItem = values.get(position);
        holder.imgPhoto.setTag(position);
        holder.imgPhoto.setImageBitmap(BitmapFactory
                .decodeFile(postItem));
        holder.imgPhoto.setVisibility(View.VISIBLE);
        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDialog dlg = new PhotoDialog(context);
                dlg.setImage(postItem);
                dlg.show();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView imgPhoto;
    }
} 
