package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class CompleteDetailCounter {

    public final static String TABLENAME = "tb_complete_detail_counter";
    public final static String TASKID = "taskid";
    public final static String CODCOUNTER = "CodCounter";
    public final static String QUANTITY = "quantity";

    public String taskid;
    public String CodCounter;
    public String quantity;

    public CompleteDetailCounter()
    {
        this.taskid = "";
        this.CodCounter = "";
        this.quantity = "";
    }
    public CompleteDetailCounter(String taskid, String CodCounter, String quantity)
    {
        this.taskid = taskid;
        this.CodCounter = CodCounter;
        this.quantity = quantity;
    }
}
