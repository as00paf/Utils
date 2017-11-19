package com.pafoid.utils.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

/**
 * Created by as00p on 2017-03-31.
 */

public class SettingsUtils {

    public static final int RC_ENABLE_GPS = 1200;
    public static final int RC_ENABLE_BLUETOOTH = 1300;

    //Wifi
    public static void turnWifiOn(Context context){
        changeWifiState(context, true);
    }

    public static void turnWifiOff(Context context){
        changeWifiState(context, false);
    }

    public static void changeWifiState(Context context, boolean enabled){
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(enabled);
    }

    public static boolean isWifiEnabled(Context context){
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    //GPS
    public static void enableGPS(Activity activity){
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, RC_ENABLE_GPS);
    }

    public static boolean isGPSEnabled(Context context){
        LocationManager manager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Bluetooth
    @SuppressWarnings("MissingPermission")
    public static void turnBluetoothOn(Activity activity){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            activity.startActivityForResult(intentBtEnabled, RC_ENABLE_BLUETOOTH);
        }
    }
}
