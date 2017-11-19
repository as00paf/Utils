package com.pafoid.utils.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.pafoid.utils.R;
import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to desaturate an image
 */
public class GreyOutOverlayTransform implements Transformation
{
    public static final String TAG = "GradientOverlayTrans";
    public static final int[] mColors = {R.color.greyouttransparentfilter, R.color.greyouttransparentfilter};

    private float[] positions = {0.0f, 1.0f};

    public GreyOutOverlayTransform() {
    }

    public Bitmap transform(Bitmap source) {
        Bitmap bm = this.mutable(source);
        if(bm == null) {
            Log.e("GreyOutOverlayTransform", "bitmap could not be copied, returning untransformed");
            return source;
        } else {
            int width = bm.getWidth();
            int height = bm.getHeight();

            Bitmap dest = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(dest);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0); //value of 0 maps the color to gray-scale
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(filter);
            canvas.drawBitmap(bm, 0, 0, paint);

            source.recycle();

            return dest;
        }
    }

    public String key() {
        return "GreyOutOverlayTransform";
    }

    public Bitmap mutable(Bitmap source) {
        if(source.isMutable()) {
            return source;
        } else {
            Bitmap.Config config = source.getConfig();
            Bitmap bm = source.copy(config != null?config: Bitmap.Config.ARGB_8888, true);
            if(bm != null) {
                source.recycle();
            }

            return bm;
        }
    }

}
