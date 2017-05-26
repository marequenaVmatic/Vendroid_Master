package com.vendomatica.vendroid.controles;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by miguel_r on 25-05-2017.
 */

public class TextEditCustom implements TextWatcher {

        private EditText mEditText;
        public String idcampo;
        public String valor;
        public TextEditCustom(EditText editText,String campo) {
            mEditText = editText;
            idcampo=campo;

        }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    @Override
    public void afterTextChanged(Editable s) {
        valor = s.toString();

    }
}