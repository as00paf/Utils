package com.pafoid.utils.geo.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.pafoid.utils.geo.R;
import com.pafoid.utils.permissions.PermissionManager;
import com.pafoid.utils.utils.GeoUtils;

import java.util.ArrayList;

/**
 *
 */
public class GoogleApiController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "GoogleApiController";

    public static final int ENABLE_GPS_REQUEST_CODE = 1234;
    private static final int INTERVAL = 5000;
    private static final int MIN_INTERVAL = 1000;
    private static final int DEFAULT_DISTANCE_THRESHOLD = 100;

    private GoogleApiClient mGoogleApiClient;

    private Location lastGPSLocation = null;
    private Location initialGPSLocation = null;
    private LocationRequest mLocationRequest;

    //Options
    private int distanceThreshold = DEFAULT_DISTANCE_THRESHOLD;
    private int interval = INTERVAL;
    private int minInterval = MIN_INTERVAL;
    private Boolean isRequestingLocationUpdates = false;
    private Boolean usesActivityDetection = false;

    private ArrayList<Delegate> delegates = new ArrayList<>();

    public GoogleApiController(Context context) {
        init(context);
    }

    public GoogleApiController(Context context, boolean usesActivityDetection) {
        this.usesActivityDetection = usesActivityDetection;
        init(context);
    }

    private void init(Context context) {
        GoogleApiClient.Builder apiClientBuilder = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API);

        if(usesActivityDetection) apiClientBuilder.addApi(ActivityRecognition.API);

        mGoogleApiClient = apiClientBuilder.build();
    }

    private void initLocationRequest(final Activity activity){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(40000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, ENABLE_GPS_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.gps_disabled_message))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ENABLE_GPS_REQUEST_CODE);
                    }
                })
                .setNegativeButton(activity.getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void connect(Activity activity){
        mGoogleApiClient.connect();

        initLocationRequest(activity);
    }

    public void disconnect(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void checkLocationSettings(final Activity activity){
        LocationRequest request= new LocationRequest()
                .setInterval(minInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder b = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        b.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                if(locationSettingsResult.getStatus().getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    try {
                        locationSettingsResult
                                .getStatus()
                                .startResolutionForResult(activity, PermissionManager.PERMISSIONS_REQUEST_LOCATION_SETTINGS);
                    }
                    catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Log.e(TAG, "An Error occured : " + e.getMessage());
                    }
                }
            }
        });
    }

    public void startLocationUpdates(Activity activity) {
        if (!PermissionManager.getInstance(activity).isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.e(TAG, "Location Permission not granted!");
            return;
        }

        if(!isRequestingLocationUpdates && mGoogleApiClient.isConnected()){
            isRequestingLocationUpdates = true;
            //noinspection MissingPermission
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            Log.e(TAG, "ApiClient not connected yet!");
        }
    }

    public void stopLocationUpdates(){
        if(mGoogleApiClient != null && isRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            isRequestingLocationUpdates = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "CONNECTION SUCCESS !");

        //noinspection MissingPermission
        lastGPSLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        notifyDelegatesApiConnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "CONNECTION FAILED !");
        notifyDelegatesApiConnectionFailed();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "CONNECTION SUSPENDED !");
        notifyDelegatesApiConnectionSuspended();
    }

    @Override
    public void onLocationChanged(Location location){
        if(lastGPSLocation == null){
            lastGPSLocation = location;
            initialGPSLocation = location;

            notifyDelegatesLocationAvailable(location);
        }else{
            float distance = GeoUtils.distanceBetweenLocations(lastGPSLocation, location);
            if(distance > 100){
                lastGPSLocation = location;
                notifyDelegatesLocationChanged(location);
            }

            distance = GeoUtils.distanceBetweenLocations(initialGPSLocation, location);
            if(distance > distanceThreshold){
                Log.d(TAG, "Distance from last location : " + distance + ". Its more than the threshold : " + distanceThreshold);
                initialGPSLocation = location;
                notifyDelegatesThresholdReached();
            }
        }
    }

    //Getters/Setters
    public Boolean isConnected(){
        return  mGoogleApiClient!= null && mGoogleApiClient.isConnected();
    }

    public Location getLastGPSLocation() {
        return lastGPSLocation;
    }

    public void setLastGPSLocation(Location lastGPSLocation) {
        this.lastGPSLocation = lastGPSLocation;
    }

    public Boolean isRequestingLocationUpdates() {
        return isRequestingLocationUpdates;
    }

    public GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    public LatLng getLastLatLng() {
        if(lastGPSLocation == null){
            return null;
        }

        return new LatLng(lastGPSLocation.getLatitude(), lastGPSLocation.getLongitude());
    }

    //Delegates
    public void registerDelegate(Delegate delegate){
        if(!delegates.contains(delegate)){
            delegates.add(delegate);
        }
    }

    public void unregisterDelegate(Delegate delegate){
        if(delegates.contains(delegate)){
            delegates.remove(delegate);
        }
    }

    public void notifyDelegatesLocationAvailable(Location location){
        for (Delegate delegate : delegates) {
            if(delegate != null) delegate.onLocationAvailable(location);
        }
    }

    public void notifyDelegatesLocationChanged(Location location){
        for (Delegate delegate : delegates) {
            if(delegate != null) delegate.onLocationChanged(location);
        }
    }

    public void notifyDelegatesApiConnected(){
        for (Delegate delegate : delegates) {
            if(delegate != null && delegate instanceof ApiConnectedDelegate) ((ApiConnectedDelegate) delegate).onApiConnected();
        }
    }

    public void notifyDelegatesApiConnectionFailed(){
        for (Delegate delegate : delegates) {
            if(delegate != null && delegate instanceof ApiConnectedDelegate) ((ApiDelegate) delegate).onApiConnectionFailed();
        }
    }

    public void notifyDelegatesApiConnectionSuspended(){
        for (Delegate delegate : delegates) {
            if(delegate != null && delegate instanceof ApiConnectedDelegate) ((ApiDelegate) delegate).onApiConnectionSuspended();
        }
    }

    private void notifyDelegatesThresholdReached() {
        for (Delegate delegate : delegates) {
            if(delegate != null && delegate instanceof ThresholdDelegate) ((ThresholdDelegate) delegate).onThresholdReached();
        }
    }

    //Interface
    public interface Delegate{
        void onLocationAvailable(Location location);
        void onLocationChanged(Location location);
    }

    public interface ApiConnectedDelegate extends Delegate{
        void onApiConnected();
    }

    public interface ApiDelegate extends ApiConnectedDelegate{
        void onApiConnectionFailed();
        void onApiConnectionSuspended();
    }

    public interface ThresholdDelegate extends ApiConnectedDelegate{
        void onThresholdReached();
    }
}
