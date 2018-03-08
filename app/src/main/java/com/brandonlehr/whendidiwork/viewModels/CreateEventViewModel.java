package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.CreateEventPostBody;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.services.AuthWithServer;

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
    private AuthWithServer client;


    @Inject
    public CreateEventViewModel(Retrofit retrofitClient) {
        client = retrofitClient.create(AuthWithServer.class);
    }

    public LiveData<Event> makeEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        Log.d(TAG, "makeEvent: ");
        createEvent(calendarId, sheetId, postBody);
        return createEventResponse;
    }

    public void createEvent(String calendarId, String sheetId, HashMap<String, CreateEventPostBody> postBody) {
        Log.d(TAG, "createEvent: ");
        Call<Event> createEvent = client.createEvent(calendarId, sheetId, postBody);
        createEvent.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    createEventResponse.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
