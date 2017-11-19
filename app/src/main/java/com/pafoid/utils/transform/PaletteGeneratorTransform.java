package com.pafoid.utils.transform;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Picasso {@link Transformation} class used get the median color of an image
 */
public class PaletteGeneratorTransform implements Transformation {

    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();
    private static final PaletteGeneratorTransform INSTANCE = new PaletteGeneratorTransform();

    public static PaletteGeneratorTransform instance() {
        return INSTANCE;
    }

    private PaletteGeneratorTransform() {}

    @Override
    public String key() {
        return ""; // Stable key for all requests. An unfortunate requirement.
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Palette palette = Palette.from(source).generate();
        CACHE.put(source, palette);

        return source;
    }
}