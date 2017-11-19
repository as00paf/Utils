package com.pafoid.utils.geo.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pafoid.utils.geo.models.IMapItem;
import com.pafoid.utils.utils.GeoUtils;

import java.util.Comparator;

/**
 * Comparator class used to order items by distance to a location
 */
public class DistanceComparator  implements Comparator<IMapItem> {

    private static final String TAG = "DistanceComparator";

    private Location location;

    public DistanceComparator(Location currentLocation) {
        this.location = currentLocation;
    }

    @Override
    public int compare(IMapItem mapItem, IMapItem mapItem1) {
        if(location == null) {
            Log.e(TAG, "Unable to retrieve location");
            return 0;
        }

        float d1 = GeoUtils.distanceBetweenCoordinates(location.getLatitude(), location.getLongitude(), mapItem.getLatitude(), mapItem.getLongitude());
        float d2 = GeoUtils.distanceBetweenCoordinates(location.getLatitude(), location.getLongitude(), mapItem1.getLatitude(), mapItem1.getLongitude());

        if (d1 > d2) {
            return 1;
        }
        else if (d1 <  d2) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
