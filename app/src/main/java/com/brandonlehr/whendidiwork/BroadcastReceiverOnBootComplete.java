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
    PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeofencingClient;
    List<Geofence> mGeofenceList = new ArrayList<>();
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "onReceive: BOOT COMPLETED ");
            mContext = context;
            mGeofencingClient = LocationServices.getGeofencingClient(context);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

            if ( sharedPref.contains( Constants.KEY_GEOFENCE_LAT ) && sharedPref.contains( Constants.KEY_GEOFENCE_LON )) {
                Log.d(TAG, "onReceive: In SharedPrefrences");
                double lat = Double.longBitsToDouble( sharedPref.getLong( Constants.KEY_GEOFENCE_LAT, -1 ));
                double lon = Double.longBitsToDouble( sharedPref.getLong( Constants.KEY_GEOFENCE_LON, -1 ));
                int fenceRadius = sharedPref.getInt(Constants.KEY_FENCE_RADIUS, 50);

                LatLng latLng = new LatLng( lat, lon );
                createGeoFence(latLng, fenceRadius);

            }
        }
    }

    private void createGeoFence(LatLng pos, int radius) {
        Log.d(TAG, "createGeoFence: ");
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                .setRequestId("whendidiwork?")

                .setCircularRegion(
                        pos.latitude,
                        pos.longitude,
                        radius
                )
                .setNotificationResponsiveness(60 * 1000)
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build());


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());
    }

    private GeofencingRequest getGeofencingRequest() {
        Log.d(TAG, "getGeofencingRequest: ");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "getGeofencePendingIntent: ");
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, LocationIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

}
