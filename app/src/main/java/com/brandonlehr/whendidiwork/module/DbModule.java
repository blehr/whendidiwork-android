package com.brandonlehr.whendidiwork.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.brandonlehr.whendidiwork.Dao.AppDao;
import com.brandonlehr.whendidiwork.Dao.AppDatabase;
import com.brandonlehr.whendidiwork.repository.CalendarRepository;
import com.brandonlehr.whendidiwork.repository.EventRepository;
import com.brandonlehr.whendidiwork.repository.SheetRepository;
import com.brandonlehr.whendidiwork.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/8/2018.
 */

@Module(includes = {AppModule.class, RetrofitClientModule.class})
public class DbModule {

    public AppDatabase appDb;


    @Inject
    public DbModule(@Named("application_context") Application app) {
        appDb = Room.databaseBuilder(app, AppDatabase.class, "test_db").fallbackToDestructiveMigration().build();
    }

    @Singleton
    @Provides
    AppDatabase providesAppDatabase() {
        return appDb;
    }

    @Singleton
    @Provides
    AppDao providesAppDao(AppDatabase appDb) {
        return appDb.getAppDao();
    }

    @Singleton
    @Provides
    CalendarRepository providesCalendarRepository(Retrofit retrofitClient, AppDao appDao) {
        return new CalendarRepository(retrofitClient, appDao);
    }

    @Singleton
    @Provides
    SheetRepository providesSheetRepository(Retrofit retrofitClient, AppDao appDao) {
        return new SheetRepository(retrofitClient, appDao);
    }

    @Singleton
    @Provides
    EventRepository providesEventRepository(Retrofit retrofitClient, AppDao appDao) {
        return new EventRepository(retrofitClient, appDao);
    }

    @Singleton
    @Provides
    UserRepository providesUserRepository(Retrofit retrofitClient, AppDao appDao) {
        return new UserRepository(retrofitClient, appDao);
    }


}
