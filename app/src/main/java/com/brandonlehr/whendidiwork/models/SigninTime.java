package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "signinTime")
public class SigninTime {
    @PrimaryKey
    int id = 1;
    Long timestamp;

    public SigninTime(Long timestamp) {
        this.timestamp = timestamp;
        this.id = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = 1;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SigninTime{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }
}
