package com.brandonlehr.whendidiwork.module;

import android.app.Application;
import android.graphics.Typeface;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by blehr on 3/8/2018.
 */

@Module(includes = {AppModule.class})
public class FontAwesomeModule {

    public Typeface faType;

    public FontAwesomeModule(@Named("application_context")Application app) {
        faType = Typeface.createFromAsset(app.getAssets(), "fonts/fontawesome.ttf");
    }

    @Provides
    @Singleton
    public Typeface providesFaType() {
        return faType;
    }

}
