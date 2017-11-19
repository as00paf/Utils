package com.pafoid.utils.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class used to deal with Dates
 */
public class DateUtils {

    public static final String TAG = "DateUtils";

    /**
     * Sub-Class used to represent Date Formats
     */
    public static class Format {
        public static final String FULL_MS = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final String FULL_MS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public static final String FULL_S_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        public static final String FULL_S_Z_2 = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
        public static final String DATE_MINUTES = "yyyy-MM-dd'T'HH:mm";

        public static final String DAY_YEAR = "d MMMM yyyy";
        public static final String DAY = "d MMMM";

        public static final String TIME = "HH'h'mm";

        //Groups
        public static final String[] TIMES = {TIME};
        public static final String[] DATES = {FULL_MS, FULL_MS_Z, FULL_S_Z, FULL_S_Z_2, DATE_MINUTES};
        public static final String[] DAYS = {DAY_YEAR, DAY};
    }

    //Dates
    /**
     * Helper method used to convert a String to a Date Object
     * @param dateString the String to convert
     * @return a Date Object
     */
    public static Date parseDate(String dateString){
        return parseDate(dateString, null);
    }

    /**
     * Helper method used to convert a String to a UTC Date Object
     * @param dateString the String to convert
     * @return a Date Object in the UTC format
     */
    public static Date parseDateUTC(String dateString){
        return parseDate(dateString, "UTC");
    }

    /**
     * Helper method used to convert a String to a Date Object for a specific TimeZone
     * @param dateString the String to convert
     * @param timeZone the TimeZone to use
     * @return a Date Object converted to UTC format
     */
    public static Date parseDate(String dateString, String timeZone){
        Date result = null;
        Exception exception = null;

        SimpleDateFormat formatter;
        for (String format : Format.DATES) {
            formatter = new SimpleDateFormat(format, Locale.getDefault());
            if(timeZone != null) formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            try{
                result = formatter.parse(dateString);
            }catch (Exception e){
                exception = e;
            }
        }

        if(result == null && exception != null){
            Log.e(TAG, "Date " + dateString + " cannot be parsed. " + exception.getMessage());
        }

        return result;
    }

    public static boolean isSameDay(Date date1, Date date2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    //Time
    /**
     * Helper method used to Convert a time String to a Date Object
     * @param dateString the String to convert
     * @return a Date Object
     */
    public static Date parseTime(String dateString){
        return parseDate(dateString, null);
    }


}
