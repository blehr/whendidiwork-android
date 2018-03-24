package com.brandonlehr.whendidiwork;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.UserTimer;
import com.brandonlehr.whendidiwork.viewModels.CreateEventViewModel;

import javax.inject.Inject;

public class TimerActivity extends AppCompatActivity {
    private static final String TAG = "TimerActivity";


    UserTimer mUserTimer;
    TextView startTimeTV;
    TextView endTimeTV;
    TextView elapsedTimeTV;
    Button timerButton, clearButton;

    Runnable updater;
    final Handler timerHandler = new Handler();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    MyTimerService mService;
    boolean mBound = false;

    Sheet mSelectedSheet;
    CreateEventViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Event");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Whendidiwork) getApplication()).getDIComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(CreateEventViewModel.class);

        startTimeTV = findViewById(R.id.start_time_tv);
        endTimeTV = findViewById(R.id.end_time_tv);
        elapsedTimeTV = findViewById(R.id.elapsed_time_tv);
        timerButton = findViewById(R.id.timer_button);
        clearButton = findViewById(R.id.clear_button);

        clearButton.setOnClickListener(view -> handleClearClick());

        timerButton.setOnClickListener(view -> {
            handleTimerButtonClick();
        });
    }

    void handleTimerButtonClick() {
        if (mUserTimer != null && mUserTimer.getStartTimeStamp() != null && mUserTimer.getEndTimeStamp() == null) {
            // stop
            Log.d(TAG, "handleTimerButtonClick: mUserTime not null " + mUserTimer.toString());

            Intent intent = new Intent(TimerActivity.this, MyTimerService.class);
            intent.setAction(MyTimerService.ACTION_STOP_FOREGROUND_SERVICE);
            startService(intent);
            timerHandler.removeCallbacks(updater);

            timerButton.setText(R.string.start_timer);
        } else if (mUserTimer == null || mUserTimer.getStartTimeStamp() == null) {
            // start
            Long beginTime = System.currentTimeMillis();
            Intent intent = new Intent(TimerActivity.this, MyTimerService.class);
            intent.setAction(MyTimerService.ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra(Constants.EXTRA_PARAM1, beginTime);
            intent.putExtra(Constants.EXTRA_PARAM2, mSelectedSheet.getName());  // may have to null check
            startService(intent);

            timerButton.setText(R.string.stop_timer);
        } else {
            timerButton.setText(R.string.create_event);
            Intent timerToEvent = new Intent(this, CreateEventActivity.class);
            timerToEvent.setAction("CREATE_EVENT_FROM_TIMER");
            startActivity(timerToEvent);
        }

    }


    void handleClearClick() {
        timerHandler.removeCallbacks(updater);
        Intent intent = new Intent(TimerActivity.this, MyTimerService.class);
        intent.setAction(MyTimerService.ACTION_CLEAR);
        startService(intent);
        timerButton.setText(R.string.start_timer);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyTimerService.TimerBinder binder = (MyTimerService.TimerBinder) service;
            mService = binder.getService();
            mBound = true;

            // if there is an incomplete timer that exists at creation
            if (mUserTimer != null && mUserTimer.getStartTimeStamp() != null && mUserTimer.getEndTimeStamp() == null) {
                // start
                Log.d(TAG, "onStart: try to restore state ");
                Intent beginIntent = new Intent(TimerActivity.this, MyTimerService.class);
                beginIntent.setAction(MyTimerService.ACTION_START_FOREGROUND_SERVICE);
                beginIntent.putExtra(Constants.EXTRA_PARAM1, mUserTimer.getStartTimeStamp());
                beginIntent.putExtra(Constants.EXTRA_PARAM2, mSelectedSheet.getName());  // may have to null check
                startService(beginIntent);

                timerButton.setText(R.string.stop_timer);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyTimerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        model.getSelectedSheet().observe(this, sheet -> mSelectedSheet = sheet);

        model.getUserTimer().observe(this, userTimer -> {
            mUserTimer = userTimer;

            if (userTimer != null) {
                Log.d(TAG, "onCreate: userTimer " + userTimer.toString());
                // set the start time text
                startTimeTV.setText(DateUtils.formatDateTime(this, mUserTimer.getStartTimeStamp(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL));

                // no end time - calculate elapsed time every second
                if (mUserTimer.getEndTimeStamp() == null) {
                    timerButton.setText(R.string.stop_timer);
                    updater = new Runnable() {

                            @Override
                            public void run () {
                                if(mBound) {
                                elapsedTimeTV.setText(DateUtils.formatElapsedTime(mService.getCurrentInterval()));
                                timerHandler.postDelayed(updater, 1000);
                            }
                        }
                    };
                    timerHandler.post(updater);

                } else {
                    // there is an end time
                    endTimeTV.setText(DateUtils.formatDateTime(this, mUserTimer.getEndTimeStamp(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                            DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL));
                    Long elapsedTime = (mUserTimer.getEndTimeStamp() - mUserTimer.getStartTimeStamp()) / 1000;
                    elapsedTimeTV.setText(DateUtils.formatElapsedTime(elapsedTime));
                    timerHandler.removeCallbacks(updater);
                    timerButton.setText(R.string.create_event);
                }
            } else {
                // clear all nothing to show
                startTimeTV.setText("");
                endTimeTV.setText("");
                elapsedTimeTV.setText("");
                timerButton.setText(R.string.start_timer);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
        timerHandler.removeCallbacks(updater);
    }

}
