package com.pafoid.utils.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

/**
 * A utility class that exposes bluetooth related helper methods
 * <p>
 * {@code BluetoothUtils} does not have a constructor and only static methods and constants
 */
public class BluetoothUtils {

    public static final String TAG = "BluetoothUtils";

    //TODO : replace with constants
    private static int[] getQueriableBluetoothProfiles() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 16, 17};
    }

    public static ArrayList<Integer> getConnectedProfiles() {
        ArrayList<Integer> connectedProfiles = new ArrayList<>();
        int[] queriableProfiles = getQueriableBluetoothProfiles();
        int connectionState;

        for (int queriableProfile : queriableProfiles) {
            //noinspection MissingPermission
            connectionState = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(queriableProfile);
            if (connectionState == STATE_CONNECTED || connectionState == STATE_DISCONNECTING)
                connectedProfiles.add(queriableProfile);
        }

        return connectedProfiles;
    }

    public static int getBluetoothConnectedDevicesCount(Context context) {
        int connectedCount = 0;
        ArrayList<Integer> bluetoothProfiles = getConnectedProfiles();

        for (int bluetoothProfile : bluetoothProfiles) {
            try {
                final long MAX_WAIT_TIME = 40 * 1000;
                final long WAIT_TIME_INCREMENT = 1 * 1000;
                BluetoothProfileServiceListener bluetoothProfileServiceListener = new BluetoothProfileServiceListener();

                synchronized (bluetoothProfileServiceListener) {
                    BluetoothAdapter.getDefaultAdapter().getProfileProxy(context.getApplicationContext(), bluetoothProfileServiceListener, bluetoothProfile);

                    long waitTime = 0;
                    while ((!bluetoothProfileServiceListener.isDone()) && (waitTime < MAX_WAIT_TIME)) {
                        bluetoothProfileServiceListener.wait(WAIT_TIME_INCREMENT);
                        waitTime += WAIT_TIME_INCREMENT;
                    }

                    connectedCount += bluetoothProfileServiceListener.connectionsCount;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
        }

        return connectedCount;
    }

    public static class BluetoothProfileServiceListener implements BluetoothProfile.ServiceListener {

        public static final String TAG = "BtProfileSrvListener";

        public int connectionsCount = 0;
        private boolean doneFlag = false;

        private final int[] profileStates = {BluetoothProfile.STATE_CONNECTED,BluetoothProfile.STATE_DISCONNECTING};

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.d(TAG,"onServiceConnected");
            synchronized (this) {
                //noinspection MissingPermission
                connectionsCount = proxy.getDevicesMatchingConnectionStates(profileStates).size();
                doneFlag = true;
                notifyAll();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.d(TAG,"onServiceDisconnected");
        }

        public boolean isDone() {
            return doneFlag;
        }
    }

}
