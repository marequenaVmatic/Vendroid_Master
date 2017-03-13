package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class CommentError {

    public final static String TABLENAME = "tb_error";
    public final static String ID = "id";
    public final static String ERROR = "error";

    public String id;
    public String error;

    public CommentError()
    {
        this.id = "";
        this.error = "";
    }
    public CommentError(String id, String error)
    {
        this.id = id;
        this.error = error;
    }
}
