package com.pafoid.utils.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Abstract service user to track geofencing events
 *
 * Options : trackEnterEvent, trackExitEvent, trackDwellEvent
 * Set the options after the call to super in the constructor
 * Extend this service and add it to your Manifest to use it in your project
 */
public abstract class GeofenceTransitionsIntentService extends IntentService {
    public static final String TAG = "GeoFenceService";

    public static final int NOTIFICATION_ID = 1;

    //Options
    public boolean trackEnterEvent, trackExitEvent, trackDwellEvent;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "Error code : " + geofencingEvent.getErrorCode();
            Log.e(TAG, errorMessage);
            onGeofenceError(geofencingEvent);
            return;
        }

        // Get the transition type and its events
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        // Get the geofences that were triggered. A single event can trigger
        // multiple geofences.
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                if(trackEnterEvent){
                    for (Geofence geofence : triggeringGeofences) {
                        onGeofenceEntered(geofence);
                    }
                }else{
                    onGeofenceEventUnsupported(geofenceTransition);
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                if(trackExitEvent){
                    for (Geofence geofence : triggeringGeofences) {
                        onGeofenceExited(geofence);
                    }
                }else{
                    onGeofenceEventUnsupported(geofenceTransition);
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                if(trackDwellEvent){
                    for (Geofence geofence : triggeringGeofences) {
                        onGeofenceDwell(geofence);
                    }
                }else{
                    onGeofenceEventUnsupported(geofenceTransition);
                }
                break;

        }
    }

    protected void onGeofenceEventUnsupported(int geofenceTransition){
        // Log the error.
        Log.e(TAG, "Invalid transition type : " + geofenceTransition);
    }

    //Abstract Methods
    protected abstract void onGeofenceEntered(Geofence geofence);

    protected abstract void onGeofenceExited(Geofence geofence);

    protected abstract void onGeofenceDwell(Geofence geofence);

    protected abstract void onGeofenceError(GeofencingEvent geofencingEvent);
}
