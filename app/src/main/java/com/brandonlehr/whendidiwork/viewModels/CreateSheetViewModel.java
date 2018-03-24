package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.brandonlehr.whendidiwork.models.CreateSheetPostBody;
import com.brandonlehr.whendidiwork.repository.SheetRepository;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * Created by blehr on 3/3/2018.
 */

public class CreateSheetViewModel extends ViewModel {
    private static final String TAG = "CreateSheetViewModel";

    SheetRepository mSheetRepository;
    private MutableLiveData<Boolean> isLoading;
    private ApiCalls client;

    @Inject
    public CreateSheetViewModel(Retrofit retrofitClient, SheetRepository sheetRepository) {
        client = retrofitClient.create(ApiCalls.class);
        mSheetRepository = sheetRepository;
        isLoading = mSheetRepository.getIsLoading();
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void createSheet(CreateSheetPostBody createSheetPostBody) {
        mSheetRepository.createSheet(createSheetPostBody);
    }
}
