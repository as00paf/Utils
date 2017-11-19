package com.pafoid.utils.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Picasso Target class with Ripple Effect
 */
public class RippleTarget implements Target {

    private static final String TAG = "RippleTarget";
    private RippleTargetDelegate delegate;
    private ImageView imageView;
    private Context mContext;
    private int rippleColor;

    public RippleTarget(Context context, ImageView imageView){
        this.imageView = imageView;
        this.mContext = context;
        this.rippleColor = ContextCompat.getColor(context, android.R.color.white);
    }

    public RippleTarget(Context context, ImageView imageView, int rippleColor){
        this.imageView = imageView;
        this.mContext = context;
        this.rippleColor = rippleColor;
    }

    public RippleTarget(Context context, ImageView imageView, RippleTargetDelegate delegate){
        this.imageView = imageView;
        this.mContext = context;
        this.delegate = delegate;
        this.rippleColor = ContextCompat.getColor(context, android.R.color.white);
    }

    public RippleTarget(Context context, ImageView imageView, int rippleColor, RippleTargetDelegate delegate){
        this.imageView = imageView;
        this.mContext = context;
        this.delegate = delegate;
        this.rippleColor = rippleColor;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Drawable image = new BitmapDrawable(mContext.getResources(), bitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable rippledImage = new RippleDrawable(ColorStateList.valueOf(rippleColor), image, null);
            imageView.setImageDrawable(rippledImage);
        }else{
            imageView.setImageDrawable(image);
        }

        imageView.requestLayout();

        if(delegate != null) {
            delegate.onRippleTargetLoadComplete();
        }else{
            Log.e(TAG, "Error");
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.e(TAG, "onBitmapFailed");
        if(delegate != null) {
            delegate.onRippleTargetLoadFailed();
        }else{
            Log.e(TAG, "Error");
        }
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        imageView.setImageDrawable(placeHolderDrawable);

        imageView.requestLayout();
    }

     public interface RippleTargetDelegate{
        void onRippleTargetLoadComplete();
        void onRippleTargetLoadFailed();
    }
}
