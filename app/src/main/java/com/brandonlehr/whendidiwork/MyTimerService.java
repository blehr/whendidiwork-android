package com.brandonlehr.whendidiwork;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.brandonlehr.whendidiwork.models.UserTimer;
import com.brandonlehr.whendidiwork.repository.UserRepository;

import javax.inject.Inject;

public class MyTimerService extends Service {
    private static final String TAG = "MyTimerService";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_AND_SET_END = "ACTION_STOP_AND_SET_END";

    public static final String ACTION_CLEAR = "ACTION_CLEAR";

    Runnable updater;
    NotificationCompat.Builder builder;
    final Handler timerHandler = new Handler();
    Long elapsedTime = 0L;
    private final IBinder mBinder = new TimerBinder();

    UserTimer newUserTimer;

    @Inject
    UserRepository mUserRepository;


    public class TimerBinder extends Binder {
        MyTimerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyTimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Long getCurrentInterval() {
        return elapsedTime;
    }

    public MyTimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((Whendidiwork) getApplication()).getDIComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    Long beginTime = intent.getLongExtra(Constants.EXTRA_PARAM1, 0L);
                    String sheetName = intent.getStringExtra(Constants.EXTRA_PARAM2);
                    startForegroundService(beginTime, sheetName);
//                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    saveEndTime();
//                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_CLEAR:
                    clearTimerData();
//                    Toast.makeText(getApplicationContext(), "You click Clear button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_AND_SET_END:
                    saveEndTime();
                    backToTimerActivity();
//                    Toast.makeText(getApplicationContext(), "You click Stop button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // build and start foreground service
    private void startForegroundService(Long beginTime, String sheetName) {
        Log.d(TAG, "startForegroundService: ");

        newUserTimer = new UserTimer();
        newUserTimer.setStartTimeStamp(beginTime);
        mUserRepository.insertUserTimer(newUserTimer);

        // create notification default intent
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }


        //create notification builder
        builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID);

        builder.setWhen(beginTime);
        builder.setSmallIcon(R.mipmap.ic_launcher_whendidiwork);

        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_LOW);

        // Add Stop button intent in notification.
        Intent stopIntent = new Intent(this, MyTimerService.class);
        stopIntent.setAction(ACTION_STOP_AND_SET_END);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_menu_my_calendar, "Stop", pendingPrevIntent);
        builder.addAction(prevAction);

        builder.setContentTitle(sheetName);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(Constants.NOTIFICATION_ID, notification);

        // mine
        updater = new Runnable() {
            @Override
            public void run() {
                elapsedTime = (System.currentTimeMillis() - beginTime) / 1000;

                builder.setContentText(DateUtils.formatElapsedTime(elapsedTime));
                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
                timerHandler.postDelayed(updater, 1000);
            }
        };
        timerHandler.post(updater);
    }

    private void saveEndTime() {
        if (newUserTimer != null) {
            newUserTimer.setEndTimeStamp(System.currentTimeMillis());
            mUserRepository.insertUserTimer(newUserTimer);
        }
        timerHandler.removeCallbacks(updater);
        stopForegroundService();
    }

    private void clearTimerData() {
        timerHandler.removeCallbacks(updater);
        mUserRepository.deleteUserTimer();
        stopForegroundService();
    }

    private void stopForegroundService() {
        Log.d(TAG, "stopForegroundService: ");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    public void backToTimerActivity() {
        Intent timerActivityIntent = new Intent(this, TimerActivity.class);
        startActivity(timerActivityIntent);
    }


}
