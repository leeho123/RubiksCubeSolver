package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.CompactCube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.util.concurrent.ExecutionException;

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


    public void testKorfSearchThreeMoveSolution() throws Exception{
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.R);
        cube.move(CompactCube.U);
        cube.move(CompactCube.RPRIME);

        String solution = Korfs.idaStarKorfs(5, cube);

        Assert.assertEquals("RU'R'", solution);
    }

    public void testKorfSearchFiveMoveSolution() throws Exception{
        CompactCube cube = new CompactCube();

        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);

        String solution = Korfs.idaStarKorfs(20, cube);
        Assert.assertEquals("U'D'L'B'R'", solution);
    }

    public void testKorfSearchSevenMoveSolution() throws Exception{
        CompactCube cube = new CompactCube();

        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);

        String solution = Korfs.idaStarKorfs(20, cube);

        Assert.assertEquals("LU'RU'D'L'B'R'", solution);
    }


    public void testKorfSearch10MoveSolution(){
        CompactCube cube = new CompactCube();

        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);


        String solution = Korfs.idaStarKorfs(20, cube);
        Assert.assertEquals("RD'F'LU'RU'D'L'B'R'", solution);
    }

    public void testKorfSearch12MoveSolution(){
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.DPRIME);
        cube.move(CompactCube.FPRIME);
        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);

        String solution = Korfs.idaStarKorfs(20, cube);
        Assert.assertEquals("RD'F'LU'RU'D'L'B'R'FD", solution);
    }

    public void testKorfSearch15MoveSolution(){
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.UPRIME);
        cube.move(CompactCube.B2);



        String solution = Korfs.idaStarKorfs(20, cube);
        Assert.assertEquals("B2UF'RD'F'LU'RU'D'L'B'R'", solution);
    }

    public void testKorfSearch17MoveSolution(){
        CompactCube cube = new CompactCube();

        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.UPRIME);
        cube.move(CompactCube.B2);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);

        String solution = Korfs.idaStarKorfs(20, cube);
        Assert.assertEquals("F'RB2UF'RD'F'LU'RU'D'L'B'R'", solution);
    }

    public void testMultiThreadedKorfSearch12Moves() throws ExecutionException, InterruptedException {
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.DPRIME);
        cube.move(CompactCube.FPRIME);
        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);

        String solution = new Korfs().idaStarMultiKorfs(20, cube);
        Assert.assertEquals("RD'F'LU'RU'D'L'B'R'FD", solution);
    }

    public void testMultiThreadedKorfSearch15Moves() throws ExecutionException, InterruptedException {
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.UPRIME);
        cube.move(CompactCube.B2);

        String solution = new Korfs().idaStarMultiKorfs(20, cube);
        Assert.assertEquals("B2UF'RD'F'LU'RU'D'L'B'R'", solution);
    }

    public void testFringeSearch13Moves(){
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.DPRIME);
        cube.move(CompactCube.FPRIME);
        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);

        String solution = Korfs.fringeSearchKorfs(20, cube);
        Assert.assertEquals("RD'F'LU'RU'D'L'B'R'FD", solution);
    }

    public void testFringeSearch14Moves(){
        CompactCube cube = new CompactCube();


        cube.move(CompactCube.R);
        cube.move(CompactCube.B);
        cube.move(CompactCube.L);
        cube.move(CompactCube.U);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.U);
        cube.move(CompactCube.LPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.D);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.UPRIME);
        cube.move(CompactCube.B2);

        String solution = Korfs.fringeSearchKorfs(20, cube);
        Assert.assertEquals("B2UF'RD'F'LU'RU'D'L'B'R'", solution);

    }
}
