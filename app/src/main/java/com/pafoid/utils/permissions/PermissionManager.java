package com.pafoid.utils.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Helper singleton class used to grant permissions for your application
 */
public class PermissionManager {

    public static final String TAG = "PermissionManager";

    public static final int PERMISSIONS_REQUEST_LOCATION_SETTINGS = 1338;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private static PermissionManager instance;

    private Context context;

    private PermissionManager(Context context) {
        super();
        this.context = context;
    }

    /**
     * Use this method as the constructor for PermissionManager since it is a singleton
     * @param context should be the application context
     * @return the singleton instance of the PermissionManager
     */
    public static PermissionManager getInstance(Context context) {
        if(instance == null){
            instance = new PermissionManager(context);
        }
        return instance;
    }

    /**
     * Helper method to verify if a permission has been granted
     * @param permission the permission to verify
     * @return true or false
     */
    public Boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Helper method to request a permission from an Activity
     * @param act the Activity that is making the request which should override onRequestPermissionsResult
     * @param permission the permission to verify
     */
    public void requestPermission(Activity act, String permission) {
        if (ContextCompat.checkSelfPermission(act, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(act, permission)) {
                Log.i(TAG, "We need use this permission");
            }

            try{
                ActivityCompat.requestPermissions(act, new String[]{permission},
                        PermissionsConstants.getRequestCodeForPermission(permission));
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Helper method to request a permission from a Fragment
     * @param fragment the Activity that is making the request which should override onRequestPermissionsResult
     * @param permission the permission to verify
     */
    public void requestPermission(Fragment fragment, String permission) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
                Log.i(TAG, "We need use this permission");
            }

            try{
                fragment.requestPermissions(new String[]{permission},
                        PermissionsConstants.getRequestCodeForPermission(permission));
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Interface used to get notified when a permission is accepted or denied
     */
    public interface PermissionRequestListener{
        void onPermissionAccepted(String permission);
        void onPermissionDenied(String permission);
    }
}
