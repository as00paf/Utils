package com.pafoid.utils.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.ColorRes;

import com.pafoid.utils.R;
import com.squareup.picasso.Transformation;

//TODO : merge with gradient overlay transformation

/**
 * Picasso {@link Transformation} class used to add a gradient to an image
 */
public class GradientTransformation implements Transformation {
    private final int[] mColors;
    private float[] positions = {0.0f, 0.5f, 0.5f, 1.0f};

    private Context context;

    public GradientTransformation(Context context) {
        super();
        this.context = context;
        mColors = new int[]{context.getResources().getColor(R.color.colorOverlay), Color.TRANSPARENT, Color.TRANSPARENT, context.getResources().getColor(R.color.colorOverlay)};
    }

    public GradientTransformation(Context context, @ColorRes int color) {
        super();
        this.context = context;
        mColors = new int[]{context.getResources().getColor(color), Color.TRANSPARENT, Color.TRANSPARENT, context.getResources().getColor(color)};
    }

    @Override
    public Bitmap transform(Bitmap source) {
        // create the output bitmap structure
        Bitmap outputBitmap = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        // prepare canvas + paint with all ingredients
        ComposeShader composeShader = _getVignetteShader(source);
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setShader(composeShader);
        paint.setAntiAlias(true);

        // paint on canvas which holds to be rendered bitmap
        canvas.drawPaint(paint);

        // recycle the original bitmap which we no longer require
        source.recycle();

        return outputBitmap;
    }

    private ComposeShader _getVignetteShader(Bitmap source) {// create the bitmap shader
        // create a bitmap shader
        Shader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // create the linear gradient shader
        Shader linearGradientShader = getLinearGradient(source);

        // create a shader that combines both effects
        return new ComposeShader(bitmapShader, linearGradientShader, PorterDuff.Mode.SRC_ATOP);
    }

    private LinearGradient getLinearGradient(Bitmap source) {
        return new LinearGradient(0, 0, 0, source.getHeight(), mColors, positions, Shader.TileMode.CLAMP);
    }

    @Override public String key() {
        return "Gradient";
    }
}
