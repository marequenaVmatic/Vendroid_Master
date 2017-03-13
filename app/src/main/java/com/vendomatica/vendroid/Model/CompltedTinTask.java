package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class CompltedTinTask {

    public final static String TABLENAME = "tb_completetintask";
    public final static String USERID = "userid";
    public final static String TASKID = "taskid";
    public final static String TASKTYPE = "tasktype";
    public final static String RUTAABASTEIMIENTO = "RutaAbastecimiento";
    public final static String CUS = "cus";
    public final static String NUS = "nus";
    public final static String QUANTITY = "quantity";

    public String userid;
    public int taskid;
    public String tasktype;
    public String RutaAbastecimiento;
    public String cus;
    public String nus;
    public String quantity;

    public CompltedTinTask()
    {
        this.userid = "";
        this.taskid = 0;
        this.tasktype = "";
        this.RutaAbastecimiento = "";
        this.cus = "";
        this.nus = "";
        this.quantity = "";
    }
    public CompltedTinTask(String userid, int taskid, String tasktype, String RutaAbastecimiento, String cus, String nus, String quantity)
    {
        this.userid = userid;
        this.taskid = taskid;
        this.tasktype = tasktype;
        this.RutaAbastecimiento = RutaAbastecimiento;
        this.cus = cus;
        this.nus = nus;
        this.quantity = quantity;
    }
}
