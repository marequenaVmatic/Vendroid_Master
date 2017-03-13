package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Falla implements Serializable {



    public String CodFalla;
    public String Nombre;

    public Falla()
    {
        this.CodFalla = "";
        this.Nombre = "";
    }
    public Falla(String CodFalla, String Nombre)
    {
        this.CodFalla = CodFalla;
        this.Nombre = Nombre;
    }
}
