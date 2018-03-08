package com.brandonlehr.whendidiwork.models;

/**
 * Created by blehr on 3/5/2018.
 */

public class CreateEventPostBody {
    public String endDate;
    public String startDate;
    public String endTime;
    public String startTime;
    public String timeZone;
    public String note;

    public CreateEventPostBody(String endDate, String startDate, String endTime, String startTime, String timeZone, String note) {
        this.endDate = endDate;
        this.startDate = startDate;
        this.endTime = endTime;
        this.startTime = startTime;
        this.timeZone = timeZone;
        this.note = note;
    }

    @Override
    public String toString() {
        return "CreateEventPostBody{" +
                "endDate='" + endDate + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
