package com.pafoid.utils.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.pafoid.utils.R;
import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to add a gradient overlay to an image
 */
public class GradientOverlayTransform implements Transformation {
    public static final String TAG = "GradientOverlayTrans";

    public static final int[] defaultBottomColors = {Color.TRANSPARENT, R.color.blacktransparentfilter}; // [0] set in constructor
    public static final float[] defaultBottomPositions = {0.8f, 1.0f};

    public static final int[] defaultTopColors = {R.color.blacktransparentfilter, Color.TRANSPARENT}; // [0] set in constructor
    public static final float[] defaultTopPositions = {0.0f, 0.2f};

    public static final int[] defaultTopBottomColors = {R.color.blacktransparentfilter, Color.TRANSPARENT, R.color.blacktransparentfilter}; // [0] set in constructor
    public static final float[] defaultTopBottomPositions = {0.2f, 0.8f, 1.0f};

    private final GradientDrawable.Orientation mOrient;
    private final int[] mColors;
    private float[] positions = defaultTopBottomPositions;

    public static GradientOverlayTransform buildTopBottom(){
        return new GradientOverlayTransform(GradientDrawable.Orientation.TOP_BOTTOM, defaultTopBottomColors, defaultTopBottomPositions);
    }

    public static GradientOverlayTransform buildBottom(){
        return new GradientOverlayTransform(GradientDrawable.Orientation.TOP_BOTTOM, defaultBottomColors, defaultBottomPositions);
    }

    public static GradientOverlayTransform buildTop(){
        return new GradientOverlayTransform(GradientDrawable.Orientation.TOP_BOTTOM, defaultTopColors, defaultTopPositions);
    }

    /**
     * Use the default colors, orentation and positions. The first quarter of the overlay will be
     * {@code R.color.greytransparentfilter}. The middle part will fade from {@code R.color.greytransparentfilter} to transparent.
     * The last quarter will be {@code R.color.greytransparentfilter}.
     */
    public GradientOverlayTransform(Context context) {
        this(GradientDrawable.Orientation.TOP_BOTTOM, defaultTopBottomColors, defaultTopBottomPositions);
    }

    /**
     * Use the default colors and positions. The first half of the overlay will be
     * {@code R.color.overlay}. The second half will fade from {@code overlay} to transparent.
     */
    public GradientOverlayTransform(Context context, GradientDrawable.Orientation orientation) {
        this(orientation, defaultTopBottomColors, defaultTopBottomPositions);
    }

    /**
     * Use the color and default positions. The first half of the overlay will be the color. The
     * second half will fade from the color to transparent.
     *
     * @since 2.4.0
     */
    public GradientOverlayTransform(Context context, GradientDrawable.Orientation orientation, int colorResId) {
        this(orientation, new int[]{ContextCompat.getColor(context, colorResId), Color.TRANSPARENT},
                defaultTopBottomPositions);
    }

    public GradientOverlayTransform(GradientDrawable.Orientation orientation, int[] colors, float[] positions) {
        mOrient = orientation;
        mColors = colors;
        this.positions = positions;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bm = mutable(source);
        if (bm == null) {
            Log.e(TAG, "bitmap could not be copied, returning untransformed");
            return source;
        }
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        int width = bm.getWidth();
        int height = bm.getHeight();
        float x0 = 0.0f, y0 = 0.0f, x1 = 0.0f, y1 = 0.0f;
        switch (mOrient) {
            case BOTTOM_TOP:
                y0 = height;
                break;
            case BL_TR:
                y0 = height;
                x1 = width;
                break;
            case LEFT_RIGHT:
                x1 = width;
                break;
            case TL_BR:
                x1 = width;
                y1 = height;
                break;
            case TOP_BOTTOM:
                y1 = height;
                break;
            case TR_BL:
                x0 = width;
                y1 = height;
                break;
            case RIGHT_LEFT:
                x0 = width;
                break;
            case BR_TL:
                x0 = width;
                y0 = height;
                break;
        }
        paint.setShader(new LinearGradient(x0, y0, x1, y1, mColors, positions, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, width, height, paint);

        if (bm != source) {
            source.recycle();
        }

        return bm;
    }

    @Override
    public String key() {
        return TAG;
    }

    public Bitmap mutable(Bitmap source) {
        if (source.isMutable()) {
            return source;
        }
        Bitmap.Config config = source.getConfig();
        Bitmap bm = source.copy(config != null ? config : Bitmap.Config.ARGB_8888, true);
        if (bm != null) {
            source.recycle();
        }
        return bm;
    }

    public void setTopBottomPositions(float[] positions){
        this.positions = positions;
    }
}
