package com.pafoid.utils.geo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pafoid.utils.geo.R;
import com.pafoid.utils.geo.models.ICircle;
import com.pafoid.utils.geo.views.DataMarker;
import com.pafoid.utils.geo.views.PicassoMarker;
import com.pafoid.utils.utils.BitmapUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Utils class used to deal with Maps and MapItems
 */
public class MapItemsUtils {

    private static final String TAG = "MapUtils";

    public static final int DEFAULT_ZOOM_PADDING = 300;
    public static final int DEFAULT_POSITION_PADDING = 100;


    //Objects
    private GoogleMap googleMap;
    private Context context;
    private ArrayList<Circle> circles;
    private ArrayList<Polygon> polygons;
    private ArrayList<DataMarker> dataMarkers;
    private ArrayList<PicassoMarker> markers = new ArrayList<>();

    public MapItemsUtils(Context context){
        this.context = context;
    }

    //Drawing Methods
    public void clearDataMarkers(){
        this.dataMarkers.clear();
    }

    public void clearMarkers(){
        this.markers.clear();
    }

    public void clearCircles(){
        this.circles.clear();
    }

    public void clearPolygons(){
        this.polygons.clear();
    }

    public void clearAll(){
        clearDataMarkers();
        clearMarkers();
        clearCircles();
        clearPolygons();
    }

    public void createMarker(Context context, LatLng position){
        if(googleMap != null){
            Log.d(TAG, "Adding marker @ " + position.latitude + "x" + position.longitude);
            PicassoMarker marker = drawMarker(context, position);
            this.markers.add(marker);
        }else{
            Log.d(TAG, "Map is null");
        }
    }

    private PicassoMarker drawMarker(Context context, LatLng position){
        MarkerOptions myMarkerOptions = new MarkerOptions().position(position);

        int drawableRes = R.drawable.ic_place_black_24dp;
        Bitmap pinBitmap = BitmapUtils.drawableToBitmap(ContextCompat.getDrawable(context, drawableRes));
        Bitmap tintedBitmap = BitmapUtils.changeImageColor(pinBitmap, ContextCompat.getColor(context, R.color.colorPrimary));

        myMarkerOptions.flat(true);

        myMarkerOptions.anchor(0.5f, 0.5f);

        Marker myMarker = googleMap.addMarker(myMarkerOptions);

        PicassoMarker marker = new PicassoMarker(myMarker);

        myMarker.setIcon(BitmapDescriptorFactory.fromBitmap(tintedBitmap));

        return marker;
    }

    //Circles
    public void createCircleWithData(ICircle iCircle){
        if(googleMap != null){
            Circle circle = drawCircle(iCircle);

            if(circles == null) circles = new ArrayList<Circle>();
            this.circles.add(circle);
        }else{
            Log.d(TAG, "Cannot draw Circle on Map because Map is null");
        }
    }

    private Circle drawCircle(ICircle iCircle){
        CircleOptions circleOptions = new CircleOptions().center(iCircle.getCoordinates())
                .fillColor(R.color.blacktransparentfilter)
                .strokeColor(R.color.colorPrimary)
                .radius(iCircle.getRadius());

        Circle circle = googleMap.addCircle(circleOptions);

        return circle;
    }

    //Camera
    public void zoomOnMarkers(){
        zoomOnMarkers(DEFAULT_ZOOM_PADDING);
    }

    public void zoomOnMarkers(int padding){
        if (circles != null && circles.size() > 0){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Circle circle: this.circles) {
                builder.include(circle.getCenter());
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            if(this.googleMap != null){
                try{
                    this.googleMap.animateCamera(cu);
                }catch (Exception e){
                    e.printStackTrace();
                    try{
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                        this.googleMap.animateCamera(cu);
                    }catch (Exception ex){
                        Log.e(TAG, "2 things went wrong : " + e.getMessage() + "\n" + ex.getMessage());
                    }
                }
            }
        }
    }

    public void zoomOnMarkersAndPosition(LatLng lastKnownPosition){
        if (circles != null && circles.size() > 0){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Circle circle : this.circles){
                builder.include(circle.getCenter());
            }

            if(lastKnownPosition != null){
                builder.include(lastKnownPosition);
            }

            LatLngBounds bounds = builder.build();
            int padding = DEFAULT_POSITION_PADDING; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            this.googleMap.animateCamera(cu);
        }
    }

    public void zoomOnPosition(LatLng position){
        CameraUpdate cu = CameraUpdateFactory.newLatLng(position);
        this.googleMap.animateCamera(cu);
    }

    public void zoomOnPositions(int padding, LatLng... positions){
        if (positions != null && positions.length > 0){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (LatLng position: positions) {
                builder.include(position);
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            if(this.googleMap != null){
                try{
                    this.googleMap.animateCamera(cu);
                }catch (Exception e){
                    e.printStackTrace();
                    try{
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                        this.googleMap.animateCamera(cu);
                    }catch (Exception ex){
                        Log.e(TAG, "2 things went wrong : " + e.getMessage() + "\n" + ex.getMessage());
                    }
                }
            }
        }
    }

    //Path
    public static void drawPath(Context context, GoogleMap googleMap, ArrayList<LatLng> points) {
        drawPath(context, googleMap, points, R.color.colorPrimaryDark, 15);
    }

    public static void drawPath(Context context, GoogleMap googleMap, ArrayList<LatLng> points, @ColorRes int pathColor) {
        drawPath(context, googleMap, points, pathColor, 15);
    }

    public static void drawPath(Context context, GoogleMap googleMap, ArrayList<LatLng> points, @ColorRes int pathColor, int pathWidth) {
        Log.d(TAG, "will draw " + points.size() + " points");

        PolylineOptions mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(ContextCompat.getColor(context, pathColor));
        mPolylineOptions.width(pathWidth);
        mPolylineOptions.addAll(points);

        googleMap.addPolyline(mPolylineOptions);
    }


    //Getters/Setters
    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.clear();
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public ArrayList<DataMarker> getDataMarkers() {
        return dataMarkers;
    }

    public ArrayList<PicassoMarker> getMarkers() {
        return markers;
    }

    public Object getDataFromMarker(Marker marker){
        Object data = null;

        if (this.dataMarkers != null) {
            for (DataMarker dataMarker: this.dataMarkers){
                if (dataMarker.getMarker().equals(marker)){
                    data = dataMarker.getData();
                }
            }
        }

        return data;
    }
}
