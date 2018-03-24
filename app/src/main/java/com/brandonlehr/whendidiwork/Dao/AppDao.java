package com.brandonlehr.whendidiwork.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.SelectedCalendar;
import com.brandonlehr.whendidiwork.models.SelectedSheet;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.models.UserTimer;

import java.util.List;

/**
 * Created by blehr on 3/6/2018.
 */

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserResponse user);

    @Query("SELECT * FROM user")
    LiveData<UserResponse> getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalendars(List<Calendar> calendars);

    @Query("DELETE FROM calendars")
    void deleteAllCalendars();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalendar(Calendar calendar);

    @Query("SELECT * FROM calendars")
    LiveData<List<Calendar>> getAllCalendars();

    @Query("SELECT * FROM calendars WHERE id=:id")
    Calendar getCalendar(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSheets(List<Sheet> sheets);

    @Query("SELECT * FROM sheets")
    LiveData<List<Sheet>> getAllSheets();

    @Query("SELECT * FROM sheets WHERE id=:id")
    Sheet getSheet(String id);

    @Query("SELECT * FROM selected_sheet")
    LiveData<Sheet> getSelectedSheet();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSheet(Sheet sheet);

    @Query("DELETE FROM sheets")
    void deleteAllSheets();

    @Query("SELECT * FROM selected_calendar")
    LiveData<Calendar> getSelectedCalendar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateSelectedSheet(SelectedSheet sheet);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateSelectedCalendar(SelectedCalendar calendar);

    @Query("DELETE FROM events")
    void deleteAllEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<Event> events);

    @Query("SELECT * FROM events")
    LiveData<List<Event>> getEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewEvent(Event event);

    @Query("SELECT * FROM events WHERE id=:id")
    LiveData<Event> getEventById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setTimeZone(TimeZone timeZone);

    @Query("SELECT * FROM timeZone")
    LiveData<TimeZone> getTimeZone();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserTimer(UserTimer userTimer);

    @Query("SELECT * FROM userTimer")
    LiveData<UserTimer> getUserTimer();

    @Query("DELETE FROM userTimer WHERE timerId=1")
    void deleteUserTimer();

}
