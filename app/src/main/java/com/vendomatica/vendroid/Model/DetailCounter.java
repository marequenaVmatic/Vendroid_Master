package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class DetailCounter {

    public final static String TABLENAME = "tb_detail_counter";
    public final static String TASKID = "taskid";
    public final static String CODCOUNTER = "CodCounter";
    public final static String QUANTITY = "quantity";

    public String taskid;
    public String CodCounter;
    public String quantity;

    public DetailCounter()
    {
        this.taskid = "";
        this.CodCounter = "";
        this.quantity = "";
    }
    public DetailCounter(String taskid, String CodCounter, String quantity)
    {
        this.taskid = taskid;
        this.CodCounter = CodCounter;
        this.quantity = quantity;
    }
}
