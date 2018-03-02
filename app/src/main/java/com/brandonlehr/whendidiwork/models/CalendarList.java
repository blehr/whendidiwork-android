package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by blehr on 2/12/2018.
 */

public class CalendarList {
    @SerializedName("calendars")
    @Expose
    private List<Calendar> calendars = null;
    @SerializedName("lastUsed")
    @Expose
    private String lastUsed;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;

    public List<Calendar> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<Calendar> calendars) {
        this.calendars = calendars;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public String getTimeZone() {
        return timeZone;
    }

}
