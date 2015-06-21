package com.rubiks.lehoang.rubikssolver.Benchmark;

import com.rubiks.lehoang.rubikssolver.Cube.Cube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;
import com.rubiks.lehoang.rubikssolver.Util;

import org.kociemba.twophase.Search;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by LeHoang on 10/06/2015.
 */
public class Benchmark {
    private static String[] tenMoveScrambles = {"RULFBU2FR3LD3",
                                                "BUD2RLFDBR3F",
                                                "RFU3L2DBF3URD","B3RDR2FU3L2BF3R",
            "B3L3F3L3D3UFB2RD",
            "R3D2R3L3F2RD2U3LF3",
            "U2R2L3D2B2D2B2D3F3D3",
            "U2LF2R2U2D2RB2F2D3",
            "DR3BL2U3L3FBDR3","R3D2FBD3B2L2B2U",
            "RB3R2L3U3LFD3L",
            "BL3B3F3U2BL3F2D3"};

    private static String[] elevenMoveScrambles = {"RULF2DBU2FR3LD3",
            "BUD2RLDFDBR3F",
            "RFU3L2DBF3LURD","B3RB2DR2U3L2BF3RD",
            "U2B3L3F3L3D3UFB2DB2",
            "R3D2R3L3F2RD2U3F3DL3",
            "RU2R2L3D2B2D2B2F3D3F3",
            "U2LF2R2U2D2RB2D3RF",
            "F3DR3BL2U3L3FDR3U","U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3",
            "D2BL3B3F3U2BL3F2D3U"};

    private static String[] twelveMoveScrambles = {"RULFBU2FR3LD3RU",
                                                   "L3F2UBUD2RLDBR3F",
                                                   "B2DRFU3L2DBF3URD","B3RB2DR2FU3L2BF3RD",
            "U2B3L3F3L3D3UFB2RDB2",
            "R3D2R3L3F2RD2U3LF3DL3",
            "RU2R2L3D2B2D2B2D3F3D3F3",
            "U2LF2R2U2D2RB2F2D3RF",
            "F3DR3BL2U3L3FBDR3U","F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2",
            "D2BL3B3F3U2BL3F2D3UF2"};

    private static String[] thirteenMoveScrambles = {"F2RUFLFBU2LFR3LD3",
            "UD3L3F2RUD2RLD2BR3F",
            "B3RB2DR2FU3L2BF3URD","U2B3L3F3L3D3UFB2RDB2L3",
            "R3D2R3L3F2RD2U3LF3DL3B",
            "RU2R2L3D2B2D2B2D3F3D3F3B3",
            "U2LF2R2U2D2RB2F2D3RFU",
            "F3DR3BL2U3L3FBDR3UB2","L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2F",
            "D2BL3B3F3U2BL3F2D3UF2R"};

    private static String[] fourteenMoveScrambles = {"D3L3F2UBRUD2RLDBR3F","F2RUFLFBU2FR3LD3RU",
            "RB2DRFU3L2RDBF3URD","U2B3L3F3L3D3L2UFB2RDB2L3",
            "R3D2R3L3F2RD2U3LF3DL3RB",
            "RU2R2L3D2B2D2B2D3F3D3F3B3D3",
            "U2LF2R2U2D2RB2F2D3RUFU",
            "F3DR3BL2U3L3FBDR3UD2B2","R2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2",
            "D2BL3B3F3U2BL3F2D3UF2RD2"};


