package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */
@Entity(tableName = "user")
public class UserResponse {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("lastUsed")
    @Expose
    @Embedded
    private LastUsed lastUsed;
    @SerializedName("google")
    @Expose
    @Embedded
    private Google google;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LastUsed getLastUsed() {
        return lastUsed;
    }

    public Google getGoogle() {
        return google;
    }

    public void setLastUsed(LastUsed lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setGoogle(Google google) {
        this.google = google;
    }
}
