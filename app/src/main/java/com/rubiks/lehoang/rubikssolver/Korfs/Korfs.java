package com.rubiks.lehoang.rubikssolver.Korfs;

import com.carrotsearch.hppc.ByteArrayDeque;
import com.carrotsearch.hppc.ByteDeque;
import com.carrotsearch.hppc.ObjectByteOpenHashMap;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.rubiks.lehoang.rubikssolver.Cube.Cube;
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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
        private byte[] arr;
        private int length;
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

    public static final String CORNERS_FILE_MIN_MOVE = "corners.csv";
    public static final String FIRST_EDGE_MIN_MOVE = "firstEdge.csv";
    public static final String SECOND_EDGE_MIN_MOVE = "secondEdge.csv";

    public static final String CORNERS_FILE_OPT_ROBOT = "CornerFlipped.csv";
    public static final String FIRST_EDGE_OPT_ROBOT = "FirstEdgeFlipped.csv";
    public static final String SECOND_EDGE_OPT_ROBOT = "SecondEdgeFlipped.csv";

    public static final String CORNERS_FILE_NAME = CORNERS_FILE_MIN_MOVE;
    public static final String FIRST_EDGE_FILE_NAME = FIRST_EDGE_MIN_MOVE;
    public static final String SECOND_EDGE_FILE_NAME = SECOND_EDGE_MIN_MOVE;

    public static final String CORNER_TRANSITION_NAME = "cornerTrans.csv";

    public static final NibbleArray cornerArr;
    public static final NibbleArray firstEdgeArr;
    public static final NibbleArray secondEdgeArr;
    public static final int[] moveCost = {1,1,1,
                                    1,1,1,
                                    1,1,1,
                                    1,1,1,
                                    1,1,1,
                                    1,1,1};

    public static final int[][] moveCostFlipped = {{1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3},
                                             {3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1}};


    static{
        System.out.println("Loading pattern DBs");
        //Fill in corner array
        cornerArr = new NibbleArray(Cube.NO_CORNER_ENCODINGS);
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
        firstEdgeArr = new NibbleArray(Cube.NO_EDGE_ENCODINGS);
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
        secondEdgeArr = new NibbleArray(Cube.NO_CORNER_ENCODINGS);
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

    private static final int MOVE_ON = -3;
    private static final int CANCEL = -4;

    private static String giveSolution(ByteDeque solution){
        StringBuilder builder = new StringBuilder();
        while(!solution.isEmpty()){
            builder.append(Cube.MoveToString[solution.removeFirst() & 0xFF]);
        }
        return builder.toString();
    }

    public static String idaStarKorfs(int maxDepth, Cube cube){

        return idaStarKorfsSubTree(maxDepth, cube, 0, Cube.NUMMOVES);
    }

    public static String idaStarKorfsStock(int maxDepth, Cube cube){
        ByteDeque solution = new ByteArrayDeque();
        int result = 0;
        if(Cube.isSolved(cube)){
            return "";
        }

        int bound = getH(cube);
        while(bound < maxDepth){
            //System.out.println("Trying depth:" + bound);
            result = searchStock(cube, 0, bound, solution);
            if(result == FOUND){
                //System.out.println("Found Something");
                return giveSolution(solution);
            }else if(result == NOT_FOUND){
                //System.out.println("Found nothing");
                return null;
            }else{
                bound = result;
            }
        }

        return null;
    }

    public static String idaStarKorfsSubTree(int maxDepth, Cube cube, int start, int end){
        ByteDeque solution = new ByteArrayDeque();
        int result = 0;
        if(Cube.isSolved(cube)){
            return "";
        }

        int bound = getH(cube);
        while(bound < maxDepth){
            //System.out.println("Trying depth:" + bound);
            result = search(cube, 0, bound, solution, start, end);
            if(result == FOUND){
                //System.out.println("Found Something");
                return giveSolution(solution);
            }else if(result == NOT_FOUND){
                //System.out.println("Found nothing");
                return null;
            }else{
                bound = result;
            }
        }

        return null;
    }

    public void cancelMulti(){
        synchronized (Korfs.this){
            solutionFound[0] = CANCEL;
            solutionFound[1] = CANCEL;

            Korfs.this.notifyAll();
        }
    }

    public String idaStarMultiKorfs(int maxDepth, Cube cube) throws ExecutionException, InterruptedException {
        ExecutorService threadpool = Executors.newFixedThreadPool(2);
        solutionFound[0] = NOT_FOUND;
        solutionFound[1] = NOT_FOUND;

        try{

            ByteDeque solution1 = new ByteArrayDeque();
            ByteDeque solution2 = new ByteArrayDeque();

            //SeenCache cache = new SeenCache(100000);
            int bound = getH(cube);
            SearchSubTreeTask task1;
            SearchSubTreeTask task2;


            Future<Integer> ans1;
            Future<Integer> ans2;


            int result1 = 0;
            int result2 = 0;


            while(bound < maxDepth) {
                //System.out.println("Trying depth:" + bound);
                task1 = new SearchSubTreeTask(0, maxDepth, new Cube(cube), 0, 9, bound, solution1);
                task2 = new SearchSubTreeTask(1, maxDepth, new Cube(cube), 9, 18, bound, solution2);
                ans1 = threadpool.submit(task1);
                ans2 = threadpool.submit(task2);

                synchronized (Korfs.this) {
                    while (true) {
                        if(solutionFound[0] >= 0 || solutionFound[1] >= 0 ||
                                (solutionFound [0] == MOVE_ON && solutionFound[1] == MOVE_ON)||
                                (solutionFound[0] == CANCEL && solutionFound[1] == CANCEL)){
                            break;
                        }
                        wait();
                    }
                    //System.out.println("Terminated");
                    if (solutionFound[0] >= 0) {
                        ans2.cancel(true);
                        return giveSolution(solution1);
                    } else if(solutionFound[1] >= 0) {
                        ans1.cancel(true);
                        return giveSolution(solution2);
                    } else if (solutionFound[0] == CANCEL && solutionFound[1] == CANCEL) {
                        ans1.cancel(true);
                        ans2.cancel(true);
                        return null;
                    }else {
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

        private Cube cube;
        private int maxDepth;
        private int start;
        private int end;
        private SeenCache cache;
        private int bound;
        private ByteDeque solution;
        private int taskNo;

        public SearchSubTreeTask(int taskNo,int maxDepth, Cube cube, int start,
                                 int end, int bound,ByteDeque solution){
            this.maxDepth = maxDepth;
            this.start = start;
            this.end = end;
            this.cube = cube;
            this.bound = bound;
            this.solution = solution;
            this.taskNo = taskNo;
        }

        @Override
        public Integer call() throws Exception {
            //System.out.println("Task started");
            int result = search(cube, 0, bound, solution, start,end);
            //System.out.println("Done");
            synchronized (Korfs.this){
                if(result == FOUND){
                    //System.out.println("Found " + taskNo);
                    solutionFound[taskNo] = taskNo;
                }else{
                    //System.out.println("Nothing found " + result);
                    solutionFound[taskNo] = MOVE_ON;
                }
                Korfs.this.notifyAll();
            }
            return result;
        }
    }

    private class SearchSubFringeTask implements Callable<Integer>{
        private Cube cube;
        private int start;
        private int end;
        private int bound;
        private ByteDeque solution;
        private int taskNo;
        private int g;
        private Deque<byte[]> laterSolutions;

        public SearchSubFringeTask(int taskNo, Cube cube, int start, int end,
                                   int bound, ByteDeque solution, Deque<byte[]> laterSolutions){
            this.g = solution.size();
            this.laterSolutions = laterSolutions;
            this.start = start;
            this.end = end;
            this.cube = cube;
            this.bound = bound;
            this.solution = solution;
            this.taskNo = taskNo;
        }
        @Override
        public Integer call() throws Exception {
            int result = searchFringe(cube, g, laterSolutions, bound, solution, start, end);

            synchronized (laterSolutions){
                if(result == FOUND){
                  //  System.out.println("Found " + taskNo);
                    solutionFound[taskNo] = taskNo;
                }else{
                    solutionFound[taskNo] = MOVE_ON;
                }
                laterSolutions.notifyAll();
            }
            return result;
        }
    }

    public String fringeSearchMulti(int maxDepth, Cube cube) throws InterruptedException, ExecutionException {
        ExecutorService threadpool = Executors.newFixedThreadPool(2);
        byte[] edgeBuffer = new byte[12];
        byte[] cornerBuffer = new byte[8];

        cube.copyCornersInto(cornerBuffer);
        cube.copyEdgesInto(edgeBuffer);

        solutionFound[0] = NOT_FOUND;
        solutionFound[1] = NOT_FOUND;

        Deque<byte[]> tempS;

        try {
            ByteDeque solution1 = new ByteArrayDeque();
            ByteDeque solution2 = new ByteArrayDeque();

            int bound = getH(cube);

            SearchSubFringeTask task1;
            SearchSubFringeTask task2;


            Future<Integer> ans1;
            Future<Integer> ans2;


            int result1 = 0;
            int result2 = 0;


            laterSolutions.clear();
            nowSolutions.clear();

            nowSolutions.addFirst(solution1.toArray());

            int min;
            int fullMin;

            Cube cube1 = new Cube();
            Cube cube2 = new Cube();

            byte[] head;
            while (bound < maxDepth) {
                min = Integer.MAX_VALUE;
                fullMin = Integer.MAX_VALUE;

                System.out.println("Trying depth:" + bound);
                while (true) {
                        if (nowSolutions.isEmpty()) {
                            break;
                        }
                        try {
                            head = nowSolutions.remove();
                            solution1.clear();
                            solution2.clear();
                            for (byte move : head) {
                                cube.move(move);
                                solution1.addLast(move);
                                solution2.addLast(move);
                            }
                        }catch(NoSuchElementException e){
                            e.printStackTrace();

                            System.out.println(nowSolutions.size());
                            System.out.println(Arrays.toString(nowSolutions.toArray()));
                            throw e;
                        }


                    cube1.resetStateTo(cube);
                    cube2.resetStateTo(cube);
                    task1 = new SearchSubFringeTask(0, cube1, 0, 9, bound, solution1, laterSolutions);
                    task2 = new SearchSubFringeTask(1, cube2, 9, 18, bound, solution2, laterSolutions);
                    ans1 = threadpool.submit(task1);
                    ans2 = threadpool.submit(task2);

                    synchronized (laterSolutions) {
                        while (true) {
                            if (solutionFound[0] >= 0 || solutionFound[1] >= 0 ||
                                    (solutionFound[0] == MOVE_ON && solutionFound[1] == MOVE_ON)) {
                                break;
                            }
                            laterSolutions.wait();
                        }
                        //System.out.println("Terminated depth" + bound);
                        if (solutionFound[0] >= 0) {
                            ans2.cancel(true);
                            return giveSolution(solution1);
                        } else if (solutionFound[1] >= 0) {
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



                    min = result1 < result2 ? result1 : result2;


                    fullMin = fullMin < min ? fullMin : min;

                    cube.resetStateTo(cornerBuffer, edgeBuffer);
                }

                // Past this point no solutions were found for any
                // Take the next bound
                bound = fullMin;

                synchronized (laterSolutions) {
                    tempS = nowSolutions;
                    nowSolutions = laterSolutions;
                    laterSolutions = tempS;
                }
            }
        }finally{
            threadpool.shutdown();
        }


        return null;
    }

    /**
     * Fixed sized cache
     */
    public static class SeenCache{
        int size;
        ObjectByteOpenHashMap<Cube> cache;
        ObjectByteOpenHashMap<Cube> cache2;

        public SeenCache(int size){
            this.size = size;
            cache = new ObjectByteOpenHashMap<Cube>();
            cache2 = new ObjectByteOpenHashMap<Cube>();
        }

        public void add(Cube obj, int val){
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
            ObjectByteOpenHashMap<Cube> temp = cache;
            cache = cache2;
            cache2 = temp;
        }

        public void clear(){
            cache.clear();
            cache2.clear();
        }

        public boolean contains(Cube obj, int val){
            return cache.containsKey(obj) && (cache.get(obj)) < val;
        }
    }

    static Deque<byte[]> nowSolutions = new ArrayDeque<byte[]>(50000000);
    static Deque<byte[]> laterSolutions = new ArrayDeque<byte[]>(50000000);

    public static String fringeSearchKorfs(int maxDepth, Cube cube){
        byte[] edgeBuffer = new byte[12];
        byte[] cornerBuffer = new byte[8];

        cube.copyCornersInto(cornerBuffer);
        cube.copyEdgesInto(edgeBuffer);

        ByteDeque solution = new ByteArrayDeque();
        int result = 0;

        nowSolutions.clear();
        laterSolutions.clear();


        nowSolutions.add(solution.toArray());

        int bound = getH(cube);

        Deque<byte[]> tempS;
        int min;
        byte[] head;
        while(bound < maxDepth){
            min = Integer.MAX_VALUE;
            //System.out.println("Trying depth:" + bound);
            while(!nowSolutions.isEmpty()) {
                head = nowSolutions.remove();
                solution.clear();
                for(byte move: head){
                    cube.move(move);
                    solution.addLast(move);
                }

                result = searchFringe(cube, solution.size(), laterSolutions, bound, solution , 0, Cube.NUMMOVES);

                if(result == FOUND){
                    return giveSolution(solution);
                }
                if(result < min){
                    min = result;
                }

                //Undo moves
                cube.resetStateTo(cornerBuffer, edgeBuffer);

                //Making it easier for GC to collect
                //solution.clear();
                //solution = null;
            }
           //System.out.println(laterSolutions.size());

            bound = min;

            tempS = nowSolutions;
            nowSolutions = laterSolutions;
            laterSolutions = tempS;
        }
        return null;
    }

    private static int searchFringe(Cube cube, int g , Deque<byte[]> laterSolutions, int bound, ByteDeque solution,
                               int start, int end){
        int f = g + getH(cube);

        if(Cube.isSolved(cube)) {
            return FOUND;
        }

        if(f > bound && f <= 20){
            synchronized (laterSolutions) {
                byte[] array = solution.toArray();
                laterSolutions.add(array);
            }
            return f;
        }else if (f > 20){
            return Integer.MAX_VALUE;
        }

        int min = Integer.MAX_VALUE;
        int t = 0;

        BitSet notDone = new BitSet(Cube.NUMMOVES);
        notDone.set(0, Cube.NUMMOVES);//Set all bits to 1

        //Rule out stupid moves
        if(!solution.isEmpty()) {

            Iterator<ByteCursor> backwardsIt = solution.descendingIterator();

            int prev = (backwardsIt.next().value/3) * 3;
            for(int i = 0; i < 3; i++){
                notDone.clear(prev + i);
            }

            int val;

            while(backwardsIt.hasNext()){
                val = (backwardsIt.next().value/3) * 3;

                //Test to see if move is on opposite face
                if(val == ((((prev + 9)%18)))){
                    //If it is then don't generate any moves for the opposite either
                    for(int i = 0; i < 3; i++){
                        notDone.clear(val + i);
                    }
                    prev = val;
                }else{
                    break;
                }
            }
        }

        for(int move = start; move < end; move++){
            if(!notDone.get(move)){
                continue;
            }
            notDone.clear(move);

            cube.move(move);
            solution.addLast((byte) move);

            t = searchFringe(cube, g + moveCost[move], laterSolutions, bound, solution, 0, Cube.NUMMOVES);
            if(t == FOUND){
                return FOUND;
            }
            if(t < min){
                min = t;
            }
            cube.move(Cube.INV_MOVES[move]);

            solution.removeLast();
        }
        return min;
    }



    private static Random rand = new Random();


    private static int search(Cube cube, int g, int bound, ByteDeque solution, int start, int end) {
        //System.out.println("I'm actually doing something");
        if(Thread.currentThread().isInterrupted()){
            //System.out.println("Task Interrupted");
            return NOT_FOUND;
        }

        if(Cube.isSolved(cube)){
            return FOUND;
        }

        int h = getH(cube);
        int f = g + h;

        //boolean isContained = cache.contains(cube, f);
        //cache.add(new CompactCube(cube), f);

        //if(isContained){
          //  return Integer.MAX_VALUE;
        //}

        if(f > bound){
            //System.out.println("Exceeded bound");
            return f;
        }

        int min = Integer.MAX_VALUE;
        int t = 0;
        BitSet notDone = new BitSet(Cube.NUMMOVES);
        notDone.set(0, Cube.NUMMOVES);//Set all bits to 1

        if(start > 0){
            for(int i = 0; i < start; i++) {
                notDone.clear(i);
            }
        }

        if(end < Cube.NUMMOVES){
            for(int i = end; i < Cube.NUMMOVES; i++){
                notDone.clear(i);
            }
        }


        //Rule out stupid moves
        if(!solution.isEmpty()) {

            Iterator<ByteCursor> backwardsIt = solution.descendingIterator();

            int prev = (backwardsIt.next().value/3) * 3;
            for(int i = 0; i < 3; i++){
                notDone.clear(prev + i);
            }

            int val;

            while(backwardsIt.hasNext()){
                 val = (backwardsIt.next().value/3) * 3;

                //Test to see if move is on opposite face
                 if(val == ((((prev + 9)%18)))){
                     //If it is then don't generate any moves for the opposite either
                     for(int i = 0; i < 3; i++){
                         notDone.clear(val + i);
                     }
                     prev = val;
                 }else{
                     break;
                 }
            }

        }

        //for(int move = rand.nextInt(CompactCube.NUMMOVES);
          //         !notDone.isEmpty();
//                        move = rand.nextInt(CompactCube.NUMMOVES)){


        for(int move = start; move < end; move++){

            //If we've already done this move
            if(!notDone.get(move)){
                //move = notDone.nextSetBit(0);
                continue;
            }
            notDone.clear(move);

            cube.move(move);

            solution.addLast((byte) move);

            t = search(cube, g + moveCost[move], bound, solution, 0, Cube.NUMMOVES);
            if(t == FOUND){
                return FOUND;
            }
            if(t == NOT_FOUND){
                return NOT_FOUND;
            }
            if(t < min){
                min = t;
            }

            cube.move(Cube.INV_MOVES[move]);
            //Take off the last thing added
            solution.removeLast();
        }
        return min;
    }

    private static int searchStock(Cube cube, int g, int bound, ByteDeque solution) {


        if(Cube.isSolved(cube)){
            return FOUND;
        }

        int h = getH(cube);
        int f = g + h;



        if(f > bound){
            //System.out.println("Exceeded bound");
            return f;
        }

        int min = Integer.MAX_VALUE;


        int t = 0;
        for(int move = 0; move < Cube.NUMMOVES; move++){


            cube.move(move);

            solution.addLast((byte) move);

            t = searchStock(cube, g + moveCost[move], bound, solution);
            if(t == FOUND){
                return FOUND;
            }
            if(t == NOT_FOUND){
                return NOT_FOUND;
            }
            if(t < min){
                min = t;
            }

            cube.move(Cube.INV_MOVES[move]);
            //Take off the last thing added
            solution.removeLast();
        }
        return min;
    }

    public static int getH(Cube cube){
        int[] values = {cornerArr.getIndex(cube.encodeCorners()),
                        firstEdgeArr.getIndex(cube.encodeFirst()),
                        secondEdgeArr.getIndex(cube.encodeSecond())};

        return Util.max(values);
    }

    public static void generateCornerMoveTable(File file) throws IOException{
        System.out.println("Generating corner transition table");
        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();

        System.out.println("Work queue made");

        int[][] moveTable = new int[Cube.NO_CORNER_ENCODINGS][6];

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
            encoding = Cube.encodeCorners(state, 8);

            for(int move = 0; move < Cube.NUMMOVES; move = move + 3){


                Cube.moveCorners(move, state);
                cornerEncoding = Cube.encodeCorners(state, 8);


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
                Cube.moveCorners(Cube.INV_MOVES[move], state);
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

        NibbleArray states = new NibbleArray(Cube.NO_CORNER_ENCODINGS);

        byte[] state = {0,1,2,3,4,5,6,7};
        workQueue.add(state);
        int count = 1;
        int moveCount = 0;
        int cornerEncoding = 0;
        long usedMemory = 0;
        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            moveCount = states.getIndex(Cube.encodeCorners(state, 8));

            for(int move = 0; move < Cube.NUMMOVES; move++){
                Cube.moveCorners(move, state);

                cornerEncoding = Cube.encodeCorners(state, 8);

                //Does not exist already and the encoding is not solved
                if((states.getIndex(cornerEncoding) == 0 ||
                        states.getIndex(cornerEncoding) > (moveCount + moveCost[move]))&&
                        cornerEncoding != 0){
                    if(states.getIndex(cornerEncoding) == 0) {
                        count++;
                        if (count % 1000000 == 0) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.gc();

                            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            System.out.println(count + " of 88179840 " + "Used mem: " + usedMemory);
                        }
                    }
                    workQueue.add(Arrays.copyOf(state, state.length));

                    states.setIndex(cornerEncoding, (moveCount + moveCost[move]));
                }
                Cube.moveCorners(Cube.INV_MOVES[move], state);
            }
        }

        System.out.println("Done! Writing to file...");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for(int i = 0; i < states.length; i++){
            writer.write(Integer.toString(states.getIndex(i)) + '\n');
        }
        writer.close();
    }

    public static void generateCornerHeuristics(File file) throws IOException {
        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();

        //For storing all state move counts of corners. Only need 1 byte since
        //move count will never exceed 11 and byte is smallest unit we can have

        NibbleArray states = new NibbleArray(Cube.NO_CORNER_ENCODINGS);

        byte[] state = {0,1,2,3,4,5,6,7,0};
        workQueue.add(state);
        int count = 1;
        int moveCount = 0;
        int cornerEncoding = 0;
        long usedMemory = 0;
        byte prevState;

        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            moveCount = states.getIndex(Cube.encodeCorners(state, 8));

            prevState = state[state.length-1];
            for(int move = 0; move < Cube.NUMMOVES; move++){
                Cube.moveCorners(move, state);

                if((move/3)*3 == Cube.B || (move/3)*3 == Cube.F){
                    if(state[state.length-1] == 1){
                        state[state.length-1] = 0;
                    }
                }

                if((move/3)*3 == Cube.U || (move/3)*3 == Cube.D){
                    if(state[state.length-1] == 0){
                        state[state.length-1] = 1;
                    }
                }
                cornerEncoding = Cube.encodeCorners(state, 8);

                //Does not exist already and the encoding is not solved
                if((states.getIndex(cornerEncoding) == 0 ||
                        states.getIndex(cornerEncoding) > (moveCount + moveCostFlipped[state[state.length-1]][move]))
                        && cornerEncoding != 0){

                    if(states.getIndex(cornerEncoding) == 0) {
                        count++;
                        if (count % 1000000 == 0) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.gc();

                            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            System.out.println(count + " of 88179840 " + "Used mem: " + usedMemory);
                        }
                    }
                    workQueue.add(Arrays.copyOf(state, state.length));
                    states.setIndex(cornerEncoding, (moveCount + moveCostFlipped[state[state.length-1]][move]));
                }
                Cube.moveCorners(Cube.INV_MOVES[move], state);
                state[state.length-1] = prevState;
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
        NibbleArray firstStates = new NibbleArray(Cube.NO_EDGE_ENCODINGS);
        NibbleArray secondStates = new NibbleArray(Cube.NO_EDGE_ENCODINGS);

        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();
        byte[] state = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22,0};

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
        //byte prevState;

        while(!workQueue.isEmpty()){
            state = workQueue.poll();
            firstMoveCount = firstStates.getIndex(Cube.encodeFirst(state));
            secondMoveCount = secondStates.getIndex(Cube.encodeSecond(state));
            //prevState = state[state.length-1];

            for(int move = 0; move < Cube.NUMMOVES; move++){
                /*
                if((move/3)*3 == CompactCube.B || (move/3)*3 == CompactCube.F){
                    if(state[state.length-1] == 1){
                        state[state.length-1] = 0;
                    }
                }

                if((move/3)*3 == CompactCube.U || (move/3)*3 == CompactCube.D){
                    if(state[state.length-1] == 0){
                        state[state.length-1] = 1;
                    }
                }*/
                Cube.moveEdges(move, state);

                firstEdgeEncoding = Cube.encodeFirst(state);
                secondEdgeEncoding = Cube.encodeSecond(state);

                /*
                 * If the state has not been seen before or we have a move count lower than what
                 * we had before then replace.
                 */
                if((firstStates.getIndex(firstEdgeEncoding) > (firstMoveCount + moveCostFlipped[state[state.length-1]][move]) ||
                        firstStates.getIndex(firstEdgeEncoding) == 0)
                        && firstEdgeEncoding != Cube.firstEdgeSolved
                        ){
                    add = true;

                    if( firstStates.getIndex(firstEdgeEncoding) == 0) {
                        firstElemCount++;
                        if (firstElemCount % 1000000 == 0) {
                            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            System.out.println("First:" + firstElemCount + " of 42577920 " + "Used mem: " + usedMemory);
                        }
                    }
                    firstStates.setIndex(firstEdgeEncoding, (firstMoveCount + moveCost[move]));
                }

                if((secondStates.getIndex(secondEdgeEncoding) > (secondMoveCount + moveCost[move]) ||
                    secondStates.getIndex(secondEdgeEncoding) == 0) &&
                           secondEdgeEncoding != Cube.secondEdgeSolved){
                    add = true;
                    if(secondStates.getIndex(secondEdgeEncoding) == 0) {
                        secondElemCount++;
                        if (secondElemCount % 1000000 == 0) {
                            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            System.out.println("Second:" + secondElemCount + " of 42577920 " + "Used mem: " + usedMemory);
                        }
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
                Cube.moveEdges(Cube.INV_MOVES[move], state);
                //state[state.length-1] = prevState;

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


    public static void main(String[] args) throws IOException {
        File file = new File(CORNERS_FILE_NAME);
        Korfs.generateCornerHeuristics2(file);

        //File file1 = new File("FirstEdgeFlipped.csv");
        //File file2 = new File("SecondEdgeFlipped.csv");
        //Korfs.generateEdgeHeuristics2(file1, file2);


        //File transFile = new File(Korfs.CORNER_TRANSITION_NAME);
        //Korfs.generateCornerMoveTable(transFile);
    }

    /*

    private static int searchFlipped(CompactCube cube, int g, int bound, ByteDeque solution, SeenCache cache, int start, int end) {
        //System.out.println("I'm actually doing something");
        if(Thread.currentThread().isInterrupted()){
            System.out.println("Task Interrupted");
            return NOT_FOUND;
        }

        if(CompactCube.isSolved(cube)){
            return FOUND;
        }

        int h = getH(cube);
        int f = g + h;

        //boolean isContained = cache.contains(cube, f);
        //cache.add(new CompactCube(cube), f);

        //if(isContained){
        //  return Integer.MAX_VALUE;
        //}

        if(f > bound){
            //System.out.println("Exceeded bound");
            return f;
        }

        int min = Integer.MAX_VALUE;
        int t = 0;
        BitSet notDone = new BitSet(CompactCube.NUMMOVES);
        notDone.set(0, CompactCube.NUMMOVES);//Set all bits to 1

        //Rule out stupid moves
        if(!solution.isEmpty()) {

            Iterator<ByteCursor> backwardsIt = solution.descendingIterator();

            int prev = (backwardsIt.next().value/3) * 3;
            for(int i = 0; i < 3; i++){
                notDone.clear(prev + i);
            }

            int val;

            while(backwardsIt.hasNext()){
                val = (backwardsIt.next().value/3) * 3;

                //Test to see if move is on opposite face
                if(val == ((((prev + 9)%18)))){
                    //If it is then don't generate any moves for the opposite either
                    for(int i = 0; i < 3; i++){
                        notDone.clear(val + i);
                    }
                    prev = val;
                }else{
                    break;
                }
            }

        }

        boolean prevFlipped = cube.isFlipped();

        for(int move = start; move < end; move++){

            //If we've already done this move
            if(!notDone.get(move)){
                //move = notDone.nextSetBit(0);
                continue;
            }
            notDone.clear(move);

            cube.move(move);

            if((move/3)*3 == CompactCube.B || (move/3)*3 == CompactCube.F){
                if(cube.isFlipped()){
                    cube.setFlipped(false);
                }
            }

            if((move/3)*3 == CompactCube.U || (move/3)*3 == CompactCube.D){
                if(!cube.isFlipped()){
                    cube.setFlipped(true);
                }
            }

            solution.addLast((byte) move);

            t = search(cube, g + moveCostFlipped[cube.isFlipped() ? 1 : 0][move], bound, solution, cache, 0, CompactCube.NUMMOVES);
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
            cube.setFlipped(prevFlipped);
            //Take off the last thing added
            solution.removeLast();
        }
        return min;
    }*/

    public static int estimateCost(String sequence) throws Cube.InvalidMoveException {
        int sum = 0;
        boolean flipped = false;
        List<Integer> moves = Cube.convertToList(sequence);
        for(Integer move : moves){
            sum += moveCostFlipped[flipped?1:0][move];
            int firstMove = (move/3) * 3;
            flipped = (!flipped && (firstMove == Cube.U || firstMove == Cube.D)? true :
                    (flipped&& (firstMove == Cube.B || firstMove == Cube.F) ? false : flipped));

        }
        return sum;
    }
}
