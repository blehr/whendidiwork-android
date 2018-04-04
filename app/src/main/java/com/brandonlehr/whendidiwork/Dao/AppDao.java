package com.brandonlehr.whendidiwork.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.SelectedCalendar;
import com.brandonlehr.whendidiwork.models.SelectedSheet;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.SigninTime;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.models.UserTimer;

import java.util.List;

/**
 * Created by blehr on 3/6/2018.
 */

@Dao
public abstract class AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUser(UserResponse user);

    @Query("SELECT * FROM user")
    public abstract LiveData<UserResponse> getUser();

    @Query("Delete FROM user")
    public abstract void deleteUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCalendars(List<Calendar> calendars);

    @Query("DELETE FROM calendars")
    public abstract void deleteAllCalendars();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCalendar(Calendar calendar);

    @Query("SELECT * FROM calendars")
    public abstract LiveData<List<Calendar>> getAllCalendars();

    @Query("SELECT * FROM calendars WHERE id=:id")
    public abstract Calendar getCalendar(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSheets(List<Sheet> sheets);

    @Query("SELECT * FROM sheets")
    public abstract LiveData<List<Sheet>> getAllSheets();

    @Query("SELECT * FROM sheets WHERE id=:id")
    public abstract Sheet getSheet(String id);

    @Query("SELECT * FROM selected_sheet")
    public abstract LiveData<Sheet> getSelectedSheet();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSheet(Sheet sheet);

    @Query("DELETE FROM selected_sheet")
    public abstract void deleteSelectedSheet();

    @Query("DELETE FROM sheets")
    public abstract void deleteAllSheets();

    @Query("SELECT * FROM selected_calendar")
    public abstract LiveData<Calendar> getSelectedCalendar();

    @Query("DELETE FROM selected_calendar")
    public abstract void deleteSelectedCalendar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateSelectedSheet(SelectedSheet sheet);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateSelectedCalendar(SelectedCalendar calendar);

    @Query("DELETE FROM events")
    public abstract void deleteAllEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertEvents(List<Event> events);

    @Query("SELECT * FROM events")
    public abstract LiveData<List<Event>> getEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertNewEvent(Event event);

    @Query("SELECT * FROM events WHERE id=:id")
    public abstract LiveData<Event> getEventById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void setTimeZone(TimeZone timeZone);

    @Query("SELECT * FROM timeZone")
    public abstract LiveData<TimeZone> getTimeZone();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUserTimer(UserTimer userTimer);

    @Query("SELECT * FROM userTimer")
    public abstract LiveData<UserTimer> getUserTimer();

    @Query("DELETE FROM userTimer WHERE timerId=1")
    public abstract void deleteUserTimer();

    @Query("SELECT * FROM signinTime")
    public abstract LiveData<SigninTime> getSigninTime();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSigninTime(SigninTime timestamp);

    @Query("DELETE FROM signinTime")
    public abstract void deleteSigninTime();

    @Transaction
    public void deleteOldUserInsertNew(UserResponse user) {
        deleteUser();
        deleteSelectedCalendar();
        deleteSelectedSheet();
        deleteUserTimer();
        deleteAllCalendars();
        deleteAllSheets();
        deleteAllEvents();
        insertUser(user);
    }

}
