package com.brandonlehr.whendidiwork;

import android.app.Application;

import com.brandonlehr.whendidiwork.component.DaggerGoogleClientComponent;
import com.brandonlehr.whendidiwork.component.GoogleClientComponent;
import com.brandonlehr.whendidiwork.module.AppModule;
import com.brandonlehr.whendidiwork.module.GoogleClientModule;
import com.brandonlehr.whendidiwork.module.RetrofitClientModule;

//import com.brandonlehr.whendidiwork.component.DaggerRetrofitClientComponent;

/**
 * Created by blehr on 3/7/2018.
 */

public class Whendidiwork extends Application {
//    private RetrofitClientComponent mRetrofitClientComponent;
    private GoogleClientComponent mGoogleClientComponent;

    @Override
    public void onCreate() {
        super.onCreate();

//        mRetrofitClientComponent = DaggerRetrofitClientComponent.builder()
//            .appModule(new AppModule(this))
//            .retrofitClientModule(new RetrofitClientModule())
//            .build();

        mGoogleClientComponent = DaggerGoogleClientComponent.builder()
                .appModule(new AppModule(this))
                .googleClientModule(new GoogleClientModule())
                .retrofitClientModule(new RetrofitClientModule())
                .build();

    }




    public GoogleClientComponent getGoogleClientComponent() {
        return mGoogleClientComponent;
    }

//    public RetrofitClientComponent getRetrofitClientComponent() {
//        return mRetrofitClientComponent;
//    }

}
