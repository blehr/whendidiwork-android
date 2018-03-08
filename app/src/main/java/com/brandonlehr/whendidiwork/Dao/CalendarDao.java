package com.brandonlehr.whendidiwork.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.brandonlehr.whendidiwork.models.Calendar;

import java.util.List;

/**
 * Created by blehr on 3/6/2018.
 */

@Dao
public interface CalendarDao {

    @Query("SELECT * FROM Calendar")
    List<Calendar> getAllCalendars();

    @Query("SELECT * FROM Calendar WHERE id=:id")
    Calendar getCalendar(String id);

}
