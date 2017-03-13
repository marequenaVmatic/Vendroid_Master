package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Clasificacion implements Serializable {



    public String CodClasificacionFalla;
    public String ClasificacionFalla;

    public Clasificacion()
    {
        this.CodClasificacionFalla = "";
        this.ClasificacionFalla = "";
    }
    public Clasificacion(String CodClasificacionFalla, String ClasificacionFalla)
    {
        this.CodClasificacionFalla = CodClasificacionFalla;
        this.ClasificacionFalla = ClasificacionFalla;
    }
}
