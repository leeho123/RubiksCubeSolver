package com.rubiks.lehoang.tests;

import android.os.Environment;

import com.rubiks.lehoang.rubikssolver.Cube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by LeHoang on 08/04/2015.
 */
//@RunWith(RobolectricTestRunner.class)
public class KorfTest extends TestCase {

    public void testKorfsCornerOnPC2() throws Exception{
        File file = new File(Korfs.CORNERS_FILE_NAME);
        Korfs.generateCornerHeuristics2(file);
    }

    public void testKorfsEdgeOnPC() throws Exception{
        File firstFile = new File(Korfs.FIRST_EDGE_FILE_NAME);
        File secondFile = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.generateEdgeHeuristics(firstFile, secondFile);
    }

    public void testLoadCornerOnPC() throws IOException{
       File file = new File(Korfs.CORNERS_FILE_NAME);
       Korfs.loadCorners(file);

       Assert.assertEquals(40320, Korfs.cornerMap.size());
    }

    public void testLoadFirstEdgesOnPC() throws IOException{
        File file = new File(Korfs.FIRST_EDGE_FILE_NAME);
        Korfs.loadFirstEdges(file);

        Assert.assertEquals(665280, Korfs.firstEdgeMap.size());
    }

    public void testLoadSecondEdgesOnPC() throws IOException {
        File file = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.loadSecondEdges(file);
        Assert.assertEquals(665280, Korfs.secondEdgeMap.size());
    }

    public void testKorfSearchOnSolvedCube() throws Exception {
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        String solution = Korfs.searchKorfs(new Cube(new BufferedReader(new StringReader(Cube.SOLVED))), 20);

        Assert.assertEquals("", solution);
    }

    public void testKorfSearchOnOneMoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");


        String cube = "Top\nB B B\nB B B\nB B B\n" +
                "Bottom\nG G G\nG G G\nG G G\n" +
                "Front\nY Y Y\nR R R\nR R R\n" +
                "Right\nO O O\nY Y Y\nY Y Y\n"+
                "Left\nR R R\nW W W\nW W W\n"+
                "Back\nW W W\nO O O\nO O O\n";


        String solution = Korfs.searchKorfs(new Cube(new BufferedReader(new StringReader(cube))), 20);

        Assert.assertEquals("U'", solution);
    }

    public void testKorfSearchThreeMoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        cube.performSequence("RUR'");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("RU'R'", solution);
    }

    public void testKorfSearchFiveMoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(Cube.SOLVED_COMPACT);
        cube.performSequence("RBLUD");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("U'D'L'B'R'", solution);
    }

    public void testKorfSearchSevenMoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(Cube.SOLVED_COMPACT);
        cube.performSequence("RBLUDR'UL'");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("LU'RD'U'L'B'R'", solution);
    }

    public void testKorfSearch8MoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(Cube.SOLVED_COMPACT);
        cube.performSequence("RBLUDR'UL'F");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("F'LU'RD'U'L'B'R'", solution);
    }

    public void testKorfSearch10MoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(Cube.SOLVED_COMPACT);
        cube.performSequence("RBLUDR'UL'FDR'");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("RD'F'LU'RD'U'L'B'R'", solution);
    }

}
