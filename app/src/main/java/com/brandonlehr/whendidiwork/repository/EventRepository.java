package com.brandonlehr.whendidiwork.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.brandonlehr.whendidiwork.Dao.AppDao;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.SheetDeleteResponse;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/9/2018.
 */

public class EventRepository {
    private static final String TAG = "EventRepository";

    private ApiCalls client;
    private AppDao mAppDao;
    public LiveData<List<Event>> mEvents;


    public EventRepository(Retrofit retrofitClient, AppDao appDao) {
        client = retrofitClient.create(ApiCalls.class);
        mAppDao = appDao;
        mEvents = mAppDao.getEvents();
    }

    public LiveData<List<Event>> subscribeToEvents() {
        return mEvents;
    }

    public void insertNewEvent(Event event) {
        new InsertNewEvent(mAppDao).execute(event);
    }

    public LiveData<Event> getEventById(String id) {
        return mAppDao.getEventById(id);
    }

    public void deleteEvent(String calendarId, String eventId) {
        Call<List<SheetDeleteResponse>> deleteEvent = client.deleteEvent(calendarId, eventId);
        deleteEvent.enqueue(new Callback<List<SheetDeleteResponse>>() {
            @Override
            public void onResponse(Call<List<SheetDeleteResponse>> call, Response<List<SheetDeleteResponse>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful delete " + response.body());
                } else {
                    Log.d(TAG, "onResponse: not successful delete " + response.body());
                }
                fetchEvents(calendarId);
            }

            @Override
            public void onFailure(Call<List<SheetDeleteResponse>> call, Throwable t) {
                Log.d(TAG, "onFailure: delete event failure" + t.getMessage() + ", " + t.toString());
                fetchEvents(calendarId);
            }
        });
    }

    public void fetchEvents(String id) {
        Call<List<Event>> fetchEvents = client.getEvents(id);
        fetchEvents.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    new insertEventsTask(mAppDao).execute(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private static class insertEventsTask extends AsyncTask<List<Event>, Void, Void> {
        private AppDao mAppDao;

        insertEventsTask(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Void doInBackground(List... lists) {
            mAppDao.deleteAllEvents();
            mAppDao.insertEvents(lists[0]);
            return null;
        }
    }

    private static class InsertNewEvent extends AsyncTask<Event, Void, Void> {
        private AppDao mAppDao;

        InsertNewEvent(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Event... events) {
            mAppDao.insertNewEvent(events[0]);
            return null;
        }
    }
}
