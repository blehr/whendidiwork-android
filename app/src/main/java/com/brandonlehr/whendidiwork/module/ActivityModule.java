package com.brandonlehr.whendidiwork.module;

import android.app.Activity;
import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by blehr on 3/7/2018.
 */

@Module
public class ActivityModule {
    private final Context context;

    ActivityModule(Activity context) {
        this.context = context;
    }

    @Named("activity_context")
    @Provides
    public Context context() {
        return context;
    }
}
