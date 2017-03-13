package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class User {

    public final static String TABLENAME = "tb_user";
    public final static String USERID = "userid";
    public final static String PASSWORD = "password";
    public final static String FIRSTNAME = "first_name";
    public final static String LASTNAME = "last_name";

    public String userid;
    public String password;
    public String firstName;
    public String lastName;

    public User()
    {
        this.userid = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
    }
    public User(String userid, String password, String firstName, String lastName)
    {
        this.userid = userid;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