    public static String[] fifteenMoveScrambles = {"BF2UFLFBU2LFR3LD3RU",
            "RD3L3UBRUD2FRLDBR3F",
            "LRB2DFU3L2RDBF3BURD","U2L3B3L3F3L3D3L2UFB2RDB2L3",
            "R3D2R3L3F2RD2U3LF3DL3F2LB",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3",
            "U2LF2R2U2D2RB2F2D3RUFLU",
            "F3DR3BL2U3L3FBDR3UL2D2B2","UR2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2L2",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3"};

    public static String[] sixteenMoveScrambles = {"BF2RUFLFBU2LFR3LD3RU",
            "RD3L3F2UBRUD2FRLDBR3F",
            "LRB2DRFU3L2RDBF3BURD","U2L3B3L3F3L3D3L2UFB2RDB2L3U2",
            "R3D2R3L3F2RD2U3LF3DL3F2LBL2",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3R3",
            "U2LF2R2U2D2RB2F2D3RUFLUD",
            "F3DR3BL2U3L3FBDR3UL2D2B2L3","F2UR2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2L2B2",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3D2"};


    public static String[] seventeenMoveScrambles = {"FURLUB3D3F3L3U3L2F3R2D3F2D2L2",
            "F2R2D2L2BR2B3R3B3L3DL3UL3F3RD3",
            "RD3R2F3U3BLD3F3RF2B2U2B2L2R3B2", "U2L3B3L3F3L3D3L2UFB2RDB2L3U2F2",
            "R3D2R3L3F2RD2U3LF3DL3F2LBL2D",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3R3U3",
            "U2LF2R2U2D2RB2F2D3RUFLUDB3",
            "F3DR3BL2U3L3FBDR3UL2D2B2L3B2","R3F2UR2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2L2B2U2",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3D2F2"};

    public static String[] eighteenMoveScrambles = {"FURLUB3D3F3L3U3L2F3R2D3F2D2L2D2",
            "F2R2D2L2BR2B3R3B3L3DL3UL3F3RD3B3",
            "RD3R2F3U3BLD3F3RF2B2U2B2L2R3B2L3", "U2L3B3L3F3L3D3L2UFB2RDB2L3U2F2U2",
            "R3D2R3L3F2RD2U3LF3DL3F2LBL2DL2",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3R3U3L3",
            "U2LF2R2U2D2RB2F2D3RUFLUDB3D",
            "F3DR3BL2U3L3FBDR3UL2D2B2L3B2D2","FR3F2UR2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2L2B2U2L2",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3D2R2F2"};

    public static String[] nineteenMoveScrambles = {"FURLUB3D3F3L3U3L2F3R2D3F2D2L2D2L2",
            "F2R2D2L2BR2B3R3B3L3DL3U3F3RD3B3D2",
            "RD3R2F3U3BLD3F3RF2B2U2B2L2R3B2L3B2", "U2L3B3L3F3L3D3L2UFB2RDB2L3U2F2U2R2",
            "R3D2R3L3F2RD2U3LF3DL3F2LBL2DL2B2",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3R3U3L3F3",
            "U2LF2R2U2D2RB2F2D3RUFLUDB3DF",
            "F3DR3BL2U3L3FBDR3UL2D2B2L3B2D2L3","D3FR3F2UR2L3F3U2R3D2FBD3B2L2B2UR2",
            "B3RB3R2L3U3LFD3LU3R2FD2L2B2U2L2B",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3D2R2F2L2"};

    public static String[] twentyMoveScrambles = {"FURLUB3D3F3L3U3L2F3R2D3F2D2L2D2L2U",
            "F2R2D2L2BR2B3R3B3L3DL3UL3F3RD3B3D2R",
            "RD3R2F3U3BLD3F3RF2B2U2B2L2R3B2L3B2U2", "U2L3B3L3F3L3D3L2UFB2RDB2L3U2F2U2R2D2",
            "R3D2R3L3F2RD2U3LF3DL3F2LBL2DL2B2F3",
            "RU2R2L3D2B2D2B2D3F3D3FR3B3D3R3U3L3F3D3",
            "U2LF2R2U2D2RB2F2D3RUFLUDB3DFL",
            "F3DR3BL2U3L3FBDR3UL2D2B2L3B2D2L3F2","D3FR3F2UR2L3F3U2R3D2FBD3B2L2B2UR2U",
            "B3RB3R2L3U3LFD3LU3R2FD2L2B2U2L2BF3",
            "D2BL3B3F3U2BL3F2D3UF2RD2F3D2R2F2L2B"};

    public static void benchMulti(String[] scrambles) throws Cube.InvalidMoveException, ExecutionException, InterruptedException {
        Korfs korfs = new Korfs();
        String solution = korfs.idaStarMultiKorfs(20, new Cube());
        System.out.println(solution);
        long sum = 0;

        for(String sequence: scrambles){
            Cube cube = new Cube();
            korfs = new Korfs();
            cube.performSequence(sequence);
            long begin = System.nanoTime();
            solution = korfs.idaStarMultiKorfs(20, cube);
            long end = System.nanoTime();
            sum += (end - begin);

            System.out.println(solution + (end-begin));
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }

    public static void benchStock(String[] scrambles) throws Cube.InvalidMoveException{
        String solution = Korfs.idaStarKorfsStock(20, new Cube());
        System.out.println(solution);
        long sum = 0;

        for(String sequence: scrambles){
            Cube cube = new Cube();

            cube.performSequence(sequence);
            long begin = System.nanoTime();
            solution = Korfs.idaStarKorfsStock(20, cube);
            long end = System.nanoTime();
            sum += (end - begin);

            System.out.println(solution + (end-begin));
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }

    public static void benchNorm(String[] scrambles) throws Cube.InvalidMoveException {
        String solution = Korfs.idaStarKorfs(20, new Cube());
        System.out.println(solution);
        long sum = 0;

        for(String sequence: scrambles){
            Cube cube = new Cube();
            cube.performSequence(sequence);
            long begin = System.nanoTime();
            solution = Korfs.idaStarKorfs(20, cube);
            long end = System.nanoTime();

            sum += (end - begin);
            System.out.println(solution + (end-begin));
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }

    public static void benchFringeSingle(String[] scrambles) throws Cube.InvalidMoveException {

        String solution = Korfs.fringeSearchKorfs(20, new Cube());
        System.out.println(solution);

        long sum = 0;


        for(String sequence: scrambles){
            Cube cube = new Cube();
            cube.performSequence(sequence);
            long begin = System.nanoTime();
            solution = Korfs.fringeSearchKorfs(20, cube);
            long end = System.nanoTime();
            sum += (end - begin);

            System.out.println(solution);
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }

    public static void benchFringeSearchMulti(String[] scrambles) throws Cube.InvalidMoveException, ExecutionException, InterruptedException {
        Korfs korfs = new Korfs();
        String solution = korfs.fringeSearchMulti(20, new Cube());
        System.out.println(solution);
        long sum = 0;

        for(String sequence: scrambles){
            Cube cube = new Cube();
            korfs = new Korfs();
            cube.performSequence(sequence);
            long begin = System.nanoTime();
            solution = korfs.fringeSearchMulti(20, cube);
            long end = System.nanoTime();
            sum += (end - begin);

            System.out.println(solution);
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }

    public static void benchKociembas(String[] scrambles) throws Cube.InvalidMoveException {
        long sum = 0;
        Search.solution(Cube.toKociemba(new Cube()),30,5,false);
        for(String sequence: scrambles){
            Cube cube = new Cube();
            cube.performSequence(sequence);
            String cubestate = Cube.toKociemba(cube);
            long begin = System.nanoTime();
            String solution = Search.solution(cubestate,30,5,false);
            long end = System.nanoTime();
            sum += (end - begin);

            System.out.println(solution + (end - begin));
        }

        System.out.println("Average time: " + (sum/(scrambles.length*1000000)) + "Millis");
    }


    public static void countKociembas() throws Cube.InvalidMoveException {
        String[][] scrambles ={tenMoveScrambles, elevenMoveScrambles,twelveMoveScrambles,thirteenMoveScrambles,
                            fourteenMoveScrambles,fifteenMoveScrambles,sixteenMoveScrambles,seventeenMoveScrambles,
        eighteenMoveScrambles,nineteenMoveScrambles,twentyMoveScrambles};

        int sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for(int i =0 ; i < scrambles.length; i++){
            sum = 0;
            min = Integer.MAX_VALUE;
            max= Integer.MIN_VALUE;
            for(int j = 0; j < scrambles[i].length; j++){
                Cube cube = new Cube();
                cube.performSequence(scrambles[i][j]);
                String cubestate = Cube.toKociemba(cube);
                String solution = Search.solution(cubestate,30,5,false);
                int length = Util.countMoves(solution);
                sum+=length;
                if(length > max){
                    max = length;
                }
                if(length < min){
                    min = length;
                }
            }
            System.out.println("Length: " + (10+i));
            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
            System.out.println("Avg: " + ((double)sum/(double)scrambles[i].length));
        }
    }

    public static void main(String[] args) throws IOException, Cube.InvalidMoveException {

        try {
            //benchStock(fourteenMoveScrambles);
            benchNorm(fifteenMoveScrambles);
            // benchMulti(fourteenMoveScrambles);
            //benchFringeSearchMulti(thirteenMoveScrambles);
            //benchFringeSingle(thirteenMoveScrambles);
            //benchKociembas(twentyMoveScrambles);
            //countKociembas();
        } catch (Cube.InvalidMoveException e) {
            e.printStackTrace();
        }
    }

}
