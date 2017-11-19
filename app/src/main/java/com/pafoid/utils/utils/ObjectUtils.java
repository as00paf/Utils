package com.pafoid.utils.utils;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

/**
 * Utility Class used to compare primitive objects
 * <p>
 * {@code ObjectUtils} does not have a constructor and all it's methods are static so
 * it does not needs to be instantiated. To use it, just call the public methods directly like this
 */
public class ObjectUtils {

    /**
     * Utility method to identify if the passed object is primitive or not
     *
     * @param obj the object to inspect
     * @return true or false
     */
    public static boolean isPrimitiveType(Object obj) {
        return (obj instanceof Boolean ||
                obj instanceof String ||
                obj instanceof CharSequence ||
                obj instanceof Long ||
                obj instanceof Integer ||
                obj instanceof Double ||
                obj instanceof Float ||
                obj instanceof Date ||
                obj instanceof Array ||
                obj instanceof List ||
                obj instanceof Byte);
    }
}
