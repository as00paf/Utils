package com.pafoid.utils.transform;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to add drop shadow behind a bitmap
 */
public class DropShadowTransform implements Transformation
{
    private int shadowSize;
    private int shadowColor;

    public DropShadowTransform()
    {
        this.shadowSize = -1;
        this.shadowColor = -1;
    }

    public DropShadowTransform(int shadowSize, int shadowColor)
    {
        this.shadowSize = shadowSize;
        this.shadowColor = shadowColor;
    }

    @Override
    public Bitmap transform(Bitmap source)
    {
        Bitmap mask = Bitmap.createBitmap(shadowSize, shadowSize, Bitmap.Config.ALPHA_8);

        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, source.getWidth(), source.getHeight());
        final RectF dst = new RectF(0, 0, source.getWidth() - shadowSize, source.getHeight() - shadowSize);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(shadowSize, shadowSize);

        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(source, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(source, dropShadow, paint);

        final BlurMaskFilter filter = new BlurMaskFilter(shadowSize, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(shadowColor);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        final Bitmap ret = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0,  0, paint);
        retCanvas.drawBitmap(source, scaleToFit, null);
        mask.recycle();
        source.recycle();

        return ret;
    }

    @Override
    public String key()
    {
        return "drop_shadow";
    }
}
