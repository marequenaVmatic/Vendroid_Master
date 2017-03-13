package com.vendomatica.vendroid.Dialog;
/*
This screen appears when the user press the images taken at the form.
 */
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.vendomatica.vendroid.R;

public class PhotoDialog extends Dialog {
	private Context pContext;
	ImageView imgPhoto;

	public PhotoDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);

		// TODO Auto-generated constructor stub
		pContext = context;
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.photo_dialog);
		// setTitle("Input Post Information");
		
		imgPhoto = (ImageView)findViewById(R.id.imgPhoto);

		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
			}

		});
	}
	public void setImage(String path){
		imgPhoto.setImageBitmap(BitmapFactory
				.decodeFile(path));
		imgPhoto.setVisibility(View.VISIBLE);
	}
}
