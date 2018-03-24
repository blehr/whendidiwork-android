package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */

public class Google {
    @SerializedName("expiry_date")
    @Expose
    private Long expiryDate;
    @SerializedName("profileImg")
    @Expose
    private String profileImg;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "google_id")
    private String id;

    public Long getExpiryDate() {
        return expiryDate;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getToken() {
        return token;
    }
    public String getId() {
        return id;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(String id) {
        this.id = id;
    }
}
