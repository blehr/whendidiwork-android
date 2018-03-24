package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by blehr on 3/14/2018.
 */

@Entity(tableName = "timeZone")
public class TimeZone {

    @PrimaryKey
    private int timeZoneKey = 1;

    String timeZone;

    public TimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getTimeZoneKey() {
        return timeZoneKey;
    }

    public void setTimeZoneKey(int timeZoneKey) {
        this.timeZoneKey = 1;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
