package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.services.AuthWithServer;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/2/2018.
 */

public class CreateCalendarViewModel  extends ViewModel {
    private static final String TAG = "CreateCalendarViewModel";

    MutableLiveData<Calendar> mCalendar = new MutableLiveData<>();
//    private AuthWithServer client = RetrofitClient.getClient(this.getApplication()).create(AuthWithServer.class);

    private AuthWithServer client;

    @Inject
    public CreateCalendarViewModel(Retrofit retrofitClient) {
        client = retrofitClient.create(AuthWithServer.class);
    }
    public LiveData<Calendar> createCalendar(CreateCalendarPostBody createCalendarPostBody) {
        goCreateCalendar(createCalendarPostBody);
        return mCalendar;
    }
    public void goCreateCalendar(CreateCalendarPostBody createCalendarPostBody) {


        Call<Calendar> createCalendar = client.createCalendar(createCalendarPostBody);
        createCalendar.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (response.isSuccessful()) {
                    mCalendar.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
