package com.rubiks.lehoang.rubikssolver.Korfs;

import com.rubiks.lehoang.rubikssolver.Cube;
import com.rubiks.lehoang.rubikssolver.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class Node implements Comparable<Node> {

    /**
     * State of cube at this node
     */
    private String cubeState;

    /**
     * Estimated min moves known to solve this cube state,f
     */
    private int estMoveToGoal;

    /**
     * Number of moves currently done, g
     */
    private int currentMoveCount;

    /**
     * Moves taken to get to this state
     */
    private String sequence;


    public Node(String state, int moveCount, int estMoveToGoal, String sequence){
        cubeState = state;
        currentMoveCount = moveCount;
        this.estMoveToGoal = estMoveToGoal;
        this.sequence = sequence;
    }

    public Node(String state) throws Exception {
        cubeState = state;
        currentMoveCount = 0;
        sequence = "";
        estMoveToGoal = getHeuristic(new Cube(cubeState));
    }

    public List<Node> generateChildren() throws Exception {
        List<Node> children = new ArrayList<Node>();
        for(String move : Cube.moves){
            Cube cube = new Cube(cubeState);
            cube.performSequence(move);

            int estGoal = getHeuristic(cube);

            Node child = new Node(cube.toCompactString(), currentMoveCount+1, currentMoveCount+estGoal, sequence+move);
            children.add(child);
        }

        return children;
    }

    public static int getHeuristic(Cube cube) throws UnsupportedEncodingException {
        int[] heuristics = new int[3];/*
        if(!Korfs.secondEdgeMap.containsKey(cube.encode(Cube.secondEdges))){
            System.out.println(cube.encode(Cube.secondEdges));
        }

        if(!Korfs.firstEdgeMap.containsKey(cube.encode(Cube.firstEdges))){
            System.out.println(cube.encode(Cube.firstEdges));
        }
        heuristics[0] = Korfs.cornerMap.get(cube.encodeCorners());
        heuristics[1] = Korfs.firstEdgeMap.get(cube.encode(Cube.firstEdges));
        heuristics[2] = Korfs.secondEdgeMap.get(cube.encode(Cube.secondEdges));
    */
        int estGoal = Util.max(heuristics);
        return estGoal;
    }

    public boolean isSolved() throws Exception {
        return new Cube(cubeState).isSolved();
    }

    public String getSequence(){
        return sequence;
    }

    public int getCurrentMoveCount(){
        return currentMoveCount;
    }

    public int getEstimateToGoal(){
        return estMoveToGoal;
    }

    @Override
    public boolean equals(Object node) {
        Cube a = null;
        Cube b = null;
        try {
            a = new Cube(cubeState);
            b = new Cube((((Node)node).cubeState));

        } catch (Exception e) {
                e.printStackTrace();
        }
        return a.equals(b);
    }

    @Override
    public int compareTo(Node other) {

        return this.estMoveToGoal < other.estMoveToGoal ? -1 :
                (this.estMoveToGoal > other.estMoveToGoal ? 1 : 0);
    }
}
