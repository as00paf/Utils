package com.pafoid.utils.utils;

import android.content.Context;
import android.os.Build;

/**
 * Helper class used to deal with Android O's new protected storage feature
 */
public class ContextUtils {

    /**
     * Helper method used to get the right Context for protected storage
     * @param context the Context to use
     * @return the Storage Protected Context if possible
     */
    public static Context getProtectedStorageContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !context.isDeviceProtectedStorage()) {
            return context.createDeviceProtectedStorageContext();
        } else {
            return context;
        }
    }
}
