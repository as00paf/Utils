package com.pafoid.utils.geo.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Interface used to represent Map circles
 */
public interface IMapItem {
    double getLatitude();
    void setLatitude(double latitude);

    double getLongitude();
    void setLongitude(double longitude);

    LatLng getCoordinates();
    void setCoordinates(LatLng coordinates);
}
