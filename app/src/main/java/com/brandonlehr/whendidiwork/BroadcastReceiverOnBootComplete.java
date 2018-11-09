package com.brandonlehr.whendidiwork;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverOnBoot";


    static final String ACTION_PROCESS_UPDATES =
            "com.brandonlehr.whendidiwork.action" +
                    ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "onReceive: BOOT COMPLETED " + context);

            OnBootService.enqueueWork(context, intent);

        }

        if (intent.getAction().equalsIgnoreCase(ACTION_PROCESS_UPDATES)) {
            Log.d(TAG, "onReceive: LOCATION INTENT ACTION RECEIVED");
            LocationIntentService.enqueueWork(context, intent);
        }
    }

}
