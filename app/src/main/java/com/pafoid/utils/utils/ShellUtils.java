package com.pafoid.utils.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility Class used to interact with Shell
 * <p>
 *     This helper class is used to execute commands in the command shell
 * </p>
 */
public class ShellUtils {

    /**
     * Helper method used to execute a command in the shell
     * @param command a String value representing the command to execute
     * @return the shell output
     */
    public static String executeCommand(String command){
        try{
            Process cmd = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            cmd.waitFor();

            return output.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
