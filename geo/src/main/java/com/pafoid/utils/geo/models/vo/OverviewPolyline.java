
package com.pafoid.utils.geo.models.vo;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OverviewPolyline implements Serializable
{

    @SerializedName("points")
    @Expose
    private String points;
    private final static long serialVersionUID = 1604901123071861980L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OverviewPolyline() {
    }

    /**
     * 
     * @param points
     */
    public OverviewPolyline(String points) {
        super();
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
