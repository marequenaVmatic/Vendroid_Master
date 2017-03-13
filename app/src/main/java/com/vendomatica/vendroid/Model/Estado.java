package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Estado implements Serializable {



    public int id_estado;
    public String Estado;

    public Estado()
    {
        this.id_estado = 0;
        this.Estado = "";
    }
    public Estado(int id_estado, String Estado)
    {
        this.id_estado = id_estado;
        this.Estado = Estado;
    }
}
