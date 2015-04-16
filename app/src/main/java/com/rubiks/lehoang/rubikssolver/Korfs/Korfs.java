package com.rubiks.lehoang.rubikssolver.Korfs;

import com.rubiks.lehoang.rubikssolver.CompactCube;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class Korfs {

    public static String CORNERS_FILE_NAME= "corners.csv";
    public static String FIRST_EDGE_FILE_NAME = "firstEdge.csv";
    public static String SECOND_EDGE_FILE_NAME = "secondEdge.csv";

/*
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
        Node node = new Node(cube.toCompactString());
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
                        workQueue.add(child);
                    }
                }
            }

        }

        return Integer.toString(minOverDepth);
    }
*/

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
    public static void generateCornerHeuristics2(File file) throws IOException {
        Queue<byte[]> workQueue = new ArrayDeque<byte[]>();

        //For storing all state move counts of corners. Only need 1 byte since
        //move count will never exceed 11 and byte is smallest unit we can have

        NibbleArray states = new NibbleArray(88179840);
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

                    states.setIndex(cornerEncoding, (moveCount + 1));
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
        NibbleArray firstStates = new NibbleArray(42577920);
        NibbleArray secondStates = new NibbleArray(42577920);

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
                if((firstStates.getIndex(firstEdgeEncoding) > (firstMoveCount + 1) ||
                        firstStates.getIndex(firstEdgeEncoding) == 0)
                        && firstEdgeEncoding != CompactCube.firstEdgeSolved
                        ){
                    add = true;
                    firstElemCount++;
                    if(firstElemCount % 1000000 == 0){
                        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("First:" + firstElemCount + " of 42577920 "+ "Used mem: "+ usedMemory);
                    }
                    firstStates.setIndex(firstEdgeEncoding, (firstMoveCount + 1));
                }

                if((secondStates.getIndex(secondEdgeEncoding) > (secondMoveCount + 1) ||
                    secondStates.getIndex(secondEdgeEncoding) == 0) &&
                           secondEdgeEncoding != CompactCube.secondEdgeSolved){
                    add = true;
                    secondElemCount++;
                    if(secondElemCount % 1000000 == 0) {
                        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("Second:" + secondElemCount + " of 42577920 " + "Used mem: " + usedMemory);
                    }
                    secondStates.setIndex(secondEdgeEncoding, (secondMoveCount + 1));
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
/*
    public static void generateEdgeHeuristics(File firstFile, File secondFile) throws Exception{
        Map<String, Integer> firstEdgeScoreMap = new HashMap<String, Integer>();
        Map<String, Integer> secondEdgeScoreMap = new HashMap<String, Integer>();

        Set<String> seen = new HashSet<String>();
        Queue<String> workQueue = new ArrayDeque<String>();

        Cube solved = new Cube(Cube.SOLVED_COMPACT);
        workQueue.add(Cube.SOLVED_COMPACT);

        firstEdgeScoreMap.put(solved.encode(Cube.firstEdges), 0);
        secondEdgeScoreMap.put(solved.encode(Cube.secondEdges),0);
        seen.add(solved.encodeAllEdges());

        Writer firstStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(firstFile), StandardCharsets.US_ASCII));
        Writer secondStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(secondFile), StandardCharsets.US_ASCII));
        try {
            StringBuilder secondBuilder = new StringBuilder();
            StringBuilder firstBuilder = new StringBuilder();

            firstBuilder.append(solved.encode(Cube.firstEdges));
            firstBuilder.append(",0\n");

            secondBuilder.append(solved.encode(Cube.secondEdges));
            secondBuilder.append(",0\n");

            firstStream.write(firstBuilder.toString());
            secondStream.write(secondBuilder.toString());

            firstBuilder.setLength(0);
            secondBuilder.setLength(0);

            while (!workQueue.isEmpty()) {
                String state = workQueue.poll();

                for (String move : Cube.moves) {
                    //Create a cube from the state

                    String enc = Cube.quickEncodeAll(state);

                    int prevFirstScore = firstEdgeScoreMap.get(enc.substring(0,6));
                    int prevSecondScore = secondEdgeScoreMap.get(enc.substring(6,12));

                    Cube current = new Cube(state);
                    current.performSequence(move);

                    String encoding = current.encodeAllEdges();

                    //If we've not seen this cube state before
                    if (!seen.contains(encoding)) {
                        workQueue.add(current.toCompactString());
                        seen.add(encoding);
                        String first  = encoding.substring(0,6);
                        String second = encoding.substring(6,12);

                        if(!firstEdgeScoreMap.containsKey(first)){
                            firstEdgeScoreMap.put(first, prevFirstScore+1);
                            firstBuilder.append(first);
                            firstBuilder.append(',');
                            firstBuilder.append(prevFirstScore + 1);
                            firstBuilder.append('\n');
                            firstStream.write(firstBuilder.toString());
                            firstBuilder.setLength(0);
                        }

                        if(!secondEdgeScoreMap.containsKey(second)){
                            secondEdgeScoreMap.put(second, prevSecondScore+1);
                            secondBuilder.append(second);
                            secondBuilder.append(',');
                            secondBuilder.append(prevSecondScore + 1);
                            secondBuilder.append('\n');
                            secondStream.write(secondBuilder.toString());
                            secondBuilder.setLength(0);
                        }

                    }
                }
            }
        }finally {
            firstStream.close();
            secondStream.close();
        }
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
*/
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
