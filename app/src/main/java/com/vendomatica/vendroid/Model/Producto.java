package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Producto implements Serializable {

    public final static String TABLENAME = "tb_producto";
    public final static String CUS = "cus";
    public final static String NUS = "nus";
    public final static String MAX_P = "maxProducto";

    public String cus;
    public String nus;
    public String maxProducto;

    public Producto()
    {
        this.cus = "";
        this.nus = "";
        this.maxProducto="";
    }
    public Producto(String cus, String nus, String maxProd)
    {
        this.cus = cus;
        this.nus = nus;
        this.maxProducto = maxProd;
    }
}
