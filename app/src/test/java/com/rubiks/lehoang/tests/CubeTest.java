package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.Cube;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.robolectric.shadows.ShadowLog;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Created by LeHoang on 07/04/2015.
 */
public class CubeTest extends TestCase {

    @Before
    public void setUp(){
        ShadowLog.stream = System.out;
    }


    public void testCubeRightTurnFromSolved(){
        String config =
        "Top\nB B B\nB B B\nB B B\n" +
        "Bottom\nG G G\nG G G\nG G G\n" +
        "Front\nR R R\nR R R\nR R R\n" +
        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
        "Left\nW W W\nW W W\nW W W\n"+
        "Back\nO O O\nO O O\nO O O\n";


        String expected =
        "Top\nB B R\nB B R\nB B R\n"+
        "Bottom\nG G O\nG G O\nG G O\n"+
        "Front\nR R G\nR R G\nR R G\n"+
        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
        "Left\nW W W\nW W W\nW W W\n"+
        "Back\nO O B\nO O B\nO O B\n";

        setConfig(config, expected, "R");

    }


    public void testCubeLeftTurnFromSolved(){
        String config =
        "Top\nB B B\nB B B\nB B B\n" +
        "Bottom\nG G G\nG G G\nG G G\n" +
        "Front\nR R R\nR R R\nR R R\n" +
        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
        "Left\nW W W\nW W W\nW W W\n"+
        "Back\nO O O\nO O O\nO O O\n";

        String expected =
        "Top\nO B B\nO B B\nO B B\n" +
        "Bottom\nR G G\nR G G\nR G G\n" +
        "Front\nB R R\nB R R\nB R R\n" +
        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
        "Left\nW W W\nW W W\nW W W\n"+
        "Back\nG O O\nG O O\nG O O\n";

        setConfig(config, expected, "L");
    }


    public void testCubeTopTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nY Y Y\nR R R\nR R R\n" +
                        "Right\nO O O\nY Y Y\nY Y Y\n"+
                        "Left\nR R R\nW W W\nW W W\n"+
                        "Back\nW W W\nO O O\nO O O\n";

        setConfig(config, expected, "U");
    }

    public void testCubeDownTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nW W W\n" +
                        "Right\nY Y Y\nY Y Y\nR R R\n"+
                        "Left\nW W W\nW W W\nO O O\n"+
                        "Back\nO O O\nO O O\nY Y Y\n";

        setConfig(config, expected, "D");
    }

    public void testCubeBackTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nY Y Y\nB B B\nB B B\n" +
                        "Bottom\nW W W\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nG Y Y\nG Y Y\nG Y Y\n"+
                        "Left\nB W W\nB W W\nB W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        setConfig(config, expected, "B");
    }

    public void testCubeFrontTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nW W W\n" +
                        "Bottom\nG G G\nG G G\nY Y Y\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y B\nY Y B\nY Y B\n"+
                        "Left\nW W G\nW W G\nW W G\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        setConfig(config, expected, "F");
    }

    public void testCubeRightPrimeFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";


        String expected =
                "Top\nB B O\nB B O\nB B O\n"+
                        "Bottom\nG G R\nG G R\nG G R\n"+
                        "Front\nR R B\nR R B\nR R B\n"+
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O G\nO O G\nO O G\n";

        setConfig(config, expected, "R'");

    }


    public void testCubeLeftPrimeTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nR B B\nR B B\nR B B\n" +
                        "Bottom\nO G G\nO G G\nO G G\n" +
                        "Front\nG R R\nG R R\nG R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nB O O\nB O O\nB O O\n";

        setConfig(config, expected, "L'");
    }

    public void testCubeTopPrimeTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nW W W\nR R R\nR R R\n" +
                        "Right\nR R R\nY Y Y\nY Y Y\n"+
                        "Left\nO O O\nW W W\nW W W\n"+
                        "Back\nY Y Y\nO O O\nO O O\n";

        setConfig(config, expected, "U'");
    }

    public void testCubeDownPrimeTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nY Y Y\n" +
                        "Right\nY Y Y\nY Y Y\nO O O\n"+
                        "Left\nW W W\nW W W\nR R R\n"+
                        "Back\nO O O\nO O O\nW W W\n";

        setConfig(config, expected, "D'");
    }

    public void testCubeBackPrimeTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nW W W\nB B B\nB B B\n" +
                        "Bottom\nY Y Y\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nB Y Y\nB Y Y\nB Y Y\n"+
                        "Left\nG W W\nG W W\nG W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        setConfig(config, expected, "B'");
    }

    public void testCubeFrontPrimeTurnFromSolved(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nB B B\nB B B\nY Y Y\n" +
                        "Bottom\nG G G\nG G G\nW W W\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y G\nY Y G\nY Y G\n"+
                        "Left\nW W B\nW W B\nW W B\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        setConfig(config, expected, "F'");
    }

    public void testSequenceOfMovesSimple(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nY Y B\nB B B\nW W G\n" +
                        "Bottom\nR W O\nG G O\nG G B\n" +
                        "Front\nR R Y\nR R Y\nW W Y\n" +
                        "Right\nO Y R\nG Y R\nG B R\n"+
                        "Left\nB R G\nB W G\nB O O\n"+
                        "Back\nO O W\nW O Y\nW O Y\n";

        setConfig(config, expected, "RUR'FDB");
    }

    public void testSequenceOfMovesComplex(){
        String config =
                "Top\nB B B\nB B B\nB B B\n" +
                        "Bottom\nG G G\nG G G\nG G G\n" +
                        "Front\nR R R\nR R R\nR R R\n" +
                        "Right\nY Y Y\nY Y Y\nY Y Y\n"+
                        "Left\nW W W\nW W W\nW W W\n"+
                        "Back\nO O O\nO O O\nO O O\n";

        String expected =
                "Top\nW O G\nG B W\nO O G\n" +
                        "Bottom\nR W B\nB G B\nO W B\n" +
                        "Front\nG Y R\nO R Y\nG G R\n" +
                        "Right\nY O W\nY Y B\nO W W\n"+
                        "Left\nO Y Y\nR W B\nY R W\n"+
                        "Back\nB G R\nG O R\nB R Y\n";

        setConfig(config, expected, "L'UR'DBF'LBUR'LBBRR");
    }

    public void setConfig(String given, String expected, String sequence){
        Cube cube = null;
        Cube expectedCube = null;
        try {
            cube = new Cube(new BufferedReader(new StringReader(given)));
            expectedCube = new Cube(new BufferedReader(new StringReader(expected)));
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            cube.performSequence(sequence);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(expectedCube.toString(), cube.toString());
    }
}
