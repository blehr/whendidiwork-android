package com.brandonlehr.whendidiwork.component;

import com.brandonlehr.whendidiwork.module.AppModule;
import com.brandonlehr.whendidiwork.module.GoogleClientModule;
import com.brandonlehr.whendidiwork.module.RetrofitClientModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by blehr on 3/7/2018.
 */

@Singleton
@Component(modules = {RetrofitClientModule.class, AppModule.class, GoogleClientModule.class})
public interface RetrofitClientComponent {
//    Retrofit getRetrofit();
//    void inject(MainActivity activity);
}
