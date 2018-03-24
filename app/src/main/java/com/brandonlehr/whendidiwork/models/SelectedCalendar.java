package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by blehr on 3/9/2018.
 */
@Entity(tableName = "selected_calendar")
public class SelectedCalendar {

    @Embedded
    private Calendar mCalendar;

    @PrimaryKey
    private int selectedCalendarKey = 1;


    public SelectedCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public int getSelectedCalendarKey() {
        return selectedCalendarKey;
    }

    public void setSelectedCalendarKey(int selectedCalendarKey) {
        this.selectedCalendarKey = 1;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }
}
