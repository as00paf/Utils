package com.pafoid.utils.geo.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Interface used to represent Map circles
 */
public interface ICircle extends IMapItem{
    String getId();
    void setId(String id);

    float getRadius();
    void setRadius(float radius);
}
