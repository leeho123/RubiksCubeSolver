package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.Cube;
import com.rubiks.lehoang.rubikssolver.Util;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.kociemba.twophase.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by LeHoang on 08/04/2015.
 */
public class KociembaTest extends TestCase {
    /*B = orange
    F = red
    U = blue
    D = green
    L = white
    R = yellow
    */


    public void testKociembaConversionSimple(){

        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected = "UUUUUUUUU" +
                              "RRRRRRRRR" +
                              "FFFFFFFFF" +
                              "DDDDDDDDD" +
                              "LLLLLLLLL" +
                              "BBBBBBBBB";

        setupConfig(config, expected);
    }

    public void testKociembaConversionComplex(){
        String config =
                "Top\nO Y Y\nO B O\nG R B\n" +
                        "Bottom\nY R B\nY G W\nO R W\n" +
                        "Front\nR B Y\nR R B\nG Y R\n" +
                        "Right\nB G O\nO Y O\nR G G\n"+
                        "Left\nB W Y\nB W G\nG G W\n"+
                        "Back\nW B R\nW O Y\nO W W\n";
        String expected = "BRRBUBDFU" +
                          "BDUBRBDDF" +
                          "FURFFUDRF" +
                          "BFLRDLRFU" +
                          "ULRULDDDL" +
                          "FULRBLLLB";
        setupConfig(config, expected);
    }

    public void testSolutionSimple() throws Exception {

        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nY Y Y\nR R R\nR R R\n" +
                        "Right\nO O O\nY Y Y\nY Y Y\n"+
                        "Left\nR R R\nW W W\nW W W\n"+
                        "Back\nW W W\nO O O\nO O O\n";

        Cube cube = new Cube(new BufferedReader(new StringReader(config)));

        String solution = Util.solveCubeUsingKociemba(cube);
        cube.performSequence(solution);

        Assert.assertEquals(true, cube.isSolved());
    }

    public void testSolutionMid() throws Exception {
        String config =
                "Top\nB B B\nB B B\nR R W\n" +
                        "Bottom\nG G G\nG G G\nG G Y\n" +
                        "Front\nY Y B\nR R B\nR R R\n" +
                        "Right\nY Y O\nY Y O\nY Y B\n"+
                        "Left\nR R G\nW W W\nW W W\n"+
                        "Back\nW W O\nO O O\nO O O\n";

        Cube cube = new Cube(new BufferedReader(new StringReader(config)));

        String solution = Util.solveCubeUsingKociemba(cube);
        cube.performSequence(solution);
        Assert.assertEquals(true, cube.isSolved());
    }

    public void testSolutionComplex() throws Exception {
        String config =
                "Top\nB O G\nW B O\nO B O\n" +
                        "Bottom\nB B Y\nG G R\nY G G\n" +
                        "Front\nW W Y\nY R R\nO Y R\n" +
                        "Right\nR B G\nR Y Y\nR B Y\n"+
                        "Left\nR O G\nR W O\nO W B\n"+
                        "Back\nW G W\nW O G\nW Y B\n";
        Cube cube = new Cube(new BufferedReader(new StringReader(config)));

        String solution = Util.solveCubeUsingKociemba(cube);
        cube.performSequence(solution);
        System.out.println(solution);
        Assert.assertEquals(true, cube.isSolved());

    }



    public void setupConfig(String myCube, String expectedK){
        Cube cube = null;
        String actual = null;
        try {
            cube = new Cube(new BufferedReader(new StringReader(myCube)));
            actual = Util.myCubeToKociemba(cube);



        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(expectedK, actual);
    }
}
