package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */

public class Google {
    @SerializedName("expiry_date")
    @Expose
    private Integer expiryDate;
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
    private String id;

    public Integer getExpiryDate() {
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
}
