package com.brandonlehr.whendidiwork.component;

import com.brandonlehr.whendidiwork.CreateCalendarActivity;
import com.brandonlehr.whendidiwork.CreateEventActivity;
import com.brandonlehr.whendidiwork.CreateSheetActivity;
import com.brandonlehr.whendidiwork.FirstScreenFragment;
import com.brandonlehr.whendidiwork.LoginActivity;
import com.brandonlehr.whendidiwork.MainActivity;
import com.brandonlehr.whendidiwork.SwipeController;
import com.brandonlehr.whendidiwork.module.AppModule;
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
@Component(modules = {GoogleClientModule.class, AppModule.class, RetrofitClientModule.class, ViewModelModule.class})
public interface GoogleClientComponent {
    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(FirstScreenFragment firstScreenFragment);
    void inject(CreateSheetActivity createSheetActivity);
    void inject(CreateCalendarActivity createCalendarActivity);
    void inject(CreateEventActivity createEventActivity);
    void inject(SwipeController swipeController);
    void inject(MainActivityViewModel mainActivityViewModel);
    void inject(CreateSheetViewModel createSheetViewModel);
    void inject(CreateCalendarViewModel createCalendarViewModel);
    void inject(CreateEventViewModel createEventViewModel);

//    interface Injectable {
//        void inject(GoogleClientComponent appComponent);
//    }
}
