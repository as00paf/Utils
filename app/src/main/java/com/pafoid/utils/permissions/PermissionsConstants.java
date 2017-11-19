package com.pafoid.utils.permissions;

import android.Manifest;
import android.os.Build;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants class used to get request codes for requesting permissions
 */
public class PermissionsConstants {

    public static final int MULTIPLE_PERMISSIONS = 255;

    public static Map<String, Integer> permissionsRequestCodes;

    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(Manifest.permission.READ_CALENDAR, 0);
        map.put(Manifest.permission.WRITE_CALENDAR, 1);

        map.put(Manifest.permission.CAMERA, 2);

        map.put(Manifest.permission.READ_CONTACTS, 3);
        map.put(Manifest.permission.WRITE_CONTACTS, 4);

        map.put(Manifest.permission.ACCESS_COARSE_LOCATION, 5);
        map.put(Manifest.permission.ACCESS_FINE_LOCATION, 6);

        map.put(Manifest.permission.RECORD_AUDIO, 7);

        map.put(Manifest.permission.READ_PHONE_STATE, 8);
        map.put(Manifest.permission.CALL_PHONE, 9);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            map.put(Manifest.permission.READ_CALL_LOG, 10);
            map.put(Manifest.permission.WRITE_CALL_LOG, 11);
        }
        map.put(Manifest.permission.ADD_VOICEMAIL, 12);
        map.put(Manifest.permission.USE_SIP, 13);
        map.put(Manifest.permission.PROCESS_OUTGOING_CALLS, 14);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            map.put(Manifest.permission.BODY_SENSORS, 15);
        }

        map.put(Manifest.permission.SEND_SMS, 16);
        map.put(Manifest.permission.RECEIVE_SMS, 17);
        map.put(Manifest.permission.READ_SMS, 18);
        map.put(Manifest.permission.RECEIVE_WAP_PUSH, 19);
        map.put(Manifest.permission.RECEIVE_SMS, 20);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            map.put(Manifest.permission.READ_EXTERNAL_STORAGE, 21);
        }
        map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, 22);

        permissionsRequestCodes = Collections.unmodifiableMap(map);
    }

    /**
     * Static method used to get the request code for a permission
     * @param permission the permission to request
     * @return an integer representing the request code used to grant a permission
     */
    public static int getRequestCodeForPermission(String permission) {
        return permissionsRequestCodes.get(permission);
    }

}
