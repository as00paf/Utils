package com.pafoid.utils.utils;

import android.content.Context;

import com.pafoid.utils.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Utility Class used to compare and convert sizes like KB, MB, GB etc
 */
public class SizeUtils {

    /**
     * Constant class used to define size units
     */
    public static class Unit{
        /**
         * Constant used to represent bytes
         */
        public static final int BYTES = 0;
        /**
         * Constant used to represent kilobytes
         */
        public static final int KILOBYTES = 1;
        /**
         * Constant used to represent megabytes
         */
        public static final int MEGABYTES = 2;
        /**
         * Constant used to represent gigabytes
         */
        public static final int GIGABYTES = 3;
        /**
         * Constant used to represent terrabytes
         */
        public static final int TERABYTES = 4;
    }

    /**
     * Constant used for ordering the size units
     */
    private static final ArrayList<Integer> SIZES = new ArrayList<Integer>() {
        {
            add(Unit.BYTES);
            add(Unit.KILOBYTES);
            add(Unit.MEGABYTES);
            add(Unit.GIGABYTES);
            add(Unit.TERABYTES);
        }
    };

    /**
     * Utility method used to convert a size from a unit to another
     * @param value the value to convert
     * @param fromUnit the starting unit
     * @param toUnit the unit to convert to
     * @return the converted value to the specified unit
     */
    public static float convert(float value, int fromUnit, int toUnit){
        float result;

        int fromIndex = SIZES.indexOf(fromUnit);//BYTES = 0
        int toIndex = SIZES.indexOf(toUnit);//KILOBYTES = 1
        if(fromIndex < 0 || toIndex < 0){
            throw new Error("Size index not found");
        }

        int unitDelta = toIndex - fromIndex;//1 - 0 = 1
        float factor = (float) Math.pow(1024, Math.abs(unitDelta));

        if(unitDelta > 0){//Bytes to Kilobytes, result should be smaller
            result = value / factor;
        }else if(unitDelta < 0){//Kilobytes to Bytes, result should be bigger
            result = factor * value;
        }else{
            result = value;
        }

        return result;
    }

    public static final int BASE = 1024;

    /**
     * Utility method used to automatically convert a size value to the best readable String value
     * @param context the Context to use
     * @param value the size to convert
     * @return a String value representing the size
     */
    public static String autoConvert(Context context, BigDecimal value){
        String result = null;

        String unit = null;
        int range = getValueRange(value);
        BigDecimal resultValue = new BigDecimal(0);

        switch (range){
            case Unit.BYTES:
                unit = context.getString(R.string.bytes);
                resultValue = value;
                break;
            case Unit.KILOBYTES:
                unit = context.getString(R.string.kilobytes);
                resultValue = value.divide(new BigDecimal(BASE));
                break;
            case Unit.MEGABYTES:
                unit = context.getString(R.string.megabytes);
                resultValue = value.divide(new BigDecimal(Math.pow(BASE, 2)));
                break;
            case Unit.GIGABYTES:
                unit = context.getString(R.string.gigabytes);
                resultValue = value.divide(new BigDecimal(Math.pow(BASE, 3)));
                break;
            case Unit.TERABYTES:
                unit = context.getString(R.string.terabytes);
                resultValue = value.divide(new BigDecimal(Math.pow(BASE, 4)));
                break;
        }

        //TODO : limit number of decimals after
        DecimalFormat decFormat = new DecimalFormat("##.##");
        resultValue = new BigDecimal(decFormat.format(resultValue));
        result = resultValue + " " + unit;

        return result;
    }

    /**
     * Utility method used to automatically convert a size value to the best readable String value
     * @param context the Context to use
     * @param value the size to convert
     * @return a String value representing the size
     */
    public static String autoConvert(Context context, int value){
        return autoConvert(context, new BigDecimal(value));
    }

    /**
     * Utility method used to automatically convert a size value to the best readable String value
     * @param context the Context to use
     * @param value the size to convert
     * @return a String value representing the size
     */
    public static String autoConvert(Context context, double value){
        return autoConvert(context, new BigDecimal(value));
    }

    /**
     * Utility method used to automatically convert a size value to the best readable String value
     * @param context the Context to use
     * @param value the size to convert
     * @return a String value representing the size
     */
    public static String autoConvert(Context context, float value){
        return autoConvert(context, new BigDecimal(value));
    }

    /**
     * Utility method used to automatically convert a size value to the best readable String value
     * @param context the Context to use
     * @param value the size to convert
     * @return a String value representing the size
     */
    public static String autoConvert(Context context, long value){
        return autoConvert(context, new BigDecimal(value));
    }

    private static int getValueRange(BigDecimal value){
        if (value.compareTo(new BigDecimal(1024)) < 0)
            return Unit.BYTES;
        else if (value.compareTo(new BigDecimal(Math.pow(BASE, 2))) < 0)
            return Unit.KILOBYTES;
        else if (value.compareTo(new BigDecimal(Math.pow(BASE, 3))) < 0)
            return Unit.MEGABYTES;
        else if (value.compareTo(new BigDecimal(Math.pow(BASE, 4))) < 0)
            return Unit.GIGABYTES;
        return Unit.TERABYTES;
    }
}
