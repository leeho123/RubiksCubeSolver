package com.rubiks.lehoang.rubikssolver;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
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

    public static void addFaceToConfig(String filename, String face, Square.Colour[][] config, Context context){
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(context.getResources().openRawResource(R.raw.state)));
    }
}
