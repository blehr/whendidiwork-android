package com.brandonlehr.whendidiwork.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

/**
 * Created by blehr on 3/6/2018.
 */

@Dao
public interface UserAppValues {
//    @Query("SELECT * FROM UserAppValues")
//    UserAppValues getUserAppValues();

    @Query("SELECT selectedCalendar FROM UserAppValues")
    String getSelectedCalendar();

    @Query("SELECT selectedSheet FROM UserAppValues")
    String getSelectedSheet();

    @Query("SELECT eventToEdit FROM UserAppValues")
    String getEventToEdit();


}
