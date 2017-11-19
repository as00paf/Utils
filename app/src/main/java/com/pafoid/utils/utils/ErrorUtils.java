package com.pafoid.utils.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.Log;

import com.pafoid.utils.R;

/**
 * Helper class used to deal with Errors and ErrorDialogs
 */
public class ErrorUtils {

    private static final String TAG = "ErrorUtils";

    /**
     * Helper method used to show an error dialog
     * @param context the context to use
     * @param titleRes the String resource to use for the dialog's title
     * @param messageRes the String resource to use for the dialog's message
     */
    public static void showErrorDialog(Context context, @StringRes int titleRes, @StringRes int messageRes) {
        showErrorDialog(context, context.getString(titleRes), context.getString(messageRes), R.style.Theme_AppCompat_Light_Dialog_Alert, null);
    }

    /**
     * Helper method used to show an error dialog
     * @param context the context to use
     * @param title the String value to use for the dialog's title
     * @param message the String value to use for the dialog's message
     */
    public static void showErrorDialog(Context context, String title, String message) {
        showErrorDialog(context, title, message, R.style.Theme_AppCompat_Light_Dialog_Alert, null);
    }

    /**
     * Helper method used to show an error dialog
     * @param context the Context to use
     * @param titleRes the String resource to use for the dialog's title
     * @param messageRes the String resource to use for the dialog's message
     * @param themeRes the Style resource to use for the dialog
     */
    public static void showErrorDialog(Context context, @StringRes int titleRes, @StringRes int messageRes, @StyleRes int themeRes) {
        showErrorDialog(context, context.getString(titleRes), context.getString(messageRes), themeRes, null);
    }

    /**
     * Helper method used to show an error dialog
     * @param context the context to use
     * @param title the String value to use for the dialog's title
     * @param message the String value to use for the dialog's message
     * @param themeRes the Style resource to use for the dialog
     */
    public static void showErrorDialog(Context context, String title, String message, @StyleRes int themeRes) {
        showErrorDialog(context, title, message, themeRes, null);
    }

    /**
     * Helper method used to show an error dialog
     * @param context the Context to use
     * @param titleRes the String resource to use for the dialog's title
     * @param messageRes the String resource to use for the dialog's message
     * @param themeRes the Style resource to use for the dialog
     * @param delegate the delegate to be notified when the positive button is clicked
     */
    public static void showErrorDialog(Context context, @StringRes int titleRes, @StringRes int messageRes, @StyleRes int themeRes, Delegate delegate) {
        showErrorDialog(context, context.getString(titleRes), context.getString(messageRes), themeRes, delegate);
    }

    /**
     * Helper method used to show an error dialog
     * @param context the Context to use
     * @param title the String value to use for the dialog's title
     * @param message the String value to use for the dialog's message
     * @param themeRes the Style resource to use for the dialog
     * @param delegate the delegate to be notified when the positive button is clicked
     */
    public static void showErrorDialog(Context context, String title, String message, int themeRes, final Delegate delegate) {
        try {
            new AlertDialog.Builder(context, themeRes)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if(delegate != null){
                                delegate.onDialogClosed();
                            }
                        }
                    }).create().show();
        }catch (Exception e){
            Log.e(TAG, title + " " + message);
            Log.e(TAG, "An error occured : " + e.getMessage() );
        }

    }

    /**
     * Interface used to be notified when the ErrorDialog is closed
     */
    public interface Delegate{
        void onDialogClosed();
    }

}
