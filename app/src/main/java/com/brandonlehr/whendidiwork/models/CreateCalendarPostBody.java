package com.brandonlehr.whendidiwork.models;

/**
 * Created by blehr on 3/2/2018.
 */

public class CreateCalendarPostBody {
    String calendar;
    String timeZone;

    public CreateCalendarPostBody(String name, String timeZone) {
        this.calendar = name;
        this.timeZone = timeZone;
    }
}
