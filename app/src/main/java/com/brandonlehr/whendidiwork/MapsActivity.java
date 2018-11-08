package com.brandonlehr.whendidiwork;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private Marker centerMarker;
    private LatLng centerPos;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 324;
    Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;
    ProgressBar mProgressBar;
    Button mapButton, clearButton;
    SeekBar mSeekBar;
    Integer fenceRadius = 10;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeofencingClient mGeofencingClient;
    List<Geofence> mGeofenceList = new ArrayList<>();
    PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Location Notifications");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapButton = findViewById(R.id.mapButton);
        clearButton = findViewById(R.id.clearButton);
        mSeekBar = findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: progress " + progress);
                fenceRadius = progress;
                drawGeofence();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mapButton.setOnClickListener(view -> {
            createGeoFence(centerMarker.getPosition());
            Toast.makeText(this, "Latitude: " + centerMarker.getPosition().latitude + " Longitude: " + centerMarker.getPosition().longitude + " Radius: " + fenceRadius, Toast.LENGTH_LONG).show();
        });

        clearButton.setOnClickListener(view -> {
            removeGeofences();
        });



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Toast.makeText(this, "permissions granted", Toast.LENGTH_LONG).show();
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "getDeviceLocation: GETTING DEVICE LOCATION");
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d(TAG, "onSuccess: lastKnownLocation " + location.toString());

                        boolean fromPref = recoverGeofenceMarker();
                        if (!fromPref) {
                            centerPos = new LatLng(location.getLatitude(), location.getLongitude());
                            createMarker(centerPos);
                            Log.d(TAG, "getDeviceLocation: no stored pref");
                        }
                        updateMapWithLocation(centerPos);
                        Log.d(TAG, "getDeviceLocation: STORED PREF");
                    }
                });
    }

    private void updateMapWithLocation(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    private void createMarker(LatLng loc) {
        centerPos = loc;
        centerMarker = mMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Center Here").snippet("Long Press To Drag")
                .draggable(true));
        drawGeofence();
    }

    private void createGeoFence(LatLng pos) {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                .setRequestId("whendidiwork?")

                .setCircularRegion(
                        pos.latitude,
                        pos.longitude,
                        fenceRadius
                )
                .setNotificationResponsiveness(60 * 1000)
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d(TAG, "onSuccess: added geofence ");
                        saveGeofencePref();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.d(TAG, "onFailure: failed to add geofence ");
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, LocationIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private  void removeGeofences() {
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        deleteGeofencePref();
                        Toast.makeText(MapsActivity.this, "Location notification removed", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                    }
                });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd: " + marker.getPosition());
                centerPos = marker.getPosition();
                drawGeofence();
            }
        });
        Log.d(TAG, "onMapReady: HERERERERERERERERRERERERRER");
        updateLocationUI();
    }

    // Saving GeoFence marker with prefs mng
    private void saveGeofencePref() {
        Log.d(TAG, "saveGeofence()");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong( Constants.KEY_GEOFENCE_LAT, Double.doubleToRawLongBits( centerPos.latitude ));
        editor.putLong( Constants.KEY_GEOFENCE_LON, Double.doubleToRawLongBits( centerPos.longitude ));
        editor.putInt( Constants.KEY_FENCE_RADIUS, fenceRadius);
        editor.apply();
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if (mMap == null) return;

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( centerPos )
                .strokeWidth(10)
                .strokeColor(Color.YELLOW)
                .fillColor( Color.argb(100, 150,150,150) )
                .radius((double) fenceRadius);
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

    // Recovering last Geofence marker
    private boolean recoverGeofenceMarker() {
        Log.d(TAG, "recoverGeofenceMarker");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if ( sharedPref.contains( Constants.KEY_GEOFENCE_LAT ) && sharedPref.contains( Constants.KEY_GEOFENCE_LON )) {
            double lat = Double.longBitsToDouble( sharedPref.getLong( Constants.KEY_GEOFENCE_LAT, -1 ));
            double lon = Double.longBitsToDouble( sharedPref.getLong( Constants.KEY_GEOFENCE_LON, -1 ));
            fenceRadius = sharedPref.getInt(Constants.KEY_FENCE_RADIUS, 50);

            LatLng latLng = new LatLng( lat, lon );
            centerPos = latLng;
            createMarker(latLng);
            mSeekBar.setProgress(fenceRadius);
            drawGeofence();
            return true;
        }
        return false;
    }

    private void deleteGeofencePref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(Constants.KEY_FENCE_RADIUS);
        editor.remove(Constants.KEY_GEOFENCE_LAT);
        editor.remove(Constants.KEY_GEOFENCE_LON);
        editor.apply();
    }
}
