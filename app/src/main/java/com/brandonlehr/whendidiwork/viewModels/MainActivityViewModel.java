package com.brandonlehr.whendidiwork.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CalendarList;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.FileList;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.services.AuthWithServer;
import com.brandonlehr.whendidiwork.services.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by blehr on 2/17/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainActivityViewModel";

    private MutableLiveData<ArrayList<Calendar>> mCalendars = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Sheet>> mSheets;
    private MutableLiveData<ArrayList<Event>> mEvents;
    private MutableLiveData<Calendar> mCalendar = new MutableLiveData<>();
    private MutableLiveData<Sheet> mSheet = new MutableLiveData<>();
    private AuthWithServer client = RetrofitClient.getClient(this.getApplication()).create(AuthWithServer.class);

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<Calendar>> getCalendarList() {
        if (mCalendars.getValue() == null) {
            Log.d(TAG, "getCalendarList: mCalendars == null");
            fetchCalendarList();
        }
        Log.d(TAG, "getCalendarList: mCalendars: " + mCalendars.getValue());
        return mCalendars;


    }

    public void fetchCalendarList() {
        Call<CalendarList> getCalendars = client.getCalendarList();
        getCalendars.enqueue(new Callback<CalendarList>() {
            @Override
            public void onResponse(Call<CalendarList> call, Response<CalendarList> response) {
                Log.d(TAG, "onResponse: from get calendar list: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    mCalendars.setValue((ArrayList<Calendar>) response.body().getCalendars());
                }
            }

            @Override
            public void onFailure(Call<CalendarList> call, Throwable t) {
                Log.d(TAG, "onFailure: fail to get calendarList: " + t.getMessage());
            }
        });
    }

    public LiveData<ArrayList<Sheet>> getFiles() {
        if (mSheets == null) {
            mSheets = new MutableLiveData<>();
            fetchFiles();
        }
        return mSheets;
    }

    public void fetchFiles() {
        Call<FileList> getFiles = client.getFiles();
        getFiles.enqueue(new Callback<FileList>() {
            @Override
            public void onResponse(Call<FileList> call, Response<FileList> response) {
                Log.d(TAG, "onResponse: FileList " + response.isSuccessful());
                if (response.isSuccessful()) {
                    mSheets.setValue((ArrayList<Sheet>) response.body().getSheets());
                }

            }

            @Override
            public void onFailure(Call<FileList> call, Throwable t) {
                Log.d(TAG, "onFailure: Fail to get FileList: " + t.getMessage());
            }
        });
    }

    public LiveData<ArrayList<Event>> getCalendarEvents(String calendarId) {
        if (mEvents == null) {
            mEvents = new MutableLiveData<>();
            fetchCalendarEvents(calendarId);
            return mEvents;
        }
        fetchCalendarEvents(calendarId); // so it is always called
        return mEvents;
    }

    public void fetchCalendarEvents(String calendarId) {
        Call<List<Event>> getEvents = client.getEvents(calendarId);
        getEvents.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    mEvents.setValue((ArrayList<Event>) response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<Calendar> setCalendar(Calendar calendar) {
        mCalendar.setValue(calendar);
        Log.d(TAG, "setCalendar: mCalendar: " + mCalendar.toString() + "," + mCalendar.getValue().toString());
        return mCalendar;
    }

    public MutableLiveData<Sheet> setSheet(Sheet sheet) {
        mSheet.setValue(sheet);
        return mSheet;
    }


    public LiveData<Calendar> getCalendar() {
        return mCalendar;
    }

    public LiveData<Sheet> getSheet() {
        return mSheet;
    }

//    public LiveData<ArrayList<Event>> getEvents() {
//        return mEvents;
//    }
//
//    public MutableLiveData<ArrayList<Calendar>> getCalendars() {
//        return mCalendars;
//    }
//
//    public MutableLiveData<ArrayList<Sheet>> getSheets() {
//        return mSheets;
//    }

}