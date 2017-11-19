package com.pafoid.utils.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;

import com.pafoid.utils.R;
import com.pafoid.utils.permissions.PermissionManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class used to deal with the Android share dialog
 */
public class ShareContentUtility {
    private static final String TAG = "ShareContentUtility";

    /**
     * This function is used for sharing assets from our app to other applications
     * using the Android Intent system.
     *
     * This function handles only certain applications to share to, and handles them all on a per-application basis.
     * Example: Facebook only shares a string containing a link to Google Play and a text, meanwhile Twitter allowed the image, along with a string.
     *
     * @param activity      - The Activity from the Activity that called this method. Needed for Intent usage, PackageManager usage, etc.
     * @param sharedBitmap  - The bitmap we want to send to other applications
     * @param text          - The text we want to send to other applications
     * @param title         - The title for the Android sharing Intent chooser.
     */

    public static void navigateToSharingContent(Activity activity, Bitmap sharedBitmap, String text, String title) {
        if(!PermissionManager.getInstance(activity).isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.d(TAG, "External Storage permission not granted");
            PermissionManager.getInstance(activity).requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return;
        }

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        String subject = String.format(activity.getString(R.string.sent_from),activity.getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject );
        emailIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, sharedBitmap));
        emailIntent.setType("application/image");

        PackageManager pm = activity.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, title);

        String defaultSmsPackageName = "mms";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity.getApplicationContext()); // Need to change the build to API 19
        }

        if(defaultSmsPackageName == null) defaultSmsPackageName = "mms";

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++)
        {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email"))
            {
                emailIntent.setPackage(packageName);
            }
            else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains(defaultSmsPackageName) || packageName.contains("tumblr"))
            {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter"))
                {
                    intent.putExtra(Intent.EXTRA_TEXT, text + " https://play.google.com/store/apps/details?id=tourism.sidekickinteractive.com.tourismsidekick");
                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, sharedBitmap));
                }
                else if(packageName.contains("facebook"))
                {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT,  "https://play.google.com/store/apps/details?id=tourism.sidekickinteractive.com.tourismsidekick");
                }
                else if(packageName.contains(defaultSmsPackageName))
                {
                    intent.putExtra("sms_body", text);
                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, sharedBitmap));
                    intent.setType("text/plain");
                }
                else if(packageName.contains("tumblr"))
                {
                    intent.putExtra(Intent.EXTRA_TEXT, text + " https://play.google.com/store/apps/details?id=tourism.sidekickinteractive.com.tourismsidekick");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        activity.startActivity(openInChooser);
    }

    /**
     *
     * Converts a Bitmap to an Uri
     *
     * @param inContext - Context of the application
     * @param inImage   - Bitmap we want to convert before sharing
     * @return - Path of the Uri
     */

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
