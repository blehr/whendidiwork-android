package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.repository.CalendarRepository;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * Created by blehr on 3/2/2018.
 */

public class CreateCalendarViewModel  extends ViewModel {
    private static final String TAG = "CreateCalendarViewModel";

    private LiveData<TimeZone> mTimeZone;
    private MutableLiveData<Boolean> isLoading;


    private ApiCalls client;
    CalendarRepository mCalendarRepository;

    @Inject
    public CreateCalendarViewModel(Retrofit retrofitClient, CalendarRepository calendarRepository) {
        client = retrofitClient.create(ApiCalls.class);
        mCalendarRepository = calendarRepository;
        mTimeZone = mCalendarRepository.getTimeZone();
        isLoading = mCalendarRepository.getIsLoading();
    }

    public LiveData<TimeZone> getTimeZone() {
        return mTimeZone;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void createCalendar(CreateCalendarPostBody createCalendarPostBody) {
        mCalendarRepository.createCalendar(createCalendarPostBody);
    }
}
