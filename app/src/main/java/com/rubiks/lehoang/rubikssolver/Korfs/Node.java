package com.rubiks.lehoang.rubikssolver.Korfs;

import com.rubiks.lehoang.rubikssolver.Cube;
import com.rubiks.lehoang.rubikssolver.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class Node {

    /**
     * State of cube at this node
     */
    private String cubeState;

    /**
     * Estimated min moves known to solve this cube state
     */
    private int estMoveToGoal;

    /**
     * Number of moves currently done, g
     */
    private int currentMoveCount;

    /**
     * Moves taken to get to this state, h
     */
    private String sequence;


    public Node(String state, int moveCount, int estMoveToGoal, String sequence){
        cubeState = state;
        currentMoveCount = moveCount;
        this.estMoveToGoal = estMoveToGoal;
        this.sequence = sequence;
    }

    public List<Node> generateChildren() throws Exception {
        List<Node> children = new ArrayList<Node>();
        for(String move : Cube.moves){
            Cube cube = new Cube(new BufferedReader(new StringReader(cubeState)));
            cube.performSequence(move);

            int[] heuristics = new int[3];
            heuristics[0] = Korfs.cornerMap.get(cube.encodeCorners());
            heuristics[1] = Korfs.firstEdgeMap.get(cube.encodeFirstEdges());
            heuristics[2] = Korfs.secondEdgeMap.get(cube.encodeSecondEdges());

            int estGoal = Util.max(heuristics);

            Node child = new Node(cube.toString(), currentMoveCount+1, estGoal, sequence+move);
            children.add(child);
        }

        return children;
    }






}
