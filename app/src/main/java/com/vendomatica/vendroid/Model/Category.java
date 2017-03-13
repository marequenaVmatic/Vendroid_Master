package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class Category {
/////////////////////////
    public final static String TABLENAME = "tb_category";
    public final static String CATEGORY = "category";

    public String category;

    public Category()
    {
        this.category = "";
    }
    public Category(String category)
    {
        this.category = category;
    }
}
