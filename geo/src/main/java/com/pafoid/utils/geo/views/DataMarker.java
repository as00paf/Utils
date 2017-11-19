package com.pafoid.utils.geo.views;

import com.google.android.gms.maps.model.Marker;

/**
 * Extend this abstract class to add data to a {@link PicassoMarker}
 * @param <T> the class of the data type
 */
public abstract class DataMarker<T> extends PicassoMarker {

    private T data;

    public DataMarker(Marker marker) {
        super(marker);
    }

    public DataMarker(Marker marker, T data) {
        super(marker);
        this.data = data;
    }

    //Getters/Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
