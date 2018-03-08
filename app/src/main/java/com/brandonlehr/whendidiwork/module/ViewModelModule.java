package com.brandonlehr.whendidiwork.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.brandonlehr.whendidiwork.viewModels.CreateCalendarViewModel;
import com.brandonlehr.whendidiwork.viewModels.CreateEventViewModel;
import com.brandonlehr.whendidiwork.viewModels.CreateSheetViewModel;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;
import com.brandonlehr.whendidiwork.viewModels.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by blehr on 3/7/2018.
 */

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindMainActivityViewModel(MainActivityViewModel mainActivityViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(CreateSheetViewModel.class)
    abstract ViewModel bindCreateSheetViewModel(CreateSheetViewModel createSheetViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateCalendarViewModel.class)
    abstract ViewModel bindCreateCalendarViewModel(CreateCalendarViewModel createCalendarViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateEventViewModel.class)
    abstract ViewModel bindCreateEventViewModel(CreateEventViewModel createEventViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

}
