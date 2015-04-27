package com.rubiks.lehoang.rubikssolver.training;

import com.carrotsearch.hppc.IntDeque;
import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;


import org.kociemba.twophase.Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.Random;

/**
 * Created by LeHoang on 08/04/2015.
 * This class is used to train the program to guess a move count lower and upper bound to be used for korf fringe search
 */
public class Trainer {
    enum Face{U1,U2,U3,F1,F2,F3,R1,R2,R3,
        D1,D2,D3,B1,B2,B3,L1,L2,L3;}
    public static int[][] validMoves = {{1,2,4,5},
                                        {0,2,3,5},
                                        {0,1,3,4},
                                        {1,2,4,5},
                                        {0,2,3,5},
                                        {0,1,3,4}};
    public static Random rand = new Random();
    public static int[] generateRandomScramble(int length){

        int[] moves = new int[length];
        moves[0] = rand.nextInt(CompactCube.NUMMOVES);
        for(int i = 1; i < length; i++){
            moves[i] = (validMoves[moves[i-1]/3][rand.nextInt(4)]*3) + (rand.nextInt(3));
        }

        return moves;
    }
    public static int[] Load20KnownDepth20(){
        BufferedReader stream;

        int[] counts = new int[50];

        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream("htm.txt")));
            String readLine = stream.readLine();
            int[] moves;
            CompactCube cube = new CompactCube();
            String solution;
            while(readLine != null){
                moves = convertFromStringToMoves(readLine);

                //Move the cube
                for(int move: moves){
                    cube.move(move);
                }

                solution = Search.solution(CompactCube.toKociemba(cube), 21, 20, false);
                System.out.println("Solution was..." + solution);
                counts[countMoves(solution)]++;
                cube.reset();
                readLine = stream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public static int countMoves(String solution) {
        char current;
        int result = 0;
        for(int i = solution.length()-1; i >=0; i--){
            current = solution.charAt(i);
            switch(current){
                case '2': result++; break;
                case '\'': continue;
                case 'U':
                case 'F':
                case 'R':
                case 'D':
                case 'B':
                case 'L':
                    result++; break;
                default:
                    continue;

            }
        }
        return result;
    }

    public static int[] convertFromStringToMoves(String moves){
        int[] result = new int[moves.length()/2];
        for(int i = 0; i < moves.length(); i+=2){
            result[i/2] = Face.valueOf(moves.substring(i, i+2)).ordinal();
        }
        return result;
    }



}
