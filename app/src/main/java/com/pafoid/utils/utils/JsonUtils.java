package com.pafoid.utils.utils;

import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A utility class that allows JSON to be converted to Java classes or vice-versa
 * <p>
 * {@code JsonUtils} does not have a constructor and all it's methods are static so
 * it does not needs to be instantiated.
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * Build an object from the specified JSON resource using Gson.
     *
     * @param type The type of the object to build.
     * @param resources An application {@link Resources} object.
     * @param id The id for the resource to load, typically held in the raw/ folder.
     *
     * @return An object of type T, with member fields populated using Gson.
     */
    public static <T> T constructUsingGson(Resources resources, @RawRes int id, Class<T> type) {
        InputStream resourceReader = resources.openRawResource(id);
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception while using JsonUtils", e);
        } finally {
            try {
                resourceReader.close();
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while using JsonUtils", e);
            }
        }

        String jsonString = writer.toString();

        //Transform
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonString, type);
    }

    //TODO : add method to convert class to JSON
}
