package com.pafoid.utils.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to round an image and add a drop shadow
 */
public class CircleShadowTransform implements Transformation{

    public static final String TAG = "CircleShadowTrans";

    private int borderSize, borderColor, shadowSize, shadowColor;

    public CircleShadowTransform(int borderSize, int borderColor){
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        this.shadowSize = 0;
        this.shadowColor = -1;
    }

    public CircleShadowTransform(int borderSize, int borderColor, int shadowSize, int shadowColor){
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        this.shadowSize = shadowSize;
        this.shadowColor = shadowColor;
    }

    @Override
    public Bitmap transform(Bitmap source){
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source){
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        float radius = size / 2f;

        //Draw Shadow
        if(shadowSize > 0){
            Paint shadowPaint = new Paint();
            BlurMaskFilter filter = new BlurMaskFilter(shadowSize/2, BlurMaskFilter.Blur.NORMAL);
            shadowPaint.setMaskFilter(filter);
            shadowPaint.setFilterBitmap(true);
            shadowPaint.setColor(shadowColor);
            shadowPaint.setAlpha(75);
            shadowPaint.setAntiAlias(true);

            canvas.drawCircle(radius - shadowSize/4, radius - shadowSize/4, radius - shadowSize, shadowPaint);
        }

        //radius = (size / 2f) - shadowSize/2;

        //Draw Source
        Paint bitmapPaint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        bitmapPaint.setShader(shader);
        bitmapPaint.setAntiAlias(true);

        canvas.drawCircle(radius - shadowSize/2, radius - shadowSize/2, radius - shadowSize, bitmapPaint);

        //Draw border
        if (borderSize > 0){
            radius = size / 2f;

            Paint strokePaint = new Paint();
            strokePaint.setColor(borderColor);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setAntiAlias(true);
            strokePaint.setStrokeWidth(borderSize);
            canvas.drawCircle(radius - shadowSize/2, radius - shadowSize/2, radius - borderSize/2 - shadowSize, strokePaint);
        }

        squaredBitmap.recycle();

        return bitmap;
    }

    @Override
    public String key(){
        return TAG;
    }
}
