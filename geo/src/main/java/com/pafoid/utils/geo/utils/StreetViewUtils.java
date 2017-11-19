package com.pafoid.utils.geo.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pafoid.utils.permissions.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by as00p on 2017-09-18.
 */

public class StreetViewUtils {
    private static final String TAG = "StreetViewUtils";

    private static final String STREETVIEW_API_URL = "https://maps.googleapis.com/maps/api/streetview/metadata?location=%1$f,%2$f&key=%3$s";
    private static final String STREETVIEW_PANO_URL = "https://maps.googleapis.com/maps/api/streetview?pano=%1$s&size=%2$dx%3$s&key=%4$s";


    /**
     * Helper method used to show a StreetView in the map application to a specific latitude and longitude
     * @param context the Context to use to show the map application
     * @param latitude the latitude to show
     * @param longitude the longitude to show
     */
    public static void launchStreetView(Context context, double latitude, double longitude){
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    /**
     * Helper method used to show a StreetView in the map application to a specific latitude, longitude, bearing, zoom and tilt
     * @param context the Context to use to show the map application
     * @param latitude the latitude to show
     * @param longitude the longitude to show
     * @param bearing the bearing to show
     * @param zoom the zoom to show
     * @param tilt the tilt to show
     */
    public static void launchStreetView(Context context, double latitude, double longitude, int bearing, int zoom, int tilt){
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude + "," + longitude+"&cbp=0," + bearing + ",0," + zoom + "," + tilt);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    /**
     * Helper method used to show a StreetView in the map application to a specific panorama id
     * @param context the Context to use to show the map application
     * @param panoId the id of the panorama to show
     */
    public static void launchStreeViewWithPanoId(Context context, String panoId){
        Uri gmmIntentUri = Uri.parse("google.streetview:panoid=" + panoId);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    /**
     * Helper method used to verify if StreetView is available at that position
     * @param context the Context to use
     * @param apiKey the Google Api Key to use
     * @param latitude the latitude of the position to verify
     * @param longitude the longitude of the position to verify
     * @param delegate the delegate used to notify if StreetView is available or not
     */
    public static void isStreetViewAvailable(Context context, String apiKey, double latitude, double longitude, StreetViewDelegate delegate){
        if(PermissionManager.getInstance(context).isPermissionGranted(Manifest.permission.INTERNET)){
            String url = String.format(STREETVIEW_API_URL, latitude, longitude, apiKey);
            new StreetViewAvailabilityTask(url).execute(delegate);
        }else{
            Toast.makeText(context, "Internet permission not granted !", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to retrieve the URL of a StreetView panorama image
     * @param panoId the id of the panorama to retrieve
     * @param width the with of the image
     * @param height the height of the image
     * @param apiKey the Google Api Key to use
     * @return a String representing the URL of the StreetView panorama image
     */
    public static String getStreetViewPanoURL(String panoId, int width, int height, String apiKey){
        return String.format(STREETVIEW_PANO_URL, panoId, width, height, apiKey);
    }

    private static class StreetViewAvailabilityTask extends AsyncTask<StreetViewDelegate, Void, String> {

        private static final String TAG = "StreetViewAvailability";
        private String apiUrl;
        private StreetViewDelegate delegate;

        public StreetViewAvailabilityTask(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        @Override
        protected String doInBackground(StreetViewDelegate... args) {
            delegate = args[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    InputStream it = new BufferedInputStream(conn.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder data = new StringBuilder();
                    String chunks ;
                    while((chunks = buff.readLine()) != null)
                    {
                        data.append(chunks);
                    }

                    return data.toString();
                }
                else {
                    Log.d(TAG, "Error : " +  conn.getResponseMessage());
                    return "ZERO_RESULTS";
                }
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e.getMessage());
                return "ZERO_RESULTS";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String panoId = null;

            try {
                panoId = new JSONObject(result).getString("pano_id");
            } catch (JSONException e) {
                //e.printStackTrace();
            }

            delegate.onStreetViewAvailabilityReceieved(!result.toString().contains("ZERO_RESULTS"), panoId);
        }
    }

    /**
     * Interface used as a delegate to check the StreetView availability of a panorama
     */
    public interface StreetViewDelegate{
        void onStreetViewAvailabilityReceieved(boolean streetViewAvailable, String panoId);
        void onStreetViewAvailabilityError(String error);
    }
}
