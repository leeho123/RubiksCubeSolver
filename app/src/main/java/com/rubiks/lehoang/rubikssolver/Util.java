package com.rubiks.lehoang.rubikssolver;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by LeHoang on 04/01/2015.
 */
public class Util {

    private static final String LOG_TAG = "com.rubiks.lehoang.rubikssolver";

    public static void LogError(String message){
        Log.e(LOG_TAG, message);
    }

    public static void LogDebug(String message){
        Log.d(LOG_TAG, message);
    }

    public static void LogError(String source, String message){
        LogError(source + ": " + message);
    }

    public static void printConfigFile(String filename,Context context){
        Util.LogDebug("CONFIG FILE");
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                Util.LogDebug(line);
                line = reader.readLine();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addFaceToConfig(String filename, String face, Square.Colour[][] config, Context context){
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(face.getBytes());
            outputStream.write('\n');
            //Write config here
            for (int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    outputStream.write(Square.Colour.ColourToLetter(config[i][j]));

                }
                outputStream.write('\n');
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
