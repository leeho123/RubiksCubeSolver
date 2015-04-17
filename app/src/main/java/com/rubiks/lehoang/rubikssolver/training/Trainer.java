package com.rubiks.lehoang.rubikssolver.training;

import com.carrotsearch.hppc.IntDeque;
import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import java.util.BitSet;

/**
 * Created by LeHoang on 08/04/2015.
 * This class is used to train the program to guess a move count to be used for korfs iterative deepening
 */
public class Trainer {
    public static int FOUND = -1;
    private IntDeque solution;
/*
    private static int search(CompactCube cube, int g, int bound, IntDeque solution, Korfs.SeenCache cache) {

        boolean isContained = cache.contains(cube);
        cache.add(cube);
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
        notDone.set(0, CompactCube.NUMMOVES);
        if(!solution.isEmpty()) {
            notDone.clear(solution.getLast());
            notDone.clear(CompactCube.INV_MOVES[solution.getLast()]);
        }
        for(int move = rand.nextInt(CompactCube.NUMMOVES);
            !notDone.isEmpty();
            move = rand.nextInt(CompactCube.NUMMOVES)){
            //If we've already done this move
            if(!notDone.get(move)){
                move = notDone.nextSetBit(0);
            }
            notDone.clear(move);

            cube.move(move);
            solution.addLast(move);

            t = search(cube, g + moveCost[move], bound, solution, cache);
            if(t == FOUND){
                return FOUND;
            }
            if(t < min){
                min = t;
            }


            cube.move(CompactCube.INV_MOVES[move]);

            //Take off the last thing added
            solution.removeLast();
        }
        return min;
    }*/

}
