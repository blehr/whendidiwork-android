package com.brandonlehr.whendidiwork.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.brandonlehr.whendidiwork.Dao.AppDao;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.models.UserTimer;
import com.brandonlehr.whendidiwork.services.ApiCalls;

import retrofit2.Retrofit;

/**
 * Created by blehr on 3/10/2018.
 */

public class UserRepository {
    private static final String TAG = "UserRepository";

    private LiveData<UserResponse> mUser;
    private LiveData<UserTimer> mUserTimer;

    private ApiCalls client;
    AppDao mAppDao;

    public UserRepository(Retrofit retrofitClient, AppDao appDao) {
        client = retrofitClient.create(ApiCalls.class);
        mAppDao = appDao;
        mUser = mAppDao.getUser();
        mUserTimer = mAppDao.getUserTimer();
    }

    public LiveData<UserTimer> getUserTimer() {
        return mUserTimer;
    }

    public void insertUserTimer(UserTimer userTimer) {
        new InsertUserTimerTask(mAppDao).execute(userTimer);
    }

    public void deleteUserTimer() {
        new DeleteUserTimerTask(mAppDao).execute();
    }

    private static class InsertUserTimerTask extends AsyncTask<UserTimer, Void, Void> {
        private AppDao asyncAppDao;

        InsertUserTimerTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(UserTimer... userTimers) {
            Log.d(TAG, "doInBackground: insert userTimer " + userTimers[0].toString());
            asyncAppDao.insertUserTimer(userTimers[0]);
            return null;
        }
    }

    private static class DeleteUserTimerTask extends AsyncTask<Void, Void, Void> {
        private AppDao asyncAppDao;

        DeleteUserTimerTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncAppDao.deleteUserTimer();
            Log.d(TAG, "doInBackground: deleting userTimer ");
            return null;
        }
    }


    public LiveData<UserResponse> getUser() {
        return mUser;
    }

    public void insertUser(UserResponse user) {
        Log.d(TAG, "insertUser: user.getId " + user.getGoogle().getId());
        if (mUser.getValue() != null && !mUser.getValue().getGoogle().getId().equals(user.getGoogle().getId())) {
            new DeleteUserTask(mAppDao).execute(user);
        } else {
            new InsertUserTask(mAppDao).execute(user);
        }
    }

    private static class DeleteUserTask extends AsyncTask<UserResponse, Void, Void> {
        private AppDao mAppDao;

        DeleteUserTask(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Void doInBackground(UserResponse... userResponses) {
            mAppDao.deleteOldUserInsertNew(userResponses[0]);
            return null;
        }
    }

    private static class InsertUserTask extends AsyncTask<UserResponse, Void, Void> {
        private AppDao asyncAppDao;

        InsertUserTask(AppDao appDao) {
            asyncAppDao = appDao;
        }

        @Override
        protected Void doInBackground(UserResponse... userResponses) {
            asyncAppDao.insertUser(userResponses[0]);
            Log.d(TAG, "doInBackground: inserting user");
            return null;
        }
    }
}
