package com.pafoid.utils.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A utility class that provides methods that are related to Files.
 * <p>
 * {@code FileUtils} does not have a constructor and all it's methods are static so
 * it does not needs to be instantiated. To use it, just call the public methods directly like this
 * {@code FileUtils.dirSize(directory)}.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    //Save/Load

    /**
     * Helper method used to save String data to a file
     * @param context the context to use
     * @param fileName the name of the file to save
     * @param data the content of the file to save
     */
    public static void writeToFile(Context context, String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    /**
     * Helper method used to read the content of a file
     * @param context the context to use
     * @param path the path to the file
     * @return a String value representing the content of the file
     */
    public static String readFromFile(Context context, String path) {
        String content = "";

        try {
            InputStream inputStream = context.openFileInput(path);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                content = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return content;
    }

    //File Extensions

    /**
     * Helper method used to retrieve the extension of a File
     * @param filename the name of the File
     * @return a String value representing the extension of the File
     */
    public static String fileExtension(String filename){
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];

        return extension;
    }

    /**
     * Helper method used to strip a file path from it's extension
     * @param filePath the file path
     * @return the file path without the extension
     */
    public static String removeExtension(String filePath){
        File f = new File(filePath);
        if (f.isDirectory()) return filePath;

        String name = f.getName();
        int lastPeriodPos = name.lastIndexOf('.');

        if (lastPeriodPos <= 0){
            return filePath;
        }else{
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }

    //Directory
    /**
     * Helper method used to get the size of a directory in kilobytes
     * @param dir the directory
     * @return a long value representing the size of the directory in kilobytes
     */
    public static long dirSize(File dir) {
        if(dir == null)
            return 0;

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();

            if(fileList != null && fileList.length > 0){
                for (File aFileList : fileList) {
                    // Recursive call if it's a directory
                    if (aFileList.isDirectory()) {
                        result += dirSize(aFileList);
                    } else {
                        // Sum the file size in bytes
                        result += aFileList.length();
                    }
                }
            }

            return result/1024; // return the file size in kilo-bytes
        }
        return 0;
    }

    /**
     * Helper method used to get the size of a directory in kilobytes
     * @param dir the directory
     * @return a BigDecimal value representing the size of the directory in kilobytes
     */
    /*public static BigDecimal dirSize(File dir) {
        if(dir == null)
            return new BigDecimal(0);

        if (dir.exists()) {
            BigDecimal result = new BigDecimal(0);
            File[] fileList = dir.listFiles();

            if(fileList != null && fileList.length > 0){
                for (File aFileList : fileList) {
                    // Recursive call if it's a directory
                    if (aFileList.isDirectory()) {
                        result.add(dirSize(aFileList));
                    } else {
                        // Sum the file size in bytes
                        result.add(new BigDecimal(aFileList.length()));
                    }
                }
            }

            return result.divide(new BigDecimal(1024)); // return the file size in kilo-bytes
        }
        return new BigDecimal(0);
    }*/

    /**
     * Helper method used to get the device's root folder
     * @return
     */
    public static File getRootFolder(){
        return Environment.getRootDirectory().getParentFile();
    }


    /**
     * Helper method used to get a list of files from a specified directory
     * @param dir the directory to use
     * @param showHidden return files that are hidden
     * @return a list of files from a specified directory
     */
    public static ArrayList<File> getDirectoryContent(File dir, final boolean showHidden){
        if(dir == null){
            Log.e(TAG, "Directory is null");
            return null;
        }

        if(!dir.isDirectory()){
            Log.e(TAG, "File is not a directory");
            return null;
        }

        if(dir.listFiles() == null){
            return new ArrayList<>();
        }

        if(showHidden){
            return new ArrayList<File>(Arrays.asList(dir.listFiles()));
        }else{
            return new ArrayList<File>(Arrays.asList(dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isHidden() == showHidden;
                }
            })));
        }
    }
}
