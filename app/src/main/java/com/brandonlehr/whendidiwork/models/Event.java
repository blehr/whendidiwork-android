package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by blehr on 2/12/2018.
 */

public class Event {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("htmlLink")
    @Expose
    private String htmlLink;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("start")
    @Expose
    private StartObject start;
    @SerializedName("end")
    @Expose
    private EndObject end;
    @SerializedName("recurrence")
    @Expose
    private List<String> recurrence = null;
    @SerializedName("iCalUID")
    @Expose
    private String iCalUID;
    @SerializedName("sequence")
    @Expose
    private Integer sequence;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("colorId")
    @Expose
    private String colorId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHtmlLink() {
        return htmlLink;
    }

    public void setHtmlLink(String htmlLink) {
        this.htmlLink = htmlLink;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public StartObject getStart() {
        return start;
    }

    public void setStart(StartObject start) {
        this.start = start;
    }

    public EndObject getEnd() {
        return end;
    }

    public void setEnd(EndObject end) {
        this.end = end;
    }

    public List<String> getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(List<String> recurrence) {
        this.recurrence = recurrence;
    }

    public String getICalUID() {
        return iCalUID;
    }

    public void setICalUID(String iCalUID) {
        this.iCalUID = iCalUID;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "kind='" + kind + '\'' +
                ", etag='" + etag + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", htmlLink='" + htmlLink + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", summary='" + summary + '\'' +
                ", start=" + start.toString() +
                ", end=" + end.toString() +
                ", recurrence=" + recurrence +
                ", iCalUID='" + iCalUID + '\'' +
                ", sequence=" + sequence +
                ", location='" + location + '\'' +
                ", colorId='" + colorId + '\'' +
                '}';
    }
}
