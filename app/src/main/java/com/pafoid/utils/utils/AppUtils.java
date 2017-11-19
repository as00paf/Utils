package com.pafoid.utils.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Helper class used for Application related functions
 */
public class AppUtils {
    public static final String TAG = "AppUtils";

    /**
     * Helper method used to retrieve the application's version code
     * @param context the Context to used to retrieve the version code
     * @return an integer representing the version of the application
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not getInstance package name: " + e);
        }
    }

    /**
     * Helper method used to retrieved the application's version name
     * @param context the Context to use to get the version name
     * @return a String value that represents the name of the application's version
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not getInstance package name: " + e);
        }
    }

    /**
     * Helper method used to show the system's application details page
     * @param context the Context to use
     */
    public static void startInstalledAppDetailsActivity(Context context) {
        if (context == null) {
            Log.e(TAG, "Context cannot be null");
            return;
        }

        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(i);
    }

    /**
     * Helper method used to show a specific application details page
     * @param context the Context to use
     * @param packageName the packageName of the application to show
     */
    public static void startInstalledAppDetailsActivity(Context context, String packageName) {
        if (context == null) {
            Log.e(TAG, "Context cannot be null");
            return;
        }

        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + packageName));
        context.startActivity(i);
    }

    /**
     * Helper method used to set the title, color and icon of the activity when the user presses on
     * the multi-tasking button
     * @param activity the calling Activity
     * @param description the description to show
     * @param iconRes the icon resource to show
     * @param color the color resource to show
     */
    public static void setActivityDescription(Activity activity, String description, @DrawableRes int iconRes, @ColorRes int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setTaskDescription(new ActivityManager.TaskDescription(description, BitmapUtils.drawableToBitmap(activity.getDrawable(iconRes)), ContextCompat.getColor(activity, color)));
        }
    }
}
