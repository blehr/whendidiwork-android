package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by blehr on 2/12/2018.
 */
@Entity
public class Calendar implements Serializable {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private String id = "";
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("colorId")
    @Expose
    private String colorId;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("foregroundColor")
    @Expose
    private String foregroundColor;
    @SerializedName("selected")
    @Expose
    private Boolean selected;
    @SerializedName("accessRole")
    @Expose
    private String accessRole;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("primary")
    @Expose
    private Boolean primary;
    @SerializedName("summaryOverride")
    @Expose
    private String summaryOverride;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getSummaryOverride() {
        return summaryOverride;
    }

    public void setSummaryOverride(String summaryOverride) {
        this.summaryOverride = summaryOverride;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "kind='" + kind + '\'' +
                ", etag='" + etag + '\'' +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", colorId='" + colorId + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", foregroundColor='" + foregroundColor + '\'' +
                ", selected=" + selected +
                ", accessRole='" + accessRole + '\'' +
                ", location='" + location + '\'' +
                ", primary=" + primary +
                ", summaryOverride='" + summaryOverride + '\'' +
                '}';
    }
}
