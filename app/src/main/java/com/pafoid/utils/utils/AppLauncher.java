package com.pafoid.utils.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

/**
 * Helper class used to launch activities or applications
 * This class does not have a constructor and all of the methods are static
 */
public class AppLauncher{
    private static final String TAG = "AppLauncher";

    /**
     * Helper method used to show a web page in the default browser
     * @param context the Context used to start the Activity
     * @param webpageUri the Uri of the web page to display
     */
	public static void launchWebPage(Context context, Uri webpageUri){
		Intent intent = new Intent(Intent.ACTION_VIEW, webpageUri);
        context.startActivity(intent);
	}

    /**
     * Helper method used to show a web page in a specific application
     * @param context the Context used to start the Activity
     * @param appUri the Uri of the app to use
     * @param webUri the Uri of the web page to display
     */
	public static void launchAppWithAppAndWebURIs(Context context, Uri appUri, Uri webUri){
		try{
			Intent appIntent = new Intent(Intent.ACTION_VIEW, appUri);
			context.startActivity(appIntent);
		}
		catch (ActivityNotFoundException activityNotFoundException){
			Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
			context.startActivity(webIntent);
		}
	}

    /**
     * Helper method used to send an email with the default email application
     * @param context the Context to use to show the email application
     * @param toEmail the email address of the recipient
     * @param subject the subject to use in the email
     * @param bodyText the body of the email
     */
	public static void launchMail(Context context, String toEmail, String subject, String bodyText){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		
		emailIntent.setType("plain/text");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
		
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

    /**
     * Helper method used to dial a phone number in the default phone application
     * @param context the Context to use to start the application
     * @param phoneNumber the phone number to dial
     */
	public static void launchPhone(Context context, String phoneNumber){
		String uriString = "tel:" + phoneNumber;
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
	}

    /**
     * Helper method used to dial a phone number with an extension in the default phone application
     * @param context the Context to use to start the application
     * @param phoneNumber the phone number to dial
     * @param extension the extension to dial
     */
	public static void launchPhone(Context context, String phoneNumber, String extension){
        if(extension == null || TextUtils.isEmpty(extension)) throw new Error("Extension cannot be null!");

		String uriString = "tel:" + phoneNumber + PhoneNumberUtils.PAUSE + extension;
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
	}

    /**
     * Helper method used to strip a URL from its protocol
     * @param websiteURL the URL to use
     * @return the same URL but without the http(s)://www part
     */
    public static String stripProtocolFromURL(String websiteURL) {
        return websiteURL.replace("http://wwww.", "").replace("http://", "").replace("https://www.", "").replace("https://", "");
    }

    //Social
    //YouTube
    /**
     * Helper method used to open the YouTube app to a specific user page
     * @param context the context to use to start the application
     * @param channelName the name of the user to show
     */
    public static void launchYoutubeUser(Context context, String channelName){
        Uri webUri = Uri.parse("http://www.youtube.com/user/" + channelName);

        AppLauncher.launchAppWithAppAndWebURIs(context, webUri, webUri);
    }

    //YouTube
    /**
     * Helper method used to open the YouTube app to a specific channel
     * @param context the context to use to start the application
     * @param channelName the name of the channel to show
     */
    public static void launchYoutubeChannel(Context context, String channelName){
        Uri webUri = Uri.parse("http://www.youtube.com/channel/" + channelName);

        AppLauncher.launchAppWithAppAndWebURIs(context, webUri, webUri);
    }

    //Facebook
    /**
     * Helper method used to get the Facebook URL to use
     * @param context the Context to use to get the Facebook URL
     * @return the Facebook URL
     */
    public static String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + AppConstants.FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return AppConstants.FACEBOOK_URL; //normal web url
        }
    }

    /**
     * Helper method used to launch Facebook either in the application or on the web to a certain page
     * @param context the Context to use to show the Facebook page
     * @param pageID the id of the page to display
     */
    public static void launchFacebookPageWithPageID(Context context, String pageID){
        Uri webUri = Uri.parse(getFacebookPageURL(context) + pageID);

        Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
        context.startActivity(webIntent);
    }

	//Twitter
    /**
     * Helper method used to show a Twitter profile in the app or on the web
     * @param context the Context to use to show the Twitter profile
     * @param username the user name of the profile to show
     */
    public static void launchTwitterPageWithUsername(Context context, String username){
        Uri appUri = Uri.parse("twitter://user?screen_name=" + username);
        Uri webUri = Uri.parse("https://twitter.com/" + username);

        AppLauncher.launchAppWithAppAndWebURIs(context, appUri, webUri);
    }

    //Map
    /**
     * Helper method used to show an address in the Map application
     * @param context the Context to use to show the address in the Map application
     * @param address the address to show
     */
	public static void launchMap(Context context, String address){
		String uriString = "geo:0,0?q=" + address;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		context.startActivity(intent);
	}

    /**
     * Helper method used to show an itinerary in the map application
     * @param context the Context to use to show the map application
     * @param toLat the destination latitude
     * @param toLng the destination longitude
     * @param fromLat the original latitude
     * @param fromLng the original longitude
     */
	public static void launchMapWithItinerary(Context context, double toLat, double toLng, double fromLat, double fromLng){
		String urlString = "http://maps.google.com/maps?";

		urlString += "saddr=" + fromLat + "," + fromLng + "&";
		urlString += "daddr=" + toLat + "," + toLng;

		Uri uri = Uri.parse(urlString);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}

    /**
     * Helper method used to show an itinerary in the map application starting at the user's current position
     * @param context the Context to use to show the map application
     * @param toLat the destination latitude
     * @param toLng the destination longitude
     */
    public static void launchMapWithItinerary(Context context, double toLat, double toLng) {
        String urlString = "http://maps.google.com/maps?";
        urlString = urlString + "daddr=" + toLat + "," + toLng;
        Uri uri = Uri.parse(urlString);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        context.startActivity(intent);
    }

    //Google Calendar
    /**
     * Helper method used to add an event to Google Calendar
     * @param context the Context to use
     * @param startDate the start date of the event
     * @param endDate the end date of the event
     * @param title the title of the event
     * @param description the description of the event
     * @param address the address of the event
     */
    public static void addEventToCalendar(Context context, Date startDate, Date endDate, String title, String description, String address) {
        Log.d(TAG, "Adding event to calendar");

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime());
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, address);

        context.startActivity(intent);
    }
}
