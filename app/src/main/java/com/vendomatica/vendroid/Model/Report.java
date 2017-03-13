package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Report {
    public String nus;
    public String quantity;

    public Report()
    {
        this.quantity = "";
        this.nus = "";
    }
    public Report(String nus, String quantity)
    {
        this.quantity = quantity;
        this.nus = nus;
    }
}
