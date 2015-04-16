package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by LeHoang on 08/04/2015.
 */
//@RunWith(RobolectricTestRunner.class)
public class KorfTest extends TestCase {

    public void testKorfsCornerOnPC2() throws Exception{
        File file = new File(Korfs.CORNERS_FILE_NAME);
        Korfs.generateCornerHeuristics2(file);
    }

    public void testNibbleSetIndex() {

        Korfs.NibbleArray arr = new Korfs.NibbleArray(100);

        arr.setIndex(40, (byte) 10);
        Assert.assertEquals(10, arr.getIndex(40));
    }

    public void testNibbleSetIndexZero(){
        Korfs.NibbleArray arr = new Korfs.NibbleArray(500);
        arr.setIndex(0, 15);
        Assert.assertEquals(15, arr.getIndex(0));
    }

    public void testNibbleSetLastIndex(){
        Korfs.NibbleArray arr = new Korfs.NibbleArray(500);
        arr.setIndex(499, 15);
        Assert.assertEquals(15, arr.getIndex(499));
    }

    public void testNibbleSetNextToEachOther(){
        Korfs.NibbleArray arr = new Korfs.NibbleArray(500);
        arr.setIndex(440,15);
        arr.setIndex(441,1);

        Assert.assertEquals(15, arr.getIndex(440));
        Assert.assertEquals(1, arr.getIndex(441));
    }

    public void testNibbleOverwriteData(){
        Korfs.NibbleArray arr = new Korfs.NibbleArray(500);
        arr.setIndex(440,15);
        arr.setIndex(441,1);
        arr.setIndex(440,8);

        Assert.assertEquals(8, arr.getIndex(440));
        Assert.assertEquals(1, arr.getIndex(441));

    }

  /*
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
    }*/

}
