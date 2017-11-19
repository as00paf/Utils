package com.pafoid.utils.transform;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to square an image
 */
public class CropSquareTransform implements Transformation {

    private int mWidth;
    private int mHeight;

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        mWidth = (source.getWidth() - size) / 2;
        mHeight = (source.getHeight() - size) / 2;

        Bitmap bitmap = Bitmap.createBitmap(source, mWidth, mHeight, size, size);
        if (bitmap != source) {
            source.recycle();
        }

        return bitmap;
    }

    @Override
    public String key() {
        return "CropSquareTransform(width=" + mWidth + ", height=" + mHeight + ")";
    }
}
