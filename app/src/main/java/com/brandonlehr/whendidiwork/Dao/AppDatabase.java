package com.brandonlehr.whendidiwork.Dao;

/**
 * Created by blehr on 3/6/2018.
 */

//@Database(entities = {UserAppValues.class, Calendar.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
//
//    private static AppDatabase INSTANCE;
//
//    public abstract UserAppValues userAppValuesDao();
//    public abstract CalendarDao mCalendarDao();
//
//    public static AppDatabase getAppDatabase(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE =
//                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
//                            // allow queries on the main thread.
//                            // Don't do this on a real app! See PersistenceBasicSample for an example.
//                            .allowMainThreadQueries()
//                            .build();
//        }
//        return INSTANCE;
//    }
//
//    public static void destroyInstance() {
//        INSTANCE = null;
//    }
//}
