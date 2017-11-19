package com.pafoid.utils.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A utility class that exposes time constants and static methods to manipulate time objects
 * <p>
 * {@code TimeUtils} does not have a constructor and only static methods and constants
 */
public class TimeUtils {

    public static final String TAG = "TimeUtils";

    public static final int MILLISECOND = 1;
    public static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int HALF_HOUR = 30 * MINUTE;
    public static final int QUARTER = 15 * MINUTE;
    public static final int HALF_DAY = 12 * HOUR;
    public static final int DAY = 24 * HOUR;
    public static final int WEEK = 7 * DAY;

    private static final ArrayList<Integer> QUARTERS = new ArrayList<Integer>(){{add(0); add(15); add(30); add(45);}};

    /**
     * Constant class used to define size units
     */
    public static class Unit {
        /**
         * Constant used to represent seconds
         */
        public static final int SECOND = 0;
        /**
         * Constant used to represent hours
         */
        public static final int MINUTE = 1;
        /**
         * Constant used to represent hours
         */
        public static final int HOUR = 2;
    }

    private static final ArrayList<Integer> UNITS = new ArrayList<Integer>() {
        {
            add(Unit.SECOND);
            add(Unit.MINUTE);
            add(Unit.HOUR);
        }
    };

    public static long toUTC(long timestamp) {
        Calendar cal = Calendar.getInstance();
        int offset = cal.getTimeZone().getOffset(timestamp);
        return timestamp + offset;
    }

    public static double millisToHours(long millis) {
        return millis/1000/60/60;
    }

    public static double millisToMinutes(long millis) {
        return millis/1000/60;
    }

    public static double millisToSeconds(long millis) {
        return millis/1000;
    }

    public static Date getCurrentTimeUTC() {
        return new Date(toUTC(System.currentTimeMillis()));
    }

    public static Date now() {
        return new Date();
    }

    public static Date tomorrow() {
        return new Date(new Date().getTime() + DAY);
    }

    public static Date yesterday() {
        return new Date(new Date().getTime() - DAY);
    }

    public static long convert(long value, int fromUnit, int toUnit){
        long result = 0;

        int fromIndex = UNITS.indexOf(fromUnit);//SECONDS = 0
        int toIndex = UNITS.indexOf(toUnit);//HOURS = 1
        if(fromIndex < 0 || toIndex < 0){
            throw new Error("Unit index not found");
        }

        int unitDelta = toIndex - fromIndex;//1 - 0 = 1
        long factor = (long) Math.pow(60, Math.abs(unitDelta));

        if(unitDelta > 0){//Seconds to Minutes, result should be smaller
            result = value / factor;
        }else if(unitDelta < 0){//Minutes to Seconds, result should be bigger
            result = factor * value;
        }else{
            result = value;
        }

        return result;
    }

    public static int getQuarterCountFromDuration(long duration) {
        return (int) duration / TimeUtils.QUARTER;
    }

    public static int getQuarterSectionFromStartTime(long startTime){
        int section = 0;

        int hour = getHoursFromTimeStamp(startTime);
        int minute = getMinutesFromTimeStamp(startTime);
        int quarter = minute > 0 ? minute - (minute % 15) : 0;
        int quarterIndex = QUARTERS.indexOf(quarter);

        section = 4 * hour + quarterIndex;

        return section;
    }

    public static int getHoursFromTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinutesFromTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));

        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecondsFromTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));

        return calendar.get(Calendar.SECOND);
    }

}
