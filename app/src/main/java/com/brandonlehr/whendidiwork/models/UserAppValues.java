package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by blehr on 3/6/2018.
 */

@Entity
public class UserAppValues {
    @PrimaryKey
    private String id;
    private String selectedCalendar;
    private String selectedSheet;
    private String eventToEdit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelectedCalendar() {
        return selectedCalendar;
    }

    public void setSelectedCalendar(String selectedCalendar) {
        this.selectedCalendar = selectedCalendar;
    }

    public String getSelectedSheet() {
        return selectedSheet;
    }

    public void setSelectedSheet(String selectedSheet) {
        this.selectedSheet = selectedSheet;
    }

    public String getEventToEdit() {
        return eventToEdit;
    }

    public void setEventToEdit(String eventToEdit) {
        this.eventToEdit = eventToEdit;
    }
}
