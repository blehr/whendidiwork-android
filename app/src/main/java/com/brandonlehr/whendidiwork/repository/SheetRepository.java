package com.brandonlehr.whendidiwork.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.brandonlehr.whendidiwork.Dao.AppDao;
import com.brandonlehr.whendidiwork.models.CreateSheetPostBody;
import com.brandonlehr.whendidiwork.models.FileList;
import com.brandonlehr.whendidiwork.models.SelectedSheet;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by blehr on 3/9/2018.
 */

public class SheetRepository {
    private static final String TAG = "SheetRepository";

    private LiveData<List<Sheet>> mSheets;
    private LiveData<Sheet> mSelectedSheet;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private ApiCalls client;
    AppDao mAppDao;

    public SheetRepository(Retrofit retrofitClient, AppDao appDao) {
        client = retrofitClient.create(ApiCalls.class);
        mAppDao = appDao;
        mSheets = mAppDao.getAllSheets();
        mSelectedSheet = mAppDao.getSelectedSheet();
        isLoading.setValue(false);
        fetchFiles();
    }

    public LiveData<List<Sheet>> getSheets() {
        return mSheets;
    }

    public LiveData<Sheet> getSelectedSheet() {
        return mSelectedSheet;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void insertSheets(List<Sheet> sheets) {
        new InsertSheetsTask(mAppDao).execute(sheets);
    }

    public void updateSelectedSheet(Sheet sheet) {
        new InsertSheetTask(mAppDao).execute(sheet);
    }

    public void createSheet(CreateSheetPostBody createSheetPostBody) {
        isLoading.setValue(true);
        Call<Sheet> createSheet = client.createSheet(createSheetPostBody);
        createSheet.enqueue(new Callback<Sheet>() {
            @Override
            public void onResponse(Call<Sheet> call, Response<Sheet> response) {
                isLoading.setValue(false);
                new SetSelectedSheetAndInsertTask(mAppDao).execute(response.body());
            }

            @Override
            public void onFailure(Call<Sheet> call, Throwable t) {

            }
        });
    }

    private static class SetSelectedSheetAndInsertTask extends AsyncTask<Sheet, Void, Void> {
        private AppDao asyncAppDao;

        SetSelectedSheetAndInsertTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Sheet... sheets) {
            asyncAppDao.updateSelectedSheet(new SelectedSheet(sheets[0]));
            asyncAppDao.insertSheet(sheets[0]);
            return null;
        }
    }

    private static class InsertSheetsTask extends AsyncTask<List<Sheet>, Void, Void> {
        private AppDao asyncAppDao;

        InsertSheetsTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(List<Sheet>[] lists) {
            asyncAppDao.deleteAllSheets();
            asyncAppDao.insertSheets(lists[0]);
            return null;
        }
    }

    private static class InsertSheetTask extends AsyncTask<Sheet, Void, Void> {
        private AppDao asyncAppDao;

        InsertSheetTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Sheet... sheet) {
            asyncAppDao.updateSelectedSheet(new SelectedSheet(sheet[0]));
            return null;
        }

    }

    public void fetchFiles() {
        Call<FileList> getFiles = client.getFiles();
        getFiles.enqueue(new Callback<FileList>() {
            @Override
            public void onResponse(Call<FileList> call, Response<FileList> response) {
                if (response.isSuccessful()) {
                    new InsertSheetsTask(mAppDao).execute(response.body().getSheets());
                }
            }

            @Override
            public void onFailure(Call<FileList> call, Throwable t) {
                Log.d(TAG, "onFailure: fail to get fileList: " + t.getMessage());
            }
        });
    }

}
