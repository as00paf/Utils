package com.pafoid.utils.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to round an image
 */
public class CircleTransform implements Transformation
{
    private static final String TAG = "CircleTransform";

    private int mBorderSize;
    private int mBorderColor;
    private int padding = 0;
    private int backgroundColor = Color.WHITE;

    public CircleTransform()
    {
        this.mBorderSize = -1;
        this.mBorderColor = -1;
    }

    public CircleTransform(int borderSize, int borderColor)
    {
        this.mBorderSize = borderSize;
        this.mBorderColor = borderColor;
    }

    public CircleTransform(int borderSize, int borderColor, int padding)
    {
        this.mBorderSize = borderSize;
        this.mBorderColor = borderColor;
        this.padding = padding;
    }

    public CircleTransform(int borderSize, int borderColor, int padding, int backgroundColor)
    {
        this.mBorderSize = borderSize;
        this.mBorderColor = borderColor;
        this.padding = padding*2;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Bitmap transform(Bitmap source){
        int size = Math.max(source.getWidth(), source.getHeight()) + padding + mBorderSize*2;

        int x = (source.getWidth() - size) / -2;
        int y = (source.getHeight() - size) / -2;

        Bitmap squaredBitmap = Bitmap.createBitmap(size, size, source.getConfig() == null ? Bitmap.Config.ARGB_8888 : source.getConfig());
        Canvas sizedCanvas = new Canvas(squaredBitmap);
        sizedCanvas.drawColor(backgroundColor);

        Paint paint1 = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint1.setShader(shader);
        paint1.setAntiAlias(true);
        sizedCanvas.drawBitmap(source, x, y, paint1);

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig() == null ? Bitmap.Config.ARGB_8888 : source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint1);

        if (mBorderSize > 0){
            Paint paint2 = new Paint();
            paint2.setColor(mBorderColor);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setAntiAlias(true);
            paint2.setStrokeWidth(mBorderSize);
            canvas.drawCircle((squaredBitmap.getWidth()) / 2, (squaredBitmap.getHeight()) / 2, r - mBorderSize/2, paint2);
            //Log.d(TAG, "Border created");
        }

        source.recycle();
        squaredBitmap.recycle();

        return bitmap;
    }

    @Override
    public String key()
    {
        return "circle";
    }
}
