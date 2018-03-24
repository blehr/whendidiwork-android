package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CreateEventPostBody;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.UserTimer;
import com.brandonlehr.whendidiwork.repository.CalendarRepository;
import com.brandonlehr.whendidiwork.repository.EventRepository;
import com.brandonlehr.whendidiwork.repository.SheetRepository;
import com.brandonlehr.whendidiwork.repository.UserRepository;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import java.util.HashMap;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/5/2018.
 */

public class CreateEventViewModel extends ViewModel {
    private static final String TAG = "CreateEventViewModel";

    MutableLiveData<Event> createEventResponse = new MutableLiveData<>();
    MutableLiveData<Event> updateEventResponse = new MutableLiveData<>();
    MutableLiveData<Response> errorResponse = new MutableLiveData<>();
    LiveData<UserTimer> mUserTimer;
    private ApiCalls client;
    private LiveData<TimeZone> mTimeZone;
    private MutableLiveData<Boolean> isLoading;
    private LiveData<Sheet> mSelectedSheet;
    private LiveData<Calendar> mSelectedCalendar;
    CalendarRepository mCalendarRepository;
    SheetRepository mSheetRepository;
    EventRepository mEventRepository;
    UserRepository mUserRepository;


    @Inject
    public CreateEventViewModel(
            Retrofit retrofitClient,
            CalendarRepository calendarRepository,
            SheetRepository sheetRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        client = retrofitClient.create(ApiCalls.class);
        mCalendarRepository = calendarRepository;
        mSheetRepository = sheetRepository;
        mEventRepository = eventRepository;
        mUserRepository = userRepository;
        mTimeZone = mCalendarRepository.getTimeZone();
        isLoading = mCalendarRepository.getIsLoading();
        mSelectedCalendar = mCalendarRepository.getSelectedCalendar();
        mSelectedSheet = mSheetRepository.getSelectedSheet();
        mUserTimer = mUserRepository.getUserTimer();
    }

    public LiveData<TimeZone> getTimeZone() {
        return mTimeZone;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Sheet> getSelectedSheet() {
        return mSelectedSheet;
    }

    public LiveData<Calendar> getSelectedCalendar() {
        return mSelectedCalendar;
    }

    public LiveData<Event> getEventById(String id) {
        return mEventRepository.getEventById(id);
    }

    public MutableLiveData<Response> getErrorResponse() {
        return errorResponse;
    }

    public LiveData<UserTimer> getUserTimer() {
        return mUserTimer;
    }

    public void insertUserTimer(UserTimer userTimer) {
        mUserRepository.insertUserTimer(userTimer);
    }

    public void deleteUserTimer() {
        mUserRepository.deleteUserTimer();
    }

    public LiveData<Event> makeEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        createEvent(calendarId, sheetId, postBody);
        return createEventResponse;
    }

    public LiveData<Event> updateEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        doUpdateEvent(calendarId, sheetId, postBody);
        return updateEventResponse;
    }

    public void createEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        Call<Event> createEvent = client.createEvent(calendarId, sheetId, postBody);
        createEvent.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    createEventResponse.setValue(response.body());
                    mEventRepository.insertNewEvent(response.body());
                } else {
                    Log.d(TAG, "onResponse: create event NOT SUCCESSFULL " + response.errorBody());
                    errorResponse.setValue(response);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.d(TAG, "onFailure: create event failure " + t.getMessage());
            }
        });
    }

    public void doUpdateEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        Call<Event> updateEvent = client.updateEvent(calendarId, sheetId, postBody);
        updateEvent.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    updateEventResponse.setValue(response.body());
                    mEventRepository.insertNewEvent(response.body());
                } else {
                    Log.d(TAG, "onResponse: create event NOT SUCCESSFULL " + response.errorBody());
                    errorResponse.setValue(response);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.d(TAG, "onFailure: create event failure " + t.getMessage());
            }
        });
    }
}
