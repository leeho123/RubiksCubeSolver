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

    @Test
    public void testKorfsCorner() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        String path = Korfs.generateCornersAndroid();
        File file = new File(path);

        Assert.assertTrue(file.exists());
    }

    public void testKorfsCornerOnPC() throws Exception{
        File file = new File(Korfs.CORNERS_FILE_NAME);
        Korfs.generateCornerHeuristics(file);
    }

    public void testKorfsFirstEdgeOnPC() throws Exception{
        File file = new File(Korfs.FIRST_EDGE_FILE_NAME);
        Korfs.generateFirstEdgeHeuristics(file);
    }

    public void testKorfsSecondEdgeOnPC() throws Exception{
        File file = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.generateSecondEdgeHeuristics(file);
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

        Cube cube = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        cube.performSequence("RBLUD");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("D'U'L'B'R'", solution);
    }

    public void testKorfSearchTenMoveSolution() throws Exception{
        System.out.println("Loading...");
        Korfs.loadHeuristics();
        System.out.println("Loading done");

        Cube cube = new Cube(new BufferedReader(new StringReader(Cube.SOLVED)));
        cube.performSequence("RBLUDR'UL'");
        String solution = Korfs.searchKorfs(cube, 20);
        Assert.assertEquals("LU'RD'U'L'B'R'", solution);
    }

}
