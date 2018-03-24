package com.brandonlehr.whendidiwork.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.brandonlehr.whendidiwork.Dao.AppDao;
import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CalendarList;
import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.models.SelectedCalendar;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/8/2018.
 */

public class CalendarRepository {
    private static final String TAG = "CalendarRepository";

    private LiveData<List<Calendar>> mCalendars;
    private LiveData<Calendar> mSelectedCalendar;
    private LiveData<TimeZone> mTimeZone;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
//    private MutableLiveData<Calendar> createdCalendar = new MutableLiveData<>();

    private ApiCalls client;
    private final AppDao mAppDao;


    public CalendarRepository(Retrofit retrofitClient, AppDao appDao) {
        client = retrofitClient.create(ApiCalls.class);
        mAppDao = appDao;
        mCalendars = mAppDao.getAllCalendars();
        mSelectedCalendar = mAppDao.getSelectedCalendar();
        mTimeZone = mAppDao.getTimeZone();
        isLoading.setValue(false);
        fetchCalendarList();
    }

    public LiveData<List<Calendar>> getCalendars() {
        return mCalendars;
    }

    public LiveData<Calendar> getSelectedCalendar() {
        return mSelectedCalendar;
    }

    public LiveData<TimeZone> getTimeZone() {
        return mTimeZone;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void updateSelectedCalendar(Calendar calendar) {
        Log.d(TAG, "updateSelectedCalendar: calendar " + calendar);
        new InsertCalendarTask(mAppDao).execute(calendar);
    }

    public void createCalendar(CreateCalendarPostBody createCalendarPostBody) {
        isLoading.setValue(true);
        Call<Calendar> createCalendar = client.createCalendar(createCalendarPostBody);
        createCalendar.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                   new SetSelectedCalendarAndAddToList(mAppDao).execute(response.body());
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                isLoading.setValue(false);
            }
        });

    }

    private static class SetSelectedCalendarAndAddToList extends AsyncTask<Calendar, Void, Void> {
        private AppDao asyncAppDao;

        SetSelectedCalendarAndAddToList(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Calendar... calendars) {
            asyncAppDao.updateSelectedCalendar(new SelectedCalendar(calendars[0]));
            asyncAppDao.insertCalendar(calendars[0]);
            return null;
        }
    }

    private static class InsertCalendarTask extends AsyncTask<Calendar, Void, Void> {
        private AppDao asyncAppDao;

        InsertCalendarTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Calendar... calendar) {
            asyncAppDao.deleteAllEvents();
            asyncAppDao.updateSelectedCalendar(new SelectedCalendar(calendar[0]));
            return null;
        }

    }


    public void fetchCalendarList() {
        Call<CalendarList> getCalendars = client.getCalendarList();
        getCalendars.enqueue(new Callback<CalendarList>() {
            @Override
            public void onResponse(Call<CalendarList> call, Response<CalendarList> response) {
                if (response.isSuccessful()) {
                    new insertAndGetCalendars(mAppDao).execute(response.body().getCalendars());
                }
            }

            @Override
            public void onFailure(Call<CalendarList> call, Throwable t) {
                Log.d(TAG, "onFailure: fail to get calendarList: " + t.getMessage());
            }
        });
    }

    private static class insertAndGetCalendars extends AsyncTask<List<Calendar>, Void, Void> {
        AppDao mAppDao;
        insertAndGetCalendars(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Void doInBackground(List<Calendar>[] lists) {
            for (Calendar calendar : lists[0]) {
                if (calendar != null) {
                    if (calendar.getPrimary()) {
                        mAppDao.setTimeZone(new TimeZone(calendar.getTimeZone()));
                    }
                }

            }
            mAppDao.deleteAllCalendars();
            mAppDao.insertCalendars(lists[0]);
            return null;
        }
    }

}
