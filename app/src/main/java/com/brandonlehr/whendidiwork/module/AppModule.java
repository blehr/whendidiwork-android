package com.brandonlehr.whendidiwork.module;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by blehr on 3/7/2018.
 */

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application){
        mApplication = application;
    }

    @Named("application_context")
    @Singleton
    @Provides
    public Application providesApplication(){ return mApplication; }
}
