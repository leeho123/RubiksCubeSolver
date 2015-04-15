package com.rubiks.lehoang.rubikssolver.Korfs;

import com.rubiks.lehoang.rubikssolver.CompactCube;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
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
    public static void generateCornerHeuristics2(File file) throws IOException {
        Queue<CompactCube> workQueue = new ArrayDeque<CompactCube>();

        //For storing all states of corners
        int[] states = new int[88179840];

        workQueue.add(new CompactCube());
        int count = 1;
        while(!workQueue.isEmpty()){
            CompactCube cube = workQueue.poll();
            int moveCount = states[cube.encodeCorners()];

            for(int move = 0; move < CompactCube.NUMMOVES; move++){

                CompactCube newCube = new CompactCube(cube);
                newCube.move(move);

                int cornerEncoding = newCube.encodeCorners();

                //Does not exist already and the encoding is not solved
                if(states[cornerEncoding] == 0 && cornerEncoding != 0){
                    count++;
                    if(count % 1000000 == 0){
                        System.out.println(count + " of 88179840");
                    }
                    workQueue.add(newCube);

                    states[cornerEncoding] = moveCount + 1;
                }
            }
        }

        System.out.println("Done! Writing to file...");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for(int i = 0; i < states.length; i++){
            writer.write(states[i]);
        }
        writer.close();
    }

    public static void generateEdgeHeuristics2(File firstFile, File secondFile){
        int[] firstStates = new int[42577920];
        int[] secondStates = new int[42577920];


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
            byte mask = (byte) (256 >>> offset);
            seen[index/8] = (byte) (seen[index/8] ^ mask);
        }

        public boolean isSeen(int index){
            byte bucket = seen[index/8];
            byte offset = (byte) (index % 8);
            byte mask = (byte) (256 >>> offset);
            byte result = (byte) (seen[index/8] & 0);
            return result != 0;
        }

    }
}
