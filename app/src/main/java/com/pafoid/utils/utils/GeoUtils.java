package com.pafoid.utils.utils;

import android.location.Location;

/**
 * A utility class that provides information about the device's location
 * <p>
 * {@code GeoUtils} does not have a constructor and all it's methods are static so
 * it does not needs to be instantiated.
 */
public class GeoUtils {

    private static final int TWO_MINUTES = TimeUtils.MINUTE * 2;

    /**
     * Helper method that compares {@link Location} objects and indicates if the first one is better than the second one
     * @param location the first {@link Location} object to compare
     * @param currentBestLocation the second {@link Location} object to compare
     * @return true or false
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Utility method used to compare location providers
     * @param provider1 first Location Provider to compare
     * @param provider2 second Location Provider to compare
     * @return true if providers are the same, false if not
     */
    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Helper method used to calculate the distance between two pairs of latitude and longitude in meters
     * @param latitude1 the first latitude
     * @param longitude1 the first longitude
     * @param latitude2 the second latitude
     * @param longitude2 the second longitude
     * @return the distance between the points in meters
     */
    public static float distanceBetweenCoordinates(final Double latitude1, final  Double longitude1,final Double latitude2,final Double longitude2){
        float result;
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLng = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;

        result = (float) (dist * meterConversion);

        return result;
    }

    /**
     * Helper method used to calculate the distance between two locations
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the distance in meters between both locations
     */
    public static float distanceBetweenLocations(Location loc1, Location loc2){
        return distanceBetweenCoordinates(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
    }
}
