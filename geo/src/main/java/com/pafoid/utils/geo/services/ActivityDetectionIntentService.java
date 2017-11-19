package com.pafoid.utils.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.pafoid.utils.geo.utils.ActivityRecognitionUtils;
import com.pafoid.utils.geo.utils.AppConstants;

import java.util.ArrayList;

/**
 *  Abstract IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public abstract class ActivityDetectionIntentService extends IntentService {

    protected static final String TAG = "ActivityDetectServ";

    private static final int DETECTION_THRESHOLD = 85;

    private int threshold = DETECTION_THRESHOLD;
    private boolean log = false;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public ActivityDetectionIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(AppConstants.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Log each activity.
        boolean inCar = false;
        for (DetectedActivity da: detectedActivities) {
            if(log) {
                Log.i(TAG, ActivityRecognitionUtils.getActivityString(
                        getApplicationContext(),
                        da.getType()) + " " + da.getConfidence() + "%"
                );
            }

            if(da.getType() == DetectedActivity.IN_VEHICLE && da.getConfidence() >= threshold){
                inCar = true;
            }
        }

        if(!getIsInCar()){
            if(inCar){
                if(log) Log.d(TAG, "App thinks you're in a car right now");
                setIsInCar(true);
                onCarActivityRecognized();
            }
        }else{
            if(!inCar){
                if(log) Log.d(TAG, "App thought you were in a car");
                setIsInCar(false);
            }
        }

        // Broadcast the list of detected activities.
        localIntent.putExtra(AppConstants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    //Abstract Methods
    protected abstract void onCarActivityRecognized();

    //Preferences
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }


    private void setIsInCar(boolean isInCar) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(AppConstants.LAST_DETECTED_ACTIVITY_KEY, isInCar)
                .commit();
    }

    private boolean getIsInCar() {
        return getSharedPreferencesInstance()
                .getBoolean(AppConstants.LAST_DETECTED_ACTIVITY_KEY, false);
    }


}
