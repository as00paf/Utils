package com.pafoid.utils.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * Helper class used to manipulate Bitmaps
 */
public class BitmapUtils {
	public static final String TAG = "BitmapUtils";

    /**
     * Helper method used to blur a bitmap
     * @param context the Context to use to blur the bitmap
     * @param bitmap the bitmap to blur
     * @return the same bitmap but blurred
     */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) 
	public static Bitmap BlurImage(Context context, Bitmap bitmap)
	{
		if(bitmap == null)
            return null;

		Bitmap result;
		try {	
	        RenderScript rsScript = RenderScript.create(context);
	        Allocation alloc = Allocation.createFromBitmap(rsScript, bitmap);
	
	        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, alloc.getElement());
	        blur.setRadius(25);
	        blur.setInput(alloc);
	
	        result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
	        Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
	        blur.forEach(outAlloc);
	        outAlloc.copyTo(result);
	
	        rsScript.destroy();
		} catch (Exception e) {
            Log.e(TAG, "Could not blur image. Error : " + e.getMessage());
            result = bitmap;
		}
        return result;
    }

    /**
     * Helper method used to size down a Bitmap from a file
     * @param file the path of the Bitmap to resize
     * @param width the desired width
     * @param height the desired height
     * @return the resized Bitmap
     */
	public static Bitmap ShrinkBitmap(String file, int width, int height){
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		
		int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
		int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

		if (heightRatio > 1 || widthRatio > 1){
			if (heightRatio > widthRatio){
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio; 
			}
		}
		
		bmpFactoryOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(file, bmpFactoryOptions);
	}

	/**
	 * Rounds a bitmap to the specified size
	 * @param bitmap the bitmap to resize
	 * @param size the size of the returned bitmap
	 * @return a rounded bitmap
	 */
	public static Bitmap getRoundedBitmap(Bitmap bitmap, int size){
	    Bitmap targetBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
	    Path path = new Path();
	    path.addCircle(((float) size - 1) / 2, ((float) size - 1) / 2, (Math.min(((float) size), ((float) size)) / 2), Path.Direction.CCW);
	
	    Canvas canvas = new Canvas(targetBitmap);
	    canvas.clipPath(path);
	    Bitmap sourceBitmap = bitmap;
	    canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, size, size), null);
	    
	    return targetBitmap;
	}

    /**
     * Rounds a bitmap to the size of it's smallest side
     * @param originalBitmap
     * @return a rounded bitmap
     */
	public static Bitmap bitmapToCircular(Bitmap originalBitmap){
		Bitmap bitmap = originalBitmap;
	       
		if(bitmap!=null) {
			int height = bitmap.getHeight();
			int width = bitmap.getWidth();
			int newSize = (height<width)?height:width;
			int targetWidth = newSize;
			int targetHeight = newSize ;
			Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Config.ARGB_8888);
			BitmapShader shader;
			shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(shader);
			Canvas canvas = new Canvas(targetBitmap);
			Path path = new Path();
			path.addCircle(((float) targetWidth - 1) / 2,
					((float) targetHeight - 1) / 2,
					(Math.min(((float) targetWidth),((float) targetHeight)) / 2),Path.Direction.CCW);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			paint.setStyle(Style.STROKE);
			canvas.clipPath(path);
			Bitmap sourceBitmap = bitmap;
			canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),sourceBitmap.getHeight()),
					new Rect(0, 0, targetWidth,targetHeight), null);
    
			bitmap = targetBitmap;   //set the circular image to your imageview
    	}
		return bitmap;
	}

    /**
     * Returns a {@link Bitmap} from a {@link Drawable}
     * @param drawable the drawable to convert
     * @return a {@link Bitmap} of the specified {@link Drawable}
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

	/**
	 * Returns a {@link Bitmap} from a {@link Drawable}
	 * @param drawable the drawable to convert
	 * @param scale the scale to draw the bitmap
	 * @return a scaled {@link Bitmap} of the specified {@link Drawable}
	 */
	public static Bitmap drawableToScaledBitmap(Drawable drawable, int scale) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 *
	 * @param sourceBitmap
	 * @param color
	 * @return
	 */
    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, Mode.SRC_IN);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    /**
     * Helper method used to save a Bitmap to the disk
     * @param context the Context to use
     * @param outputImage the Bitmap to save
     * @param imageName the name of the File
     */
	public static void writeFileToInternalStorage(Context context, Bitmap outputImage, String imageName) {
	    String fileName = imageName;

	    FileOutputStream stream;
		try {
			stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			outputImage.compress(CompressFormat.PNG, 90, stream);

		    stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    /**
     * Helper method used to add a border to a Bitmap
     * @param bitmap the Bitmap to add the border to
     * @param borderSize the size of the border in pixels
     * @param borderColor the color of the border
     * @return the same Bitmap with a border
     */
	public static Bitmap addCircularBorderToBitmap(Bitmap bitmap, int borderSize, int borderColor){
        Bitmap result = null;
        if( bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int radius = Math.min(height / 2, width / 2);
            result = Bitmap.createBitmap(width + 8, height + 8, Config.ARGB_8888);

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            Canvas canvas = new Canvas(result);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setStyle(Style.FILL);

            canvas.drawCircle((width / 2) + 4, (height / 2) + 4, radius, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            canvas.drawBitmap(bitmap, 4, 4, paint);
            paint.setXfermode(null);
            paint.setStyle(Style.STROKE);
            paint.setColor(borderColor);
            paint.setStrokeWidth(borderSize);
            canvas.drawCircle((width / 2) + 4, (height / 2) + 4, radius, paint);
        }
		return result;
	}

    /**
     * Helper method used to round an image
     * @param bitmap the Bitmap to round
     * @return the same Bitmap rounded
     */
    public static Bitmap getProfileIcon(Bitmap bitmap){
        if (bitmap != null){
            bitmap = bitmapToCircular(bitmap);
        }
        return bitmap;
    }

    /**
     * Helper method used to overlay two bitmaps on top of the other
     * @param bmp1 the Bitmap on the bottom
     * @param bmp2 the Bitmap on top
     * @return the merged Bitmap
     */
    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2){
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    /**
     * Helper method used to overlay two bitmaps on top of the other
     * @param backgroundBitmap the Bitmap on the bottom
     * @param overlayBitmap the Bitmap on top
     * @param left the left offset of the top Bitmap in pixels
     * @param top the top offset of the top Bitmap in pixels
     * @return the merged Bitmap
     */
    public static Bitmap overlay(Bitmap backgroundBitmap, Bitmap overlayBitmap, float left, float top) {
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), backgroundBitmap.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, new Matrix(), null);
        canvas.drawBitmap(overlayBitmap, left, top, null);
        return resultBitmap;
    }
}

