package com.pafoid.utils.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.squareup.picasso.Transformation;

/**
 * Picasso {@link Transformation} class used to blur an image
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class BlurTransform implements Transformation{

    private static final String TAG = "BlurTransform";

    private static final int DEFAULT_BLUR = 25;

    private Context context;
    private int radius;

    public BlurTransform(Context context){
        this.context = context;
        this.radius = DEFAULT_BLUR;
    }

    public BlurTransform(Context context, int radius){
        this.context = context;
        this.radius = radius;
    }

    @Override
    public Bitmap transform(Bitmap source){
        if(source == null)
            return null;

        Bitmap result;
        try {
            RenderScript rsScript = RenderScript.create(context);
            Allocation alloc = Allocation.createFromBitmap(rsScript, source);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, alloc.getElement());
            blur.setRadius(radius);
            blur.setInput(alloc);

            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Could not blur image. Error : " + e.getMessage());
            result = source;
        }
        return result;
    }

    @Override
    public String key()
    {
        return TAG;
    }
}
