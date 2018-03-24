package com.brandonlehr.whendidiwork.Dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.SelectedCalendar;
import com.brandonlehr.whendidiwork.models.SelectedSheet;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.models.UserTimer;

/**
 * Created by blehr on 3/6/2018.
 */

@Database(entities = {Calendar.class,
        Sheet.class,
        SelectedCalendar.class,
        SelectedSheet.class,
        UserResponse.class,
        Event.class,
        TimeZone.class,
        UserTimer.class}, version = 12)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao getAppDao();

}
