package com.pafoid.utils.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class DeviceUtils {

    //TODO : update with DeviceUtils from Interact project
    private static final String TAG = "DeviceUtils";

    public static void deviceDump(Context context) {
        Log.d(TAG, "deviceDump()");
        Log.d(TAG, "Device is " + getDevice());
        Log.d(TAG, "Device's serial number is " + getSerial());
        Log.d(TAG, "Device's brand is " + getBrand());
        Log.d(TAG, "Device's hardware is " + getHardware());
        Log.d(TAG, "Device's id is " + getId());
        Log.d(TAG, "Device's manufacturer is " + getManufacturer());
        Log.d(TAG, "Device's model is " + getModel());
        Log.d(TAG, "Device's type is " + getType());
        Log.d(TAG, "Device's product is " + getProduct());
        Log.d(TAG, "Device's IMEI " + getIMEI(context));
        Log.d(TAG, "App OS is Android " + getOSVersionName() + " SDK " + getSDKVersion() + " build number " + getOSBuildNumber() );
        Log.d(TAG, getInstalledApplications(context).size() + " apps are installed" );
        Log.d(TAG, "Device's total internal memory size is " + getTotalInternalMemorySize() );
        Log.d(TAG, "Device's total external memory size is " + getTotalExternalMemorySize() );
        Log.d(TAG, "Device's available internal memory size is " + getAvailableInternalMemorySize() );
        Log.d(TAG, "Device's available external memory size is " + getAvailableInternalMemorySize() );
        Log.d(TAG, "Device's total RAM size is " + getTotalRamSize(context) );
        Log.d(TAG, "Device's screen resolution is " + getScreenResolution(context) );
        Log.d(TAG, "Device's locale is " + getLocale());
        Log.d(TAG, "Device's time zone is " + getTimeZone());
        Log.d(TAG, "Device's wifi MAC adress is " + getWifiMacAddress(context));
        Log.d(TAG, "Device's mobile MAC adress is " + getMobileMacAddress(context));
        Log.d(TAG, "Device's mobile IP adress is " + getMobileIPAddress(context));
        Log.d(TAG, "Device's hostname is " + getHostName());
        Log.d(TAG, "Device's bluetooth MAC address is " + getBluetoothMacAddress(context));
        Log.d(TAG, "Device's CPU type is " + getCpuType());
        Log.d(TAG, "Device's CPU max frequency is " + getMaxCPUFreqMHz() + " mHz");
        //To test
        Log.d(TAG, "Device's CPU has " + getNumberOfCores() + " cores");
    }

    //Apps
    public static ArrayList<String> getInstalledPackages(Context context){
        ArrayList<String> results = new ArrayList<>();


        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(0);
        Iterator<PackageInfo> iterator = packages.iterator();
        while(iterator.hasNext()){
            results.add(iterator.next().applicationInfo.packageName);
        }

        return results;
    }

    public static ArrayList<String> getInstalledApplications(Context context){
        ArrayList<String> results = new ArrayList<>();


        PackageManager manager = context.getPackageManager();
        List<ApplicationInfo> packages = manager.getInstalledApplications(0);
        Iterator<ApplicationInfo> iterator = packages.iterator();
        while(iterator.hasNext()){
            results.add(iterator.next().packageName);
        }

        return results;
    }

    //OS
    public static String getOSBuildNumber(){
        return Build.DISPLAY;
    }

    public static String getOSVersionName(){
        return Build.VERSION.RELEASE;
    }

    public static int getSDKVersion(){
        return Build.VERSION.SDK_INT;
    }

    //Device
    public static String getSerial(){
        return Build.SERIAL;
    }

    public static String getBrand(){
        return Build.BRAND;
    }

    public static String getDevice(){
        return Build.DEVICE;
    }

    public static String getHardware(){
        return Build.HARDWARE;
    }

    public static String getId(){
        return Build.ID;
    }

    public static String getManufacturer(){
        return Build.MANUFACTURER;
    }

    public static String getModel(){
        return Build.MODEL;
    }

    public static String getType(){
        return Build.TYPE;
    }

    public static String getProduct(){
        return Build.PRODUCT;
    }

    public static String getIMEI(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED){
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }else{
            return "Error : Permission READ_PHONE_STATE is not granted";
        }
    }

    //Storage
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "Error";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "Error";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String getTotalRamSize(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            return memInfo.totalMem/1024/1024 + " MB";
        }else{
            String str1 = "/proc/meminfo";
            String str2;
            String[] arrayOfString;
            long initial_memory;
            try {
                FileReader localFileReader = new FileReader(str1);
                BufferedReader localBufferedReader = new BufferedReader(    localFileReader, 8192);
                str2 = localBufferedReader.readLine();//meminfo
                arrayOfString = str2.split("\\s+");
                for (String num : arrayOfString) {
                    Log.i(str2, num + "\t");
                }
                //total Memory
                initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
                localBufferedReader.close();
                return initial_memory + " MB";
            }
            catch (IOException e)
            {
                return formatSize(-1);
            }
        }
    }

    public static String getScreenResolutionString(Context context){
        Point resolution = getScreenResolution(context);

        return resolution.x + "x" + resolution.y;
    }

    public static Point getScreenResolution(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display d = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }

        Point point = new Point(widthPixels,heightPixels);
        return point;
    }

    public static double getScreenSize(Context context){
        Point resolution = getScreenResolution(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        float a = (float) resolution.x / metrics.xdpi;
        float b = (float) resolution.y / metrics.ydpi;
        float c = (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

        return Math.round(c * 100) / 100f;
    }

    public static double getScreenDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        float a = metrics.xdpi;
        float b = metrics.ydpi;
        int result = (int) (a + b)/2;
        return result;
    }

    public static String getLocale(){
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getTimeZone(){
        return TimeZone.getDefault().getDisplayName();
    }

    public static String getWifiMacAddress(Context context){
            return getInterfaceMacAddress(context, "wlan0");
    }

    public static String getMobileIPAddress(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        int netType = info.getType();

        if (netType == ConnectivityManager.TYPE_WIFI){
            return "";
        }

        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getMobileMacAddress(Context context){
        return getInterfaceMacAddress(context, "eth0");
    }

    public static String getInterfaceMacAddress(Context context, String interfaceName){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public static String getHostName() {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return "Error";
        }
    }

    public static String getBluetoothMacAddress(Context context){
        return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    }

    public static String getCpuType(){
        return System.getProperty("os.arch");
    }

    public static int getMaxCPUFreqMHz() {

        int maxFreq = -1;
        try {

            RandomAccessFile reader = new RandomAccessFile( "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state", "r" );

            boolean done = false;
            while ( ! done ) {
                String line = reader.readLine();
                if ( null == line ) {
                    break;
                }
                String[] splits = line.split( "\\s+" );
                assert ( splits.length == 2 );
                int timeInState = Integer.parseInt( splits[1] );
                if ( timeInState > 0 ) {
                    int freq = Integer.parseInt( splits[0] ) / 1000;
                    if ( freq > maxFreq ) {
                        maxFreq = freq;
                    }
                }
            }

        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

        return maxFreq;
    }

    public static int getNumberOfCores() {
        if(Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        }
        else {
            return getNumCoresOldPhones();
        }
    }

    private static int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            Log.d(TAG, "CPU Count: "+files.length);
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Print exception
            Log.d(TAG, "CPU Count: Failed.");
            e.printStackTrace();
            //Default to return 1 core
            return 1;
        }
    }

    public static boolean isScreenLarge(Resources res) {
        final int screenSize = res.getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if(!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = c.getResources().getConfiguration().orientation;
            int resourceId;
            if (isScreenLarge(c.getResources())){
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            }  else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
