package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class MachineCounter {

    public final static String TABLENAME = "tb_machine_counter";
    public final static String TASKBUSINESSKEY = "TaskBusinessKey";
    public final static String CODCONTADOR = "CodContador";
    public final static String STARTVALUE = "StartValue";
    public final static String ENDVALUE = "EndValue";
    public final static String STARTDATE = "StartDate";
    public final static String ENDDATE = "EndDate";

    public String TaskBusinessKey;
    public String CodContador;
    public String StartValue;
    public String EndValue;
    public String StartDate;
    public String EndDate;

    public MachineCounter()
    {
        this.TaskBusinessKey = "";
        this.CodContador = "";
        this.StartValue = "";
        this.EndValue = "";
        this.StartDate = "";
        this.EndDate = "";
    }
    public MachineCounter(String taskBusinessKey, String CodContador, String StartValue, String EndValue, String StartDate, String EndDate)
    {
        this.TaskBusinessKey = taskBusinessKey;
        this.CodContador = CodContador;
        this.StartValue = StartValue;
        this.EndValue = EndValue;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
    }
}
