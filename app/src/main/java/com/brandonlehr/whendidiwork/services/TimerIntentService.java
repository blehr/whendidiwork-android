package com.brandonlehr.whendidiwork.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.brandonlehr.whendidiwork.Constants;
import com.brandonlehr.whendidiwork.MainActivity;
import com.brandonlehr.whendidiwork.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TimerIntentService extends IntentService {
    private static final String TAG = "TimerIntentService";

    private static final String EXTRA_PARAM1 = "com.brandonlehr.whendidiwork.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.brandonlehr.whendidiwork.services.extra.PARAM2";
    Runnable updater;
    NotificationCompat.Builder mBuilder;
    final Handler timerHandler = new Handler();

    public TimerIntentService() {
        super("TimerIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionStart(Context context, Long param1, Long param2) {
        Intent intent = new Intent(context, TimerIntentService.class);
        intent.setAction(Constants.ACTION_START);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionStop(Context context, Long param1, Long param2) {
        Intent intent = new Intent(context, TimerIntentService.class);
        intent.setAction(Constants.ACTION_STOP);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
//        context.stopService(intent);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_START.equals(action)) {
                final Long param1 = intent.getLongExtra(EXTRA_PARAM1, 0L);
                final Long param2 = intent.getLongExtra(EXTRA_PARAM2, 0L);
                handleActionStart(param1, param2);
            } else if (Constants.ACTION_STOP.equals(action)) {
                final Long param1 = intent.getLongExtra(EXTRA_PARAM1, 0L);
                final Long param2 = intent.getLongExtra(EXTRA_PARAM2, 0L);
                handleActionStop(param1, param2);
            }
        }
    }


    private void handleActionStart(Long timeStamp, Long param2) {
        Log.d(TAG, "handleActionStart: " + timeStamp);


        //    Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        mBuilder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID);
        mBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_whendidiwork)
                .setContentTitle("Whendidiwork")
                .setContentText(timeStamp.toString())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        startForeground(Constants.NOTIFICATION_ID, mBuilder.build());

        updater = new Runnable() {
            @Override
            public void run() {
                Long elapsedTime = (System.currentTimeMillis() - timeStamp) / 1000;
//                timerText.setText(elapsedTime.toString());
//                Log.d(TAG, "run: elapsedTime " + elapsedTime.toString());
                mBuilder.setContentText(elapsedTime.toString());
                notificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
                timerHandler.postDelayed(updater, 1000);
            }
        };
        timerHandler.post(updater);
    }




    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStop(Long param1, Long param2) {
        // TODO: Handle action Baz
        stopSelf();
        stopForeground(true);
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
