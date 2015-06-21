package com.rubiks.lehoang.rubikssolver;

import android.content.Context;
import android.util.Log;


import com.rubiks.lehoang.rubikssolver.Cube.Colour;
import com.rubiks.lehoang.rubikssolver.Cube.OldCube;

import org.kociemba.twophase.Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LeHoang on 04/01/2015.
 */
public class Util {

    private static final String LOG_TAG = "com.lehoang.rubikssolve";

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

    public static void addFaceToConfig(String filename, String face, Colour[][] config, Context context){
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(face.getBytes());
            outputStream.write('\n');
            //Write config here
            for (int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    outputStream.write(Colour.ColourToLetter(config[i][j]));

                }
                outputStream.write('\n');
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<Colour, String> getColourFaceMap(OldCube cube){
        Map<Colour, String> colourToFace = new HashMap<Colour, String>();

        colourToFace.put(cube.getFace(OldCube.BACK).getFaceColour(),"B");
        colourToFace.put(cube.getFace(OldCube.FRONT).getFaceColour(),"F");
        colourToFace.put(cube.getFace(OldCube.RIGHT).getFaceColour(),"R");
        colourToFace.put(cube.getFace(OldCube.LEFT).getFaceColour(),"L");
        colourToFace.put(cube.getFace(OldCube.TOP).getFaceColour(),"U");
        colourToFace.put(cube.getFace(OldCube.BOTTOM).getFaceColour(),"D");

        return colourToFace;
    }

    public static String myCubeToKociemba(OldCube cube){

        StringBuilder kociemba = new StringBuilder();
        Map<Colour, String> colourToFace = getColourFaceMap(cube);

        /**
         * Face[] faces = {top, right, front, bottom, left, back};
         */
        int count = 0;
        for(OldCube.Face face : cube){
            OldCube.Face conv = face;

            // Odd faces need to be flipped
            if(count % 2 == 1) {
                if (count == 3) {
                    //Bottom
                    conv = face.flipX();
                } else {
                    //right and back
                    conv = face.flipY();
                }
            }

            for(Colour square : conv){
                kociemba.append(colourToFace.get(square));
            }

            count++;
        }


        return kociemba.toString();
    }

    public static long base12to10(String sequence){
        long base10Sequence = 0;
        for(int i = sequence.length()-1; i >= 0 ; i--){
            char unit = sequence.charAt(i);

            int expo = unit - 'a';
            int unitBase10 = (int) (expo* Math.pow(12, (sequence.length() - 1 - i)));
            base10Sequence += unitBase10;
        }

        return base10Sequence;
    }


    //UF UR UB UL DF DR DB DL FR FL BR BL UFR URB UBL ULF DRF DFL DLB DBR

    private static int[][] convSingMasterEdge = {{7,19}, {5,10}, {1,46}, {3,37},
            {28,25}, {32,16},{34,52}, {30,43},
            {23,12},{21,41},{48,14},{50,39}};

    private static int[][] getConvSingMasterCorner = {{8,20,9},{2,11,45},
            {0,47,36},{6,38,18},
            {29,15,26},{27,24,44},
            {33,42,53},{35,51,17}};

    public static String compactToSingmaster(String cube){
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < convSingMasterEdge.length; i++){
            for(int j = 0; j < convSingMasterEdge[0].length;j++){
                builder.append(cube.charAt(convSingMasterEdge[i][j]));
            }
            builder.append(" ");
        }

        for(int i = 0; i < getConvSingMasterCorner.length; i++){
            for(int j = 0; j < getConvSingMasterCorner[0].length; j++){
                builder.append(cube.charAt(getConvSingMasterCorner[i][j]));
            }
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static String solveCubeUsingKociemba(OldCube cube){
        int maxDepth = 24, maxTime = 5;
        boolean useSeparator = false;
        String setup = Util.myCubeToKociemba(cube);

        String result = Search.solution(setup, maxDepth, maxTime, useSeparator);
        result = result.replaceAll("\\s+", "");
        return result;
    }

    public static int max(int[] array){
        int max = -1;

        for(int val: array){
            if(val > max){
                max= val;
            }
        }

        return max;
    }

    public static void order(char[] arr){
        if(arr[0] > arr[1]){
            char temp = arr[0];
            arr[0] = arr[1];
            arr[1] = temp;
        }
    }
    public static int countMoves(String sequence){
        int sum = 0;
        for(int i = 0; i < sequence.length(); i++){
            switch (sequence.charAt(i)){
                case 'R':
                    sum++;
                    break;
                case 'F':
                    sum++;
                    break;
                case 'L':
                    sum++;
                    break;
                case 'U':
                    sum++;
                    break;
                case 'D':
                    sum++;
                    break;
                case 'B':
                    sum++;
                    break;
                default:
                    System.out.println(sequence.charAt(i));
            }
        }
        return sum;
    }



}
