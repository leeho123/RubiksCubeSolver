package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.CompactCube;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by LeHoang on 13/04/2015.
 */
public class CompactCubeTest extends TestCase {
    static String solved = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    public void testConstructor(){
        CompactCube cube = new CompactCube(solved);

        Assert.assertEquals(solved, CompactCube.toKociemba(cube));
    }

    public void testComplexConstructor(){
        CompactCube cube = new CompactCube("UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB");

        Assert.assertEquals("UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB", CompactCube.toKociemba(cube));
    }

    public void testDefaultConstructor(){
        CompactCube cube = new CompactCube();
        String kociemba = CompactCube.toKociemba(cube);

        Assert.assertEquals(solved, kociemba);
    }

    public void testRMove(){
        String rMove = "UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.R);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testRPrimeMove(){
        String rMove = "UUBUUBUUBRRRRRRRRRFFUFFUFFUDDFDDFDDFLLLLLLLLLDBBDBBDBB";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.RPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testR2Move(){
        String rMove = "UUDUUDUUDRRRRRRRRRFFBFFBFFBDDUDDUDDULLLLLLLLLFBBFBBFBB";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.R2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testLMove(){
        String lMove = "BUUBUUBUURRRRRRRRRUFFUFFUFFFDDFDDFDDLLLLLLLLLBBDBBDBBD";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.L);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testLPrimeMove(){
        String lMove = "FUUFUUFUURRRRRRRRRDFFDFFDFFBDDBDDBDDLLLLLLLLLBBUBBUBBU";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.LPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testL2Move(){
        String lMove = "DUUDUUDUURRRRRRRRRBFFBFFBFFUDDUDDUDDLLLLLLLLLBBFBBFBBF";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.L2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testUMove(){
        String UMove = "UUUUUUUUUBBBRRRRRRRRRFFFFFFDDDDDDDDDFFFLLLLLLLLLBBBBBB";

        CompactCube cube = new CompactCube();
        cube.move(CompactCube.U);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testUPrimeMove(){
        String UMove = "UUUUUUUUUFFFRRRRRRLLLFFFFFFDDDDDDDDDBBBLLLLLLRRRBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.UPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testU2Move(){
        String UMove = "UUUUUUUUULLLRRRRRRBBBFFFFFFDDDDDDDDDRRRLLLLLLFFFBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.U2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testDMove(){
        String dMove = "UUUUUUUUURRRRRRFFFFFFFFFLLLDDDDDDDDDLLLLLLBBBBBBBBBRRR";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.D);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testDPrimeMove(){
        String dMove = "UUUUUUUUURRRRRRBBBFFFFFFRRRDDDDDDDDDLLLLLLFFFBBBBBBLLL";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.DPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testD2Move(){
        String dMove = "UUUUUUUUURRRRRRLLLFFFFFFBBBDDDDDDDDDLLLLLLRRRBBBBBBFFF";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.D2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testFMove(){
        String fMove = "UUUUUULLLURRURRURRFFFFFFFFFRRRDDDDDDLLDLLDLLDBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.F);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testFPrimeMove(){
        String fMove = "UUUUUURRRDRRDRRDRRFFFFFFFFFLLLDDDDDDLLULLULLUBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.FPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testF2Move(){
        String fMove = "UUUUUUDDDLRRLRRLRRFFFFFFFFFUUUDDDDDDLLRLLRLLRBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.F2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testBMove(){
        String bMove = "RRRUUUUUURRDRRDRRDFFFFFFFFFDDDDDDLLLULLULLULLBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.B);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testBPrime(){
        String bMove = "LLLUUUUUURRURRURRUFFFFFFFFFDDDDDDRRRDLLDLLDLLBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.BPRIME);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testB2Move(){
        String bMove = "DDDUUUUUURRLRRLRRLFFFFFFFFFDDDDDDUUURLLRLLRLLBBBBBBBBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.B2);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testMoveSequence(){
        String config = "UUUUUUFFFUBBRRRRRRRRRFFDFFDDDBDDBDDBFFDLLLLLLLLLUBBUBB";
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.R);
        cube.move(CompactCube.U);
        String koc = CompactCube.toKociemba(cube);
        Assert.assertEquals(config, koc);
    }

    public void testComplexMoveSequence(){
        CompactCube cube = new CompactCube();
        cube.move(CompactCube.R);
        cube.move(CompactCube.U);
        cube.move(CompactCube.RPRIME);
        cube.move(CompactCube.F);
        cube.move(CompactCube.FPRIME);
        cube.move(CompactCube.R);
        cube.move(CompactCube.UPRIME);
        cube.move(CompactCube.RPRIME);

        Assert.assertEquals(solved, CompactCube.toKociemba(cube));
    }

    public void testFactorialNumbering(){
       byte[] test = {2,0,4,1,3};

       int encoding = CompactCube.getFactorialNumber(test);

       assertEquals(37, encoding);
    }

    public void testFactorialNumbering2(){
       byte[] test = {3,0,2,1,4};

        int encoding = CompactCube.getFactorialNumber(test);

        assertEquals(38, encoding);
    }

    public void testFactorialNumbering3(){
        byte[] test = {4,3,2,1,0};

        int encoding = CompactCube.getFactorialNumber(test);

        assertEquals(119, encoding);
    }

    public void testFactorialNumbering4(){
        byte[] test = {0,1,2,3};

        int encoding = CompactCube.getFactorialNumber(test);

        assertEquals(0, encoding);
    }

    public void testFactorialNumbering5(){
        byte[] test = {0,1,3,2};
        int encoding = CompactCube.getFactorialNumber(test);
        System.out.println(encoding);
        assertEquals(1, encoding);
    }

    public void testBase10Conversion(){
        byte[] test = {1,2,3,4,5,6};

        int encoding = CompactCube.getBase10FromBaseN(test, 8);

        assertEquals(42798, encoding);
    }

    public void testBase10Conversion2(){
        byte[] test = {0,0,0,0,0,0,0,0,0,0,0,0};

        int encoding = CompactCube.getBase10FromBaseN(test, 3);

        assertEquals(0, encoding);
    }

    public void testBase10Conversion3(){
        byte[] test = {1,1,1,1,1,1};
        int encoding = CompactCube.getBase10FromBaseN(test,2);

        assertEquals(63, encoding);
    }

    public void testEncodeCorners(){
        CompactCube cube = new CompactCube();
        int result = cube.encodeCorners();

        assertEquals(0, result);
    }

    public void testEncodeEdges(){
        CompactCube cube = new CompactCube();

        int result = cube.encodeEdges();

        assertEquals(0, result);
    }

    public void testNPK(){
        byte[] test = {2,3};
        int index = CompactCube.getNPKIndex(test, 4);

        assertEquals(8 , index);
    }

    public void testNPK2(){
        byte[] test = {2,1,3};
        int index = CompactCube.getNPKIndex(test, 4);

        assertEquals(15, index);
    }

    public void testEncode6Of12(){
        byte[] test = {0,2,4,6,8,10};

        int encoding = CompactCube.encode6Of12Edges(test);

        assertEquals(0, encoding);
    }

    public void testEncode6Of12Second(){
        byte[] test = {23,21,19,17,15,13};
        int encoding = CompactCube.encode6Of12Edges(test);

        assertEquals(42577919, encoding);
    }

}
