package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Producto_RutaAbastecimento {

    public final static String TABLENAME = "tb_producto_ruta";
    public final static String TASKTYPE = "tasktype";
    public final static String TASKBUSINESSKEY = "taskbusinesskey";
    public final static String RUTAABASTECIMENTO = "RutaAbastecimento";
    public final static String CUS = "cus";

    public String TaskType;
    public String TaskBusinessKey;
    public String RutaAbastecimento;
    public String cus;

    public Producto_RutaAbastecimento()
    {
        this.TaskType = "";
        this.TaskBusinessKey = "";
        this.RutaAbastecimento = "";
        this.cus = "";
    }
    public Producto_RutaAbastecimento(String taskType, String taskBusinessKey, String RutaAbastecimento, String cus)
    {
        this.TaskType = taskType;
        this.TaskBusinessKey = taskBusinessKey;
        this.RutaAbastecimento = RutaAbastecimento;
        this.cus = cus;
    }
}
