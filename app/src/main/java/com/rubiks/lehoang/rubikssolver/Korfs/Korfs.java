package com.rubiks.lehoang.rubikssolver.Korfs;

import android.os.Environment;

import com.rubiks.lehoang.rubikssolver.Cube;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

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

    public static String searchKorfs(Cube cube, int maxDepth) throws Exception {
        if(cube.isSolved()){
            System.out.println("Already solved!");
            return "";
        }

        int minKnownSolutionLength = Node.getHeuristic(cube);
        String solution = null;
        while(minKnownSolutionLength < maxDepth){
            System.out.println("Trying depth:" + minKnownSolutionLength);
            solution = search(cube, minKnownSolutionLength);

            try {
                minKnownSolutionLength = Integer.parseInt(solution);
            }catch(NumberFormatException e){
                //If this happens then we have a solution
                break;
            }
        }

        return solution;
    }

    private static String search(Cube cube, int solutionLength) throws Exception {
        PriorityQueue<Node> workQueue = new PriorityQueue<Node>();
        Set<Node> seen = new HashSet<Node>();
        Node node = new Node(cube.toString());
        workQueue.add(node);
        seen.add(node);
        int minOverDepth = solutionLength+1;

        while(!workQueue.isEmpty()){
            Node current = workQueue.poll();

            if(current.isSolved()){
                return current.getSequence();
            }else{
                seen.add(current);

                for(Node child: current.generateChildren()){
                    //Find the minimum depth such that the child has estimated a solution is possible.
                    //E.g. if all children say the solution is 8 moves away,
                    // there's no point in checking 2 move solutions.
                    int childGoalEstimate = child.getEstimateToGoal();
                    //System.out.println("Estimate is:" +childGoalEstimate);

                    if(childGoalEstimate > solutionLength && childGoalEstimate < minOverDepth){
                        System.out.println("New depth found:" + childGoalEstimate);
                        minOverDepth = childGoalEstimate;
                    }

                    //Only add it to the worklist to be expanded if we haven't seen it before and
                    //its estimate is less than the desired solution length
                    if(!seen.contains(child) && childGoalEstimate <= solutionLength){
                        System.out.println(workQueue.size());
                        workQueue.add(child);
                    }
                }
            }

        }

        return Integer.toString(minOverDepth);
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
                    scoreMap.put(encoding, prevScore + 1);
                    String toWrite = encoding + ", "  + (prevScore+1) + "\n";
                    stream.write(toWrite.getBytes());
                }
            }
        }
        stream.close();


    }


    public static void generateFirstEdgeHeuristics(File file) throws Exception {
        generateEdgeHeuristics(file, Cube.firstEdges);
    }

    public static void generateEdgeHeuristics(File file, Cube.Edge[] edges) throws Exception{
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();

        Queue<String> workQueue = new ArrayDeque<String>();

        Cube solved = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        workQueue.add(Cube.SOLVED);
        scoreMap.put(solved.encode(edges),0);

        Writer solvedStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.US_ASCII));
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(solved.encode(edges));
            builder.append(",0\n");

            solvedStream.write(builder.toString());
            builder.setLength(0);

            while (!workQueue.isEmpty()) {
                String state = workQueue.poll();

                for (String move : Cube.moves) {
                    //Create a cube from the state
                    Cube current = new Cube(new BufferedReader(new StringReader(state)));
                    int prevScore = scoreMap.get(current.encode(edges));
                    current.performSequence(move);

                    String encoding = current.encode(edges);
                    if (!scoreMap.containsKey(encoding)) {
                        workQueue.add(current.toString());
                        scoreMap.put(encoding, prevScore + 1);

                        builder.append(encoding);
                        builder.append(',');
                        builder.append(prevScore + 1);
                        builder.append('\n');
                        solvedStream.write(builder.toString());
                        builder.setLength(0);
                    }
                }
            }
        }finally {
            solvedStream.close();
        }
    }
    public static void generateSecondEdgeHeuristics(File file) throws Exception {
        generateEdgeHeuristics(file, Cube.secondEdges);
    }

    public static void loadHeuristics() throws IOException {
        if(cornerMap.isEmpty()) {
            loadCorners(new File(CORNERS_FILE_NAME));
        }
        if(firstEdgeMap.isEmpty()) {
            loadFirstEdges(new File(FIRST_EDGE_FILE_NAME));
        }
        if(secondEdgeMap.isEmpty()) {
            loadSecondEdges(new File(SECOND_EDGE_FILE_NAME));
        }
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
