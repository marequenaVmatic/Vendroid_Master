package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class LogEvent {

    public final static String TABLENAME = "log_event";
    public final static String USERID = "userid";
    public final static String TASKID = "taskid";
    public final static String DATETIME = "datetime";
    public final static String DESCRIPTION = "description";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String BATTERY_LEVEL = "phn_battery_level";
    public final static String PHONE_FREESPACE = "phn_freespace";
    public final static String IS_CHARGING_USB = "phn_is_oncharge_usb";
    public final static String IS_CHARGING_OTHER = "phn_is_oncharge_other";

    public String userid;
    public String taskid;
    public String datetime;
    public String description;
    public String latitude;
    public String longitude;
    public String batteryLevel;
    public String freespace;
    public int isChargingUSB;
    public int isChargingOther;

    public LogEvent()
    {
        this.userid = "";
        this.taskid = "";
        this.datetime = "";
        this.description = "";
        this.latitude = "";
        this.longitude = "";
        this.batteryLevel = "";
        this.freespace = "";
        this.isChargingUSB = 0;
        this.isChargingOther = 0;
    }
    public LogEvent(String userid, String taskid, String datetime, String description, String latitude, String longitude, String strBatteryLevel, String strFreeSpace, int iIsChargingUSB, int iIsChargingOther)
    {
        this.userid = userid;
        this.taskid = taskid;
        this.datetime = datetime;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.batteryLevel = strBatteryLevel;
        this.freespace = strFreeSpace;
        this.isChargingUSB = iIsChargingUSB;
        this.isChargingOther = iIsChargingOther;
    }
}
