package com.brandonlehr.whendidiwork.component;

import com.brandonlehr.whendidiwork.CalendarListFragment;
import com.brandonlehr.whendidiwork.CreateCalendarActivity;
import com.brandonlehr.whendidiwork.CreateEventActivity;
import com.brandonlehr.whendidiwork.CreateSheetActivity;
import com.brandonlehr.whendidiwork.EventListFragment;
import com.brandonlehr.whendidiwork.LoginActivity;
import com.brandonlehr.whendidiwork.MainActivity;
import com.brandonlehr.whendidiwork.MyTimerService;
import com.brandonlehr.whendidiwork.SheetListFragment;
import com.brandonlehr.whendidiwork.TimerActivity;
import com.brandonlehr.whendidiwork.module.AppModule;
import com.brandonlehr.whendidiwork.module.DbModule;
import com.brandonlehr.whendidiwork.module.FontAwesomeModule;
import com.brandonlehr.whendidiwork.module.GoogleClientModule;
import com.brandonlehr.whendidiwork.module.RetrofitClientModule;
import com.brandonlehr.whendidiwork.module.ViewModelModule;
import com.brandonlehr.whendidiwork.viewModels.CreateCalendarViewModel;
import com.brandonlehr.whendidiwork.viewModels.CreateEventViewModel;
import com.brandonlehr.whendidiwork.viewModels.CreateSheetViewModel;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by blehr on 3/7/2018.
 */
@Singleton
@Component(modules = {GoogleClientModule.class, AppModule.class, RetrofitClientModule.class, ViewModelModule.class, FontAwesomeModule.class, DbModule.class})
public interface DIComponent {
    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(CreateSheetActivity createSheetActivity);
    void inject(CreateCalendarActivity createCalendarActivity);
    void inject(CreateEventActivity createEventActivity);
    void inject(MainActivityViewModel mainActivityViewModel);
    void inject(CreateSheetViewModel createSheetViewModel);
    void inject(CreateCalendarViewModel createCalendarViewModel);
    void inject(CreateEventViewModel createEventViewModel);
    void inject(CalendarListFragment calendarListFragment);
    void inject(SheetListFragment sheetListFragment);
    void inject(EventListFragment eventListFragment);
    void inject(TimerActivity timerActivity);
    void inject(MyTimerService myTimerService);

}
