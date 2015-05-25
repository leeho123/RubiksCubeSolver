package com.rubiks.lehoang.rubikssolver.Korfs;

import com.carrotsearch.hppc.ByteArrayDeque;
import com.carrotsearch.hppc.ByteDeque;
import com.carrotsearch.hppc.IntArrayDeque;
import com.carrotsearch.hppc.IntDeque;
import com.carrotsearch.hppc.ObjectByteOpenHashMap;
import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    public static String CORNER_TRANSITION_NAME = "cornerTrans.csv";

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

    private static final int FOUND = -1;
    private static final int NOT_FOUND = -2;
    int[] solutionFound = {NOT_FOUND, NOT_FOUND};

    public static int MOVE_ON = -3;

    private static String giveSolution(ByteDeque solution){
        StringBuilder builder = new StringBuilder();
        while(!solution.isEmpty()){
            builder.append(CompactCube.MoveToString[solution.removeFirst() & 0xFF]);
        }
        return builder.toString();
    }

    public static String idaStarKorfs(int maxDepth, CompactCube cube){
        SeenCache cache = new SeenCache(100000);
        return idaStarKorfsSubTree(maxDepth, cube, 0, CompactCube.NUMMOVES, cache);
    }

    public static String idaStarKorfsSubTree(int maxDepth, CompactCube cube, int start, int end, SeenCache cache){
        ByteDeque solution = new ByteArrayDeque();
        int result = 0;
        if(CompactCube.isSolved(cube)){
            return "";
        }

        int bound = getH(cube);
        while(bound < maxDepth){
            System.out.println("Trying depth:" + bound);
            result = search(cube, 0, bound, solution, cache, start, end);
            cache.clear();
            if(result == FOUND){
                return giveSolution(solution);
            }else if(result == NOT_FOUND){
                return null;
            }else{
                bound = result;
            }
        }

        return null;
    }


    public String idaStarMultiKorfs(int maxDepth, CompactCube cube) throws ExecutionException, InterruptedException {
        ExecutorService threadpool = Executors.newFixedThreadPool(2);
        try{

            ByteDeque solution1 = new ByteArrayDeque();
            ByteDeque solution2 = new ByteArrayDeque();

            SeenCache cache = new SeenCache(100000);
            int bound = getH(cube);
            SearchSubTreeTask task1;
            SearchSubTreeTask task2;


            Future<Integer> ans1;
            Future<Integer> ans2;


            int result1 = 0;
            int result2 = 0;


            while(bound < maxDepth) {
                System.out.println("Trying depth:" + bound);
                cache.clear();
                task1 = new SearchSubTreeTask(0, maxDepth, new CompactCube(cube), 0, 9, cache, bound, solution1);
                task2 = new SearchSubTreeTask(1, maxDepth, new CompactCube(cube), 9, 18, cache, bound, solution2);
                ans1 = threadpool.submit(task1);
                ans2 = threadpool.submit(task2);

                synchronized (Korfs.this) {
                    while (true) {
                        if(solutionFound[0] >= 0 || solutionFound[1] >= 0 ||
                                (solutionFound [0] == MOVE_ON && solutionFound[1] == MOVE_ON)){
                            break;
                        }
                        wait();
                    }
                    if (solutionFound[0] >= 0) {
                        ans2.cancel(true);
                        return giveSolution(solution1);
                    } else if(solutionFound[1] >= 0) {
                        ans1.cancel(true);
                        return giveSolution(solution2);
                    } else {
                        solutionFound[0] = NOT_FOUND;
                        solutionFound[1] = NOT_FOUND;
                    }
                }

                result1 = ans1.get();
                result2 = ans2.get();

                if (result1 == FOUND) {
                    return giveSolution(solution1);
                }
                if (result2 == FOUND) {
                    return giveSolution(solution2);
                }

                // Past this point no solutions were found for any
                // Take the next bound
                bound = result1 < result2 ? result1 : result2;
            }
        }finally{
                threadpool.shutdown();
        }
        return null;
    }

    private class SearchSubTreeTask implements Callable<Integer> {

        CompactCube cube;
        int maxDepth;
        int start;
        int end;
        SeenCache cache;
        int bound;
        ByteDeque solution;
        int taskNo;

        public SearchSubTreeTask(int taskNo,int maxDepth, CompactCube cube, int start,
                                 int end, SeenCache cache, int bound,ByteDeque solution){
            this.maxDepth = maxDepth;
            this.cache = cache;
            this.start = start;
            this.end = end;
            this.cube = cube;
            this.bound = bound;
            this.solution = solution;
            this.taskNo = taskNo;
        }

        @Override
        public Integer call() throws Exception {

            int result = search(cube, 0, bound, solution, cache, start,end);
            synchronized (Korfs.this){
                if(result == FOUND){
                    solutionFound[taskNo] = taskNo;
                }else{
                    solutionFound[taskNo] = MOVE_ON;
                }
                Korfs.this.notifyAll();
            }
            return result;
        }
    }
    /**
     * Fixed sized cache
     */
    public static class SeenCache{
        int size;
        ObjectByteOpenHashMap<CompactCube> cache;
        ObjectByteOpenHashMap<CompactCube> cache2;

        public SeenCache(int size){
            this.size = size;
            cache = new ObjectByteOpenHashMap<CompactCube>();
            cache2 = new ObjectByteOpenHashMap<CompactCube>();
        }

        public synchronized void add(CompactCube obj, int val){
            if(cache.containsKey(obj)){
                if(cache.get(obj) > val){
                    cache.put(obj, (byte) val);
                    if(cache.size() >= size/2){
                        cache2.put(obj, (byte) val);
                    }
                }
            }else{
                if(cache.size() >= size){
                    cache.clear();
                    flip();
                }
                cache.put(obj, (byte) val);
            }
        }

        private void flip(){
            ObjectByteOpenHashMap<CompactCube> temp = cache;
            cache = cache2;
            cache2 = temp;
        }

        public void clear(){
            cache.clear();
            cache2.clear();
        }

        public synchronized boolean contains(CompactCube obj, int val){
            return cache.containsKey(obj) && (cache.get(obj)) < val;
        }
    }

    public static String fringeSearchKorfs(int maxDepth, CompactCube cube){
        ByteDeque solution = new ByteArrayDeque();
        SeenCache cache = new SeenCache(100000);
        int result = 0;
        Deque<CompactCube> nowList = new ArrayDeque<CompactCube>();
        IntDeque nowGs = new IntArrayDeque();
        Deque<ByteDeque> nowSolutions = new ArrayDeque<ByteDeque>();


        Deque<CompactCube> laterList = new ArrayDeque<CompactCube>();
        IntDeque laterGs = new IntArrayDeque();
        Deque<ByteDeque> laterSolutions = new ArrayDeque<ByteDeque>();

        nowList.addFirst(cube);
        nowGs.addFirst(0);
        nowSolutions.addFirst(solution);

        int bound = getH(cube);

        Deque<CompactCube> temp;
        IntDeque tempG;
        Deque<ByteDeque> tempS;

        int min;
        while(bound < maxDepth){
            min = Integer.MAX_VALUE;
            System.out.println("Trying depth:" + bound);
            while(!nowList.isEmpty()) {
                solution = nowSolutions.removeFirst();
                result = searchFringe(nowList.removeFirst(), nowGs.removeFirst(), laterList, laterGs, laterSolutions, bound, solution , cache, 0, CompactCube.NUMMOVES);
                if(result == FOUND){
                    return giveSolution(solution);
                }
                if(result < min){
                    min = result;
                }
                //Making it easier for GC to collect
                solution.clear();
                solution = null;
            }
            System.out.println(laterList.size());

            bound = min;

            temp = nowList;
            nowList = laterList;
            laterList = temp;

            tempG = nowGs;
            nowGs = laterGs;
            laterGs = tempG;

            tempS = nowSolutions;
            nowSolutions = laterSolutions;
            laterSolutions = tempS;
        }
        return null;
    }

    private static int searchFringe(CompactCube cube, int g, Deque<CompactCube> later, IntDeque laterGs, Deque<ByteDeque> laterSolutions, int bound, ByteDeque solution,
                              SeenCache cache, int start, int end){
        int f = g + getH(cube);

        boolean isContained = cache.contains(cube, f);
        cache.add(new CompactCube(cube), f);
        if(isContained){
            return Integer.MAX_VALUE;
        }

        if(f > bound && f <= 20){
            later.addFirst(new CompactCube(cube));
            laterGs.addFirst(g);
            laterSolutions.addFirst(new ByteArrayDeque(solution));
            return f;
        }else if (f > 20){
            return Integer.MAX_VALUE;
        }

        if(CompactCube.isSolved(cube)) {
            return FOUND;
        }


        int min = Integer.MAX_VALUE;
        int t = 0;

        BitSet notDone = new BitSet(CompactCube.NUMMOVES);
        notDone.set(0, CompactCube.NUMMOVES);//Set all bits to 1

        //Rule out stupid moves
        if(!solution.isEmpty()) {
            notDone.clear(solution.getLast());
            notDone.clear(CompactCube.INV_MOVES[solution.getLast()]);
        }

        for(int move = start; move < end; move++){
            if(!notDone.get(move)){
                continue;
            }
            notDone.clear(move);

            cube.move(move);
            solution.addLast((byte) move);

            t = searchFringe(cube, g + moveCost[move], later, laterGs, laterSolutions, bound, solution, cache, 0, CompactCube.NUMMOVES);
            if(t == FOUND){
                return FOUND;
            }
            if(t < min){
                min = t;
            }
            cube.move(CompactCube.INV_MOVES[move]);

            solution.removeLast();
        }
        return min;

    }
    private static Random rand = new Random();

    private static int search(CompactCube cube, int g, int bound, ByteDeque solution, SeenCache cache, int start, int end) {
        if(Thread.currentThread().isInterrupted()){
            return NOT_FOUND;
        }
        int f = g + getH(cube);

        boolean isContained = cache.contains(cube, f);
        cache.add(new CompactCube(cube), f);

        if(isContained){
            return Integer.MAX_VALUE;
        }

        if(f > bound){
            return f;
        }

        if(CompactCube.isSolved(cube)){
            return FOUND;
        }

        int min = Integer.MAX_VALUE;
        int t = 0;
        BitSet notDone = new BitSet(CompactCube.NUMMOVES);
        notDone.set(0, CompactCube.NUMMOVES);//Set all bits to 1

        //Rule out stupid moves
        if(!solution.isEmpty()) {
            notDone.clear(solution.getLast());
            notDone.clear(CompactCube.INV_MOVES[solution.getLast()]);
        }

        /**for(int move = rand.nextInt(CompactCube.NUMMOVES);
                    !notDone.isEmpty();
                        move = rand.nextInt(CompactCube.NUMMOVES)){
         **/
        for(int move = start; move < end; move++){
            //If we've already done this move
            if(!notDone.get(move)){
                //move = notDone.nextSetBit(0);
                continue;
            }
            notDone.clear(move);

            cube.move(move);
            solution.addLast((byte) move);

            t = search(cube, g + moveCost[move], bound, solution, cache, 0, CompactCube.NUMMOVES);
            if(t == FOUND){
                return FOUND;
            }
            if(t == NOT_FOUND){
                return NOT_FOUND;
            }
            if(t < min){
                min = t;
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

    public static void generateCornerMoveTable(File file) throws IOException{
        System.out.println("Generating corner transition table");
        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();

        System.out.println("Work queue made");

        int[][] moveTable = new int[CompactCube.NO_CORNER_ENCODINGS][6];

        System.out.println("Set up table");

        int count = 1;
        int[] notFilled = {0,0,0,0,0,0};
        byte[] state = {0,1,2,3,4,5,6,7}; //Solved state
        workQueue.add(state);

        int encoding = 0;
        int cornerEncoding = 0;
        long usedMemory = 0;

        System.out.println("Starting work");

        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            encoding = CompactCube.encodeCorners(state);

            for(int move = 0; move < CompactCube.NUMMOVES; move = move + 3){


                CompactCube.moveCorners(move, state);
                cornerEncoding = CompactCube.encodeCorners(state);


                if( (moveTable[encoding][move/3] == 0 || moveTable[encoding][move/3] == -1) && cornerEncoding != 0){

                    //Array has not yet been filled

                    moveTable[encoding][move/3] = cornerEncoding;

                    if(Arrays.equals(moveTable[cornerEncoding], notFilled)){
                        count++;
                        if(count % 1000000 == 0){
                            System.gc();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            System.out.println(count + " of 88179840 "+ "Used mem: "+ usedMemory);
                        }

                        workQueue.add(Arrays.copyOf(state, state.length));
                        Arrays.fill(moveTable[cornerEncoding], -1);
                    }
                }
                CompactCube.moveCorners(CompactCube.INV_MOVES[move], state);
            }
        }

        StringBuilder  builder = new StringBuilder();
        System.out.println("Done! Writing to file...");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for(int i = 0; i < moveTable.length; i++){
            for(int move = 0; move < moveTable[0].length; move++){
                builder.append(moveTable[i][move]);
                builder.append(" ");
            }
            builder.append("\n");
            writer.write(builder.toString());
            builder.setLength(0);
        }
        writer.close();

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


        //File file1 = new File(Korfs.FIRST_EDGE_FILE_NAME);
        //File file2 = new File(Korfs.SECOND_EDGE_FILE_NAME);
        //Korfs.generateEdgeHeuristics2(file1, file2);


        File transFile = new File(Korfs.CORNER_TRANSITION_NAME);
        Korfs.generateCornerMoveTable(transFile);
    }
}
