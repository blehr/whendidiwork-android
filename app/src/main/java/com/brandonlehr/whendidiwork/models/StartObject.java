package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */

public class StartObject {
    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("date")
    @Expose
    private String date;

    public String getDateTime() {
       return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StartObject{" +
                "dateTime='" + dateTime + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
