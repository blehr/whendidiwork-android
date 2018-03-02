package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */

public class UserResponse {

    @SerializedName("_id")
    @Expose
    private String id;
//    @SerializedName("__v")
//    @Expose
//    private Integer v;
    @SerializedName("lastUsed")
    @Expose
    private LastUsed lastUsed;
    @SerializedName("google")
    @Expose
    private Google google;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public Integer getV() {
//        return v;
//    }
//
//    public void setV(Integer v) {
//        this.v = v;
//    }

    public LastUsed getLastUsed() {
        return lastUsed;
    }

//    public void setLastUsed(LastUsed lastUsed) {
//        this.lastUsed = lastUsed;
//    }

    public Google getGoogle() {
        return google;
    }

//    public void setGoogle(Google google) {
//        this.google = google;
//    }
}
