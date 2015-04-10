package com.rubiks.lehoang.rubikssolver.Korfs;

import android.content.Context;
import android.os.Environment;

import com.rubiks.lehoang.rubikssolver.Cube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class Korfs {

    public static String CORNERS_FILE_NAME= "corners.csv";
    public static String FIRST_EDGE_FILE_NAME = "firstEdge.csv";
    public static String SECOND_EDGE_FILE_NAME = "secondEdge.csv";

    public static Map<String, Integer> cornerMap = new HashMap<String, Integer>();
    public static Map<String, Integer> firstEdgeMap = new HashMap<String, Integer>();
    public static Map<String, Integer> secondEdgeMap = new HashMap<String, Integer>();

    public static String Search(Cube cube){
        if(cube.isSolved()){
            return "";
        }

        int depth = 1;


        return null;

    }

    public static String Search(Cube cube, int depth){
        return null;
    }



    public static String generateCornersAndroid() throws Exception {
        File path = Environment.getExternalStorageDirectory();

        File file = new File(path,"korfs_corners.csv");

        generateCornerHeuristics(file);

        return file.getAbsolutePath();
    }

    public static void generateCornerHeuristics(File file) throws Exception {
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();

        Queue<String> workQueue = new ArrayDeque<String>();

        Cube solved = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        workQueue.add(Cube.SOLVED);
        scoreMap.put(solved.encodeCorners(),0);

        FileOutputStream solvedStream = new FileOutputStream(file);
        try{
            String toWrite = solved.encodeCorners()+", "+"0\n";
            solvedStream.write(toWrite.getBytes());
        }finally{
            solvedStream.close();
        }
        FileOutputStream stream = new FileOutputStream(file, true);
        while(!workQueue.isEmpty()){
            //Take a cube state from the work queue
            String state = workQueue.poll();

            for(String move : Cube.moves){
                //Create a cube from the state
                Cube current = new Cube(new BufferedReader(new StringReader(state)));
                int prevScore = scoreMap.get(current.encodeCorners());
                current.performSequence(move);
                String encoding = current.encodeCorners();

                //Check if state has been seen before
                if(!scoreMap.containsKey(encoding)){
                    String newState = current.toString();

                    //Add to worklist to be expanded
                    workQueue.add(newState);

                    //Put in hashmap
                    scoreMap.put(encoding, prevScore+1);
                    String toWrite = encoding + ", "  + (prevScore+1) + "\n";
                    stream.write(toWrite.getBytes());
                }
            }
        }
        stream.close();


    }

    public static void generateFirstEdgeHeuristics(File file) throws Exception {
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();

        Queue<String> workQueue = new ArrayDeque<String>();

        Cube solved = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        workQueue.add(Cube.SOLVED);
        scoreMap.put(solved.encodeFirstEdges(),0);

        FileOutputStream solvedStream = new FileOutputStream(file);
        try{
            String toWrite = solved.encodeFirstEdges()+", "+"0\n";
            solvedStream.write(toWrite.getBytes());
        }finally{
            solvedStream.close();
        }

        FileOutputStream stream = new FileOutputStream(file, true);

        while(!workQueue.isEmpty()){
            String state = workQueue.poll();

            for(String move: Cube.moves){
                //Create a cube from the state
                Cube current = new Cube(new BufferedReader(new StringReader(state)));
                int prevScore = scoreMap.get(current.encodeFirstEdges());
                current.performSequence(move);

                String encoding = current.encodeFirstEdges();
                if(!scoreMap.containsKey(encoding)){
                    String newState = current.toString();

                    workQueue.add(newState);

                    scoreMap.put(encoding, prevScore+1);


                    String toWrite = encoding + ", "  + (prevScore+1) + "\n";
                    stream.write(toWrite.getBytes());

                }
            }
        }
        stream.close();

    }

    public static void generateSecondEdgeHeuristics(File file) throws Exception {
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();

        Queue<String> workQueue = new ArrayDeque<String>();

        Cube solved = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        workQueue.add(Cube.SOLVED);
        scoreMap.put(solved.encodeSecondEdges(),0);

        FileOutputStream solvedStream = new FileOutputStream(file);
        try{
            String toWrite = solved.encodeSecondEdges()+", "+"0\n";
            solvedStream.write(toWrite.getBytes());
        }finally{
            solvedStream.close();
        }

        FileOutputStream stream = new FileOutputStream(file, true);

        while(!workQueue.isEmpty()){
            String state = workQueue.poll();

            for(String move: Cube.moves){
                //Create a cube from the state
                Cube current = new Cube(new BufferedReader(new StringReader(state)));
                int prevScore = scoreMap.get(current.encodeSecondEdges());
                current.performSequence(move);

                String encoding = current.encodeSecondEdges();
                if(!scoreMap.containsKey(encoding)){
                    String newState = current.toString();

                    workQueue.add(newState);

                    scoreMap.put(encoding, prevScore+1);


                    String toWrite = encoding + ", "  + (prevScore+1) + "\n";
                    stream.write(toWrite.getBytes());

                }
            }
        }
        stream.close();
    }

    public static void loadHeuristics(File corners, File firstEdge, File secondEdge) throws IOException {
        loadCorners(corners);
    }


    public static void loadFirstEdges(File file) throws IOException {
        populateMap(file, firstEdgeMap);
    }

    private static void populateMap(File file, Map<String, Integer> map) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);

        String readLine = reader.readLine();

        while(readLine != null){
            String[] parts = readLine.split(",");
            map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            readLine = reader.readLine();
        }
    }

    public static void loadCorners(File corners) throws IOException {
        populateMap(corners, cornerMap);
    }

    public static void loadSecondEdges(File file) throws IOException {
        populateMap(file, secondEdgeMap);
    }
}
