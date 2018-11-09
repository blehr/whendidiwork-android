package com.brandonlehr.whendidiwork;

import android.app.Application;

import com.brandonlehr.whendidiwork.component.DIComponent;
import com.brandonlehr.whendidiwork.component.DaggerDIComponent;
import com.brandonlehr.whendidiwork.module.ActivityModule;
import com.brandonlehr.whendidiwork.module.AppModule;
import com.brandonlehr.whendidiwork.module.DbModule;
import com.brandonlehr.whendidiwork.module.GoogleClientModule;
import com.brandonlehr.whendidiwork.module.RetrofitClientModule;

/**
 * Created by blehr on 3/7/2018.
 */

public class Whendidiwork extends Application {
    private DIComponent mDIComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mDIComponent = DaggerDIComponent.builder()
                .appModule(new AppModule(this))
                .googleClientModule(new GoogleClientModule())
                .retrofitClientModule(new RetrofitClientModule())
                .dbModule(new DbModule(this))
                .build();

    }


    public DIComponent getDIComponent() {
        return mDIComponent;
    }

}
