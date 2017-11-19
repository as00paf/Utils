package com.pafoid.utils.geo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.pafoid.utils.geo.utils.AppConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class used to represent a path drawn on a GoogleMap
 */
public abstract class MapPath implements Serializable{

    @SerializedName(AppConstants.MIDDLE_COORDINATE)
    private LatLng middleCoordinate;

    @SerializedName(AppConstants.COORDINATES)
    private List<List<IMapItem>> coordinates;

    //Public Methods
    public List<List<LatLng>> getLatLngCoordinates() {
        List<List<LatLng>> coords = new ArrayList<List<LatLng>>();

        for (List<IMapItem> coordinateList : coordinates) {
            List<LatLng> list = new ArrayList<LatLng>();

            for (IMapItem coordinate : coordinateList) {
                list.add(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
            }

            coords.add(list);
        }

        return coords;
    }

    //Getters/Setters
    public LatLng getMiddleCoordinate() {
        return middleCoordinate;
    }

    public void setMiddleCoordinate(LatLng middleCoordinate) {
        this.middleCoordinate = middleCoordinate;
    }

    public List<List<IMapItem>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<IMapItem>> coordinates) {
        this.coordinates = coordinates;
    }
}
