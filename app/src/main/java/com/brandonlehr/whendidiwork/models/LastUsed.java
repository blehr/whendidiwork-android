package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by blehr on 2/12/2018.
 */

public class LastUsed {
    @SerializedName("sheet")
    @Expose
    private String sheet;
    @SerializedName("calendar")
    @Expose
    private String calendar;

    public String getSheet() {
        return sheet;
    }

    public String getCalendar() {
        return calendar;
    }
}
