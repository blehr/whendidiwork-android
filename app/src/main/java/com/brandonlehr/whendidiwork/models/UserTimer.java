package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by blehr on 3/19/2018.
 */

@Entity(tableName = "userTimer")
public class UserTimer {
    @PrimaryKey
    private int timerId = 1;
    private Long startTimeStamp;
    private Long endTimeStamp;

    public Long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(Long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public Long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(Long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public int getTimerId() {
        return timerId;
    }

    public void setTimerId(int id) {
        this.timerId = 1;
    }

    @Override
    public String toString() {
        return "UserTimer{" +
                "timerId=" + timerId +
                ", startTimeStamp=" + startTimeStamp +
                ", endTimeStamp=" + endTimeStamp +
                '}';
    }
}
