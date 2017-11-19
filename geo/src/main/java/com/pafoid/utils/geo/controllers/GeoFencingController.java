package com.pafoid.utils.geo.controllers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pafoid.utils.geo.R;
import com.pafoid.utils.geo.models.ICircle;
import com.pafoid.utils.geo.services.ActivityDetectionIntentService;
import com.pafoid.utils.geo.services.GeofenceTransitionsIntentService;
import com.pafoid.utils.geo.utils.AppConstants;
import com.pafoid.utils.geo.utils.DistanceComparator;
import com.pafoid.utils.geo.utils.GeofenceErrorMessages;
import com.pafoid.utils.geo.utils.MapItemsUtils;
import com.pafoid.utils.permissions.PermissionManager;

import java.util.ArrayList;
import java.util.Collections;

public class GeoFencingController implements OnCompleteListener<Void>, GoogleApiController.ThresholdDelegate, ResultCallback<Status> {

    public static final String TAG = "GeoFencingManager";

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    //Objects
    private Activity context;
    private GeofencingClient mGeofencingClient;
    private MapItemsUtils mapItemsUtils;
    private GoogleApiController apiController;

    //Data
    private ArrayList<ICircle> geofences;
    private ArrayList<Geofence> geoFenceList;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    private PendingIntent mGeofencePendingIntent;

    public GeoFencingController(Activity context, GoogleApiController apiController, ArrayList<ICircle> geofences) {
        this.context = context;
        this.apiController = apiController;
        this.geofences = geofences;

        mGeofencingClient = LocationServices.getGeofencingClient(context);

        //Distance handler
        apiController.registerDelegate(this);
    }

    private void populateGeofenceList() {
        //Sort by distance
        Location currentLocation = apiController.getLastGPSLocation();
        Collections.sort(geofences, new DistanceComparator(currentLocation));

        geoFenceList = new ArrayList();
        int size = Math.min(geofences.size(), 100);//Limit to 100 Geofences
        for (int i = 0; i < size; i++) {
            ICircle circle = geofences.get(i);
            Log.e(TAG, "Creating geofence " + i + " with id " + circle.getId() + " @ " + circle.getLatitude() + ", " + circle.getLongitude());

            geoFenceList.add(new Geofence.Builder()
                    .setRequestId(circle.getId())
                    .setCircularRegion(
                            circle.getLatitude(),
                            circle.getLongitude(),
                            circle.getRadius() //Radius in meters
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    //Geofence Tasks
    public void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    private void addGeofences() {
        //Geofences
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission not granted !");
            return;
        }

        Log.e(TAG, "Registering Geofences !");

        //noinspection MissingPermission
        mGeofencingClient
                .addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d(TAG, "Geofence added !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.e(TAG, "Failed to add Geofence ! " + e.getMessage() );
                    }
                });

        mPendingGeofenceTask = PendingGeofenceTask.NONE;
    }

    private void removeGeofences() {
        if (!grantPermissions()) {
            return;
        }

        Log.e(TAG, "Removing Geofences !");

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    //Request & Intent
    private GeofencingRequest getGeofencingRequest() {
        if(geoFenceList == null || geoFenceList.size() == 0) populateGeofenceList();

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geoFenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(context, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    //Map
    public void addCirclesToMap(GoogleMap googleMap) {
        if(mapItemsUtils == null) mapItemsUtils = new MapItemsUtils(context);
        mapItemsUtils.setGoogleMap(googleMap);
        for (ICircle iCircle : geofences) {
            mapItemsUtils.createCircleWithData(iCircle);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapItemsUtils.zoomOnMarkers(20);
            }
        }, 1000);
    }

    //Location
    @Override
    public void onLocationAvailable(Location location) {
        populateGeofenceList();
        if(!getGeofencesAdded()) mPendingGeofenceTask = PendingGeofenceTask.ADD;
        performPendingGeofenceTask();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onApiConnected() {
        requestActivityUpdates();
    }

    @Override
    public void onThresholdReached() {
        Log.d(TAG, "Threshold reached! Will now recreate geofences");
        removeGeofences();
        populateGeofenceList();
        addGeofences();
    }

    //Activity Recognition
    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            Log.d(TAG, context.getString(requestingUpdates ? R.string.activity_updates_added :
                    R.string.activity_updates_removed));
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(context, ActivityDetectionIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void requestActivityUpdates() {
        Log.d(TAG, "Requesting Activity Updates");

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                apiController.getApiClient(),
                0,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    public void removeActivityUpdates() {
        if (!apiController.getApiClient().isConnected()) {
            Toast.makeText(context, context.getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                apiController.getApiClient(),
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }


    //Preferences
    private SharedPreferences getSharedPreferencesInstance() {
        return context.getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, context.MODE_PRIVATE);
    }

    private void updateGeofencesAdded(boolean added) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(AppConstants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    private boolean getGeofencesAdded() {
        return getSharedPreferencesInstance() .getBoolean(
                AppConstants.GEOFENCES_ADDED_KEY, false);
    }

    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(AppConstants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .apply();
    }

    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(AppConstants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    //Permissions
    private boolean grantPermissions() {
        if(!PermissionManager.getInstance(context).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            PermissionManager.getInstance(context).requestPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }

        mPendingGeofenceTask = PendingGeofenceTask.ADD;

        return true;
    }
}
