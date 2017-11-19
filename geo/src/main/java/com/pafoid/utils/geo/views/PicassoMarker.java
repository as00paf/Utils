package com.pafoid.utils.geo.views;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Helper class used to show a Marker on Map with a Picasso loaded image
 */
public class PicassoMarker implements Target {

    private static final String TAG = "PicassoMarker";

    private Marker marker;

    public PicassoMarker(Marker marker)
    {
        this.marker = marker;
    }

    public Marker getMarker()
    {
        return this.marker;
    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof PicassoMarker){
            Marker marker = ((PicassoMarker) o).marker;

            return this.marker.equals(marker);
        }

        return false;
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from){
        try {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            this.marker.setIcon(descriptor);
        }catch (Exception e){
            Log.e(TAG, "Bitmap failed to load. Error : " + e.getMessage());
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable)
    {
        Log.d(TAG, "bitmap failed");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable){

    }
}
