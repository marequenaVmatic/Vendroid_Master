package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class TaskType {

    public final static String TABLENAME = "tb_tasktype";
    public final static String TYPE = "type";
    public final static String NAME = "name";

    public String type;
    public String name;

    public TaskType()
    {
        this.type = "";
        this.name="";
    }
    public TaskType(String type, String name)
    {
        this.type = type;
        this.name = name;
    }
}
