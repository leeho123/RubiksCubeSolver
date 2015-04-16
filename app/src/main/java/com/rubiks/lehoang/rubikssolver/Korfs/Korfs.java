package com.rubiks.lehoang.rubikssolver.Korfs;

import com.google.common.cache.LoadingCache;
import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class Korfs {
    /**
     * Used instead of byte to save half the space since we only need 4 bits per move count
     */
    public static class NibbleArray{
        byte[] arr;
        int length;
        public NibbleArray(int size){
            arr = new byte[size/2];
            length = size;
        }

        public void setIndex(int index, int val){
            int shift = (index %2 ) * 4;
            byte clear = (byte) (15 << shift); //00001111

            //Clear the space
            arr[index/2] = (byte) (arr[index/2] & clear);
            //Set the value
            arr[index/2] = (byte) (arr[index/2] ^ (val << (4-shift)));
        }

        public int getIndex(int index){
            int shift = (1-(index % 2)) * 4;
            int mask =  (15 << shift); //00001111
            byte val = (byte) ((arr[index/2] & mask) >>> shift);
            return val & 0xFF;
        }
    }
    public static String CORNERS_FILE_NAME= "corners.csv";
    public static String FIRST_EDGE_FILE_NAME = "firstEdge.csv";
    public static String SECOND_EDGE_FILE_NAME = "secondEdge.csv";

    public static NibbleArray cornerArr;
    public static NibbleArray firstEdgeArr;
    public static NibbleArray secondEdgeArr;
    public static int[] moveCost = {1,2,1,
                                    1,2,1,
                                    1,2,1,
                                    1,2,1,
                                    1,2,1,
                                    1,2,1};
    static{
        System.out.println("Loading pattern DBs");
        //Fill in corner array
        cornerArr = new NibbleArray(CompactCube.NO_CORNER_ENCODINGS);
        BufferedReader stream;
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(CORNERS_FILE_NAME)));
            String readLine = stream.readLine();
            int index = 0;
            while(readLine != null){
                cornerArr.setIndex(index, Integer.parseInt(readLine));
                index++;
                readLine = stream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Fill in first edge array
        firstEdgeArr = new NibbleArray(CompactCube.NO_EDGE_ENCODINGS);
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(FIRST_EDGE_FILE_NAME)));
            String readLine = stream.readLine();
            int index = 0;
            while(readLine != null){
                firstEdgeArr.setIndex(index, Integer.parseInt(readLine));
                index++;
                readLine = stream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


            //Fill in second edge array
        secondEdgeArr = new NibbleArray(CompactCube.NO_CORNER_ENCODINGS);
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(SECOND_EDGE_FILE_NAME)));
            String readLine = stream.readLine();
            int index = 0;
            while(readLine != null){
                secondEdgeArr.setIndex(index, Integer.parseInt(readLine));
                index++;
                readLine = stream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished loading");
    }

    private Deque<String> solution;
    private CompactCube cube;
    private static final int FOUND = -1;

    public Korfs(CompactCube cube){
        solution = new ArrayDeque<String>();
        this.cube = cube;
    }

    public  String idaStarKorfs(int maxDepth){
        int result = 0;
        if(CompactCube.isSolved(cube)){
            return "";
        }

        int bound = getH(cube);
        while(bound < maxDepth){
            result = search(cube, 0, bound, solution);
            if(result == FOUND){
                return giveSolution(solution);
            }else{
                bound = result;
                System.out.println("Trying depth:" + bound);
            }
        }

        return null;
    }

    private static String giveSolution(Deque<String> solution){
        StringBuilder builder = new StringBuilder();
        while(!solution.isEmpty()){
            builder.append(solution.removeFirst());
        }
        return builder.toString();
    }

    /**
     * Fixed sized cache
     */
    static class SeenCache<T>{
        int size;
        LinkedHashSet<T> cache = new LinkedHashSet<T>();

        public SeenCache(int size){
            this.size = size;
        }

        /**
         * Add an object into the cache. If the cache is full, evict the 10% oldest elements.
         * Reinsertion affects the ordering and will bring the element back to the front
         * @param obj
         */
        public void add(T obj){
            if(cache.size() < size) {
                if (cache.contains(obj)) {
                    cache.remove(obj);
                }
                cache.add(obj);
            }else{
                for(T cand: get10PercentOldestElem()) {
                    cache.remove(cand);
                }
                cache.add(obj);
            }
        }

        public T[] get10PercentOldestElem(){
            int numToGet = cache.size() / 10;
            T[] result = (T[]) new Object[numToGet];
            if(!cache.isEmpty()) {
                Iterator<T> it = cache.iterator();
                for(int i = 0; i < numToGet; i++){
                    result[i] = it.next();
                }
                return result;
            }else{
                return null;
            }
        }

        /**
         * Look up to see if this object exists. If the object doesnt exist, add it in.
         * @param obj
         * @return
         */
        public boolean lookupExists(T obj){
            boolean contains = cache.contains(obj);
            add(obj);
            return contains;
        }
    }

    private static int search(CompactCube cube, int g, int bound, Deque<String> solution) {
        SeenCache<CompactCube> cache = new SeenCache<CompactCube>(1000000);
        int f = g + getH(cube);

        if(f > bound){
            return f;
        }

        if(CompactCube.isSolved(cube)){
            return FOUND;
        }

        int min = Integer.MAX_VALUE;
        int t = 0;
        for(int move = 0; move < CompactCube.NUMMOVES; move++){

            cube.move(move);
            solution.addLast(CompactCube.MoveToString[move]);

            if(!cache.lookupExists(cube)){
                t = search(cube, g + moveCost[move], bound, solution);
                if(t == FOUND){
                    return FOUND;
                }
                if(t < min){
                    min = t;
                }
            }

            cube.move(CompactCube.INV_MOVES[move]);

            //Take off the last thing added
            solution.removeLast();
        }
        return min;
    }

    public static int getH(CompactCube cube){
        int[] values = {cornerArr.getIndex(cube.encodeCorners()),
                        firstEdgeArr.getIndex(cube.encodeFirst()),
                        secondEdgeArr.getIndex(cube.encodeSecond())};

        return Util.max(values);
    }


    public static void generateCornerHeuristics2(File file) throws IOException {
        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();

        //For storing all state move counts of corners. Only need 1 byte since
        //move count will never exceed 11 and byte is smallest unit we can have

        NibbleArray states = new NibbleArray(CompactCube.NO_CORNER_ENCODINGS);

        byte[] state = {0,1,2,3,4,5,6,7};
        workQueue.add(state);
        int count = 1;
        int moveCount = 0;
        int cornerEncoding = 0;
        long usedMemory = 0;
        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            moveCount = states.getIndex(CompactCube.encodeCorners(state));

            for(int move = 0; move < CompactCube.NUMMOVES; move++){


                CompactCube.moveCorners(move, state);

                cornerEncoding = CompactCube.encodeCorners(state);

                //Does not exist already and the encoding is not solved
                if(states.getIndex(cornerEncoding) == 0 && cornerEncoding != 0){
                    count++;
                    if(count % 1000000 == 0){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.gc();

                        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println(count + " of 88179840 "+ "Used mem: "+ usedMemory);
                    }
                    workQueue.add(Arrays.copyOf(state, state.length));

                    states.setIndex(cornerEncoding, (moveCount + moveCost[move]));
                }
                CompactCube.moveCorners(CompactCube.INV_MOVES[move], state);
            }
        }

        System.out.println("Done! Writing to file...");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for(int i = 0; i < states.length; i++){
            writer.write(Integer.toString(states.getIndex(i)) + '\n');
        }
        writer.close();
    }

    public static void generateEdgeHeuristics2(File firstFile, File secondFile) throws IOException {
        NibbleArray firstStates = new NibbleArray(CompactCube.NO_EDGE_ENCODINGS);
        NibbleArray secondStates = new NibbleArray(CompactCube.NO_EDGE_ENCODINGS);

        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();
        byte[] state = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22};

        workQueue.add(state);

        int firstElemCount = 1;
        int secondElemCount = 1;
        int firstMoveCount = 0;
        int secondMoveCount = 0;
        int firstEdgeEncoding = 0;
        int secondEdgeEncoding = 0;
        long usedMemory = 0;
        boolean add = false;
        boolean runGC = false;

        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            firstMoveCount = firstStates.getIndex(CompactCube.encodeFirst(state));
            secondMoveCount = secondStates.getIndex(CompactCube.encodeSecond(state));

            for(int move = 0; move < CompactCube.NUMMOVES; move++){

                CompactCube.moveEdges(move, state);

                firstEdgeEncoding = CompactCube.encodeFirst(state);
                secondEdgeEncoding = CompactCube.encodeSecond(state);

                /*
                 * If the state has not been seen before or we have a move count lower than what
                 * we had before then replace.
                 */
                if((firstStates.getIndex(firstEdgeEncoding) > (firstMoveCount + moveCost[move]) ||
                        firstStates.getIndex(firstEdgeEncoding) == 0)
                        && firstEdgeEncoding != CompactCube.firstEdgeSolved
                        ){
                    add = true;
                    firstElemCount++;
                    if(firstElemCount % 1000000 == 0){
                        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("First:" + firstElemCount + " of 42577920 "+ "Used mem: "+ usedMemory);
                    }
                    firstStates.setIndex(firstEdgeEncoding, (firstMoveCount + moveCost[move]));
                }

                if((secondStates.getIndex(secondEdgeEncoding) > (secondMoveCount + moveCost[move]) ||
                    secondStates.getIndex(secondEdgeEncoding) == 0) &&
                           secondEdgeEncoding != CompactCube.secondEdgeSolved){
                    add = true;
                    secondElemCount++;
                    if(secondElemCount % 1000000 == 0) {
                        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("Second:" + secondElemCount + " of 42577920 " + "Used mem: " + usedMemory);
                    }
                    secondStates.setIndex(secondEdgeEncoding, (secondMoveCount + moveCost[move]));
                }
                /// Add state to workqueue if anything new was seen
                if(add){
                    add = false;
                    workQueue.add(Arrays.copyOf(state, state.length));
                }

                if(runGC){
                    runGC = false;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.gc();
                }

                //Move back
                CompactCube.moveEdges(CompactCube.INV_MOVES[move], state);
            }
        }
        System.out.println("Done! Writing to file...");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(firstFile)));
        for(int i = 0; i < firstStates.length; i++){
            writer.write(Integer.toString(firstStates.getIndex(i)) + '\n');
        }
        writer.close();

        Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(secondFile)));
        for(int i = 0; i < secondStates.length; i++){
            writer2.write(Integer.toString(secondStates.getIndex(i)) + '\n');
        }
        writer2.close();
    }

    /**
     * Class that has 1 bit for each element. 0 if present, 1 if not.
     */
    class Bitset{
        byte[] seen;


        public Bitset(int size){
            seen = new byte[size/8];
        }

        public void setSeen(int index){
            byte offset = (byte) (index % 8);
            byte mask = (byte) (128 >>> offset);
            seen[index/8] = (byte) (seen[index/8] ^ mask);
        }

        public boolean isSeen(int index){
            byte bucket = seen[index/8];
            byte offset = (byte) (index % 8);
            byte mask = (byte) (128 >>> offset);
            byte result = (byte) (seen[index/8] & 0);
            return result != 0;
        }

    }

    public static void main(String[] args) throws IOException {
        //File file = new File(Korfs.CORNERS_FILE_NAME);
        //Korfs.generateCornerHeuristics2(file);

        File file1 = new File(Korfs.FIRST_EDGE_FILE_NAME);
        File file2 = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.generateEdgeHeuristics2(file1, file2);
    }
}
