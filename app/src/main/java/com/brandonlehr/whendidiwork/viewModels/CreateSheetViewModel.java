package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.CreateSheetPostBody;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.services.AuthWithServer;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/3/2018.
 */

public class CreateSheetViewModel extends ViewModel {
    private static final String TAG = "CreateSheetViewModel";

    MutableLiveData<Sheet> mSheet = new MutableLiveData<>();
    private AuthWithServer client;

    @Inject
    public CreateSheetViewModel(Retrofit retrofitClient) {
        client = retrofitClient.create(AuthWithServer.class);
    }

    public LiveData<Sheet> createSheet(CreateSheetPostBody createSheetPostBody) {
        goCreateSheet(createSheetPostBody);
        return mSheet;
    }
    public void goCreateSheet(CreateSheetPostBody createSheetPostBody) {

        Call<Sheet> createSheet = client.createSheet(createSheetPostBody);
        createSheet.enqueue(new Callback<Sheet>() {
            @Override
            public void onResponse(Call<Sheet> call, Response<Sheet> response) {
                if (response.isSuccessful()) {
                    mSheet.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Sheet> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
