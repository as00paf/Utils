
package com.pafoid.utils.geo.models.vo;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeocodedWaypoint implements Serializable
{

    @SerializedName("geocoder_status")
    @Expose
    private String geocoderStatus;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("partial_match")
    @Expose
    private Boolean partialMatch;
    private final static long serialVersionUID = -4691742005992917777L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GeocodedWaypoint() {
    }

    /**
     * 
     * @param geocoderStatus
     * @param placeId
     * @param types
     * @param partialMatch
     */
    public GeocodedWaypoint(String geocoderStatus, String placeId, List<String> types, Boolean partialMatch) {
        super();
        this.geocoderStatus = geocoderStatus;
        this.placeId = placeId;
        this.types = types;
        this.partialMatch = partialMatch;
    }

    public String getGeocoderStatus() {
        return geocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        this.geocoderStatus = geocoderStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Boolean getPartialMatch() {
        return partialMatch;
    }

    public void setPartialMatch(Boolean partialMatch) {
        this.partialMatch = partialMatch;
    }

}
