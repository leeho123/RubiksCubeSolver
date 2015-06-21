package com.rubiks.lehoang.tests;

import com.rubiks.lehoang.rubikssolver.Cube.Cube;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by LeHoang on 13/04/2015.
 */
public class CubeTest extends TestCase {
    static String solved = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    public void testConstructor(){
        Cube cube = new Cube(solved);

        Assert.assertEquals(solved, Cube.toKociemba(cube));
    }

    public void testComplexConstructor(){
        Cube cube = new Cube("UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB");

        Assert.assertEquals("UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB", Cube.toKociemba(cube));
    }

    public void testComplexCon(){
        Cube cube = new Cube("FRFFUDDLBLBRBRDRLLRFDDFULRBBUULDUFFFDDBRLLDBUUBLRBFUUR");

        Assert.assertEquals("FRFFUDDLBLBRBRDRLLRFDDFULRBBUULDUFFFDDBRLLDBUUBLRBFUUR", Cube.toKociemba(cube));
    }

    public void testDefaultConstructor(){
        Cube cube = new Cube();
        String kociemba = Cube.toKociemba(cube);

        Assert.assertEquals(solved, kociemba);
    }

    public void testRMove(){
        String rMove = "UUFUUFUUFRRRRRRRRRFFDFFDFFDDDBDDBDDBLLLLLLLLLUBBUBBUBB";
        Cube cube = new Cube();
        cube.move(Cube.R);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testRPrimeMove(){
        String rMove = "UUBUUBUUBRRRRRRRRRFFUFFUFFUDDFDDFDDFLLLLLLLLLDBBDBBDBB";

        Cube cube = new Cube();
        cube.move(Cube.RPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testR2Move(){
        String rMove = "UUDUUDUUDRRRRRRRRRFFBFFBFFBDDUDDUDDULLLLLLLLLFBBFBBFBB";

        Cube cube = new Cube();
        cube.move(Cube.R2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(rMove, koc);
    }

    public void testLMove(){
        String lMove = "BUUBUUBUURRRRRRRRRUFFUFFUFFFDDFDDFDDLLLLLLLLLBBDBBDBBD";

        Cube cube = new Cube();
        cube.move(Cube.L);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testLPrimeMove(){
        String lMove = "FUUFUUFUURRRRRRRRRDFFDFFDFFBDDBDDBDDLLLLLLLLLBBUBBUBBU";

        Cube cube = new Cube();
        cube.move(Cube.LPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testL2Move(){
        String lMove = "DUUDUUDUURRRRRRRRRBFFBFFBFFUDDUDDUDDLLLLLLLLLBBFBBFBBF";

        Cube cube = new Cube();
        cube.move(Cube.L2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(lMove, koc);
    }

    public void testUMove(){
        String UMove = "UUUUUUUUUBBBRRRRRRRRRFFFFFFDDDDDDDDDFFFLLLLLLLLLBBBBBB";

        Cube cube = new Cube();
        cube.move(Cube.U);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testUPrimeMove(){
        String UMove = "UUUUUUUUUFFFRRRRRRLLLFFFFFFDDDDDDDDDBBBLLLLLLRRRBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.UPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testU2Move(){
        String UMove = "UUUUUUUUULLLRRRRRRBBBFFFFFFDDDDDDDDDRRRLLLLLLFFFBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.U2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(UMove, koc);
    }

    public void testDMove(){
        String dMove = "UUUUUUUUURRRRRRFFFFFFFFFLLLDDDDDDDDDLLLLLLBBBBBBBBBRRR";
        Cube cube = new Cube();
        cube.move(Cube.D);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testDPrimeMove(){
        String dMove = "UUUUUUUUURRRRRRBBBFFFFFFRRRDDDDDDDDDLLLLLLFFFBBBBBBLLL";
        Cube cube = new Cube();
        cube.move(Cube.DPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testD2Move(){
        String dMove = "UUUUUUUUURRRRRRLLLFFFFFFBBBDDDDDDDDDLLLLLLRRRBBBBBBFFF";
        Cube cube = new Cube();
        cube.move(Cube.D2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(dMove, koc);
    }

    public void testFMove(){
        String fMove = "UUUUUULLLURRURRURRFFFFFFFFFRRRDDDDDDLLDLLDLLDBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.F);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testFPrimeMove(){
        String fMove = "UUUUUURRRDRRDRRDRRFFFFFFFFFLLLDDDDDDLLULLULLUBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.FPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testF2Move(){
        String fMove = "UUUUUUDDDLRRLRRLRRFFFFFFFFFUUUDDDDDDLLRLLRLLRBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.F2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(fMove, koc);
    }

    public void testBMove(){
        String bMove = "RRRUUUUUURRDRRDRRDFFFFFFFFFDDDDDDLLLULLULLULLBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.B);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testBPrime(){
        String bMove = "LLLUUUUUURRURRURRUFFFFFFFFFDDDDDDRRRDLLDLLDLLBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.BPRIME);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testB2Move(){
        String bMove = "DDDUUUUUURRLRRLRRLFFFFFFFFFDDDDDDUUURLLRLLRLLBBBBBBBBB";
        Cube cube = new Cube();
        cube.move(Cube.B2);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(bMove, koc);
    }

    public void testMoveSequence(){
        String config = "UUUUUUFFFUBBRRRRRRRRRFFDFFDDDBDDBDDBFFDLLLLLLLLLUBBUBB";
        Cube cube = new Cube();
        cube.move(Cube.R);
        cube.move(Cube.U);
        String koc = Cube.toKociemba(cube);
        Assert.assertEquals(config, koc);
    }

    public void testComplexMoveSequence() {
        Cube cube = new Cube();
        cube.move(Cube.R);
        cube.move(Cube.U);
        cube.move(Cube.RPRIME);
        cube.move(Cube.F);
        cube.move(Cube.FPRIME);
        cube.move(Cube.R);
        cube.move(Cube.UPRIME);
        cube.move(Cube.RPRIME);

        Assert.assertEquals(solved, Cube.toKociemba(cube));
    }

    public void testComplexMoveSequenceAsString() throws Cube.InvalidMoveException{
        Cube cube = new Cube();
        cube.move(Cube.R);
        cube.move(Cube.U);
        cube.move(Cube.RPRIME);
        cube.move(Cube.F);
        cube.move(Cube.FPRIME);
        cube.move(Cube.R);
        cube.move(Cube.UPRIME);
        cube.move(Cube.RPRIME);

        Cube cube2 = new Cube();
        cube2.performSequence("RUR3FF3RU3R3");

        Assert.assertEquals(cube2, cube);
    }

    public void testFactorialNumbering(){
       byte[] test = {2,0,4,1,3};

       int encoding = Cube.getFactorialNumber(test);

       assertEquals(37, encoding);
    }

    public void testFactorialNumbering2(){
       byte[] test = {3,0,2,1,4};

        int encoding = Cube.getFactorialNumber(test);

        assertEquals(38, encoding);
    }

    public void testFactorialNumbering3(){
        byte[] test = {4,3,2,1,0};

        int encoding = Cube.getFactorialNumber(test);

        assertEquals(119, encoding);
    }

    public void testFactorialNumbering4(){
        byte[] test = {0,1,2,3};

        int encoding = Cube.getFactorialNumber(test);

        assertEquals(0, encoding);
    }

    public void testFactorialNumbering5(){
        byte[] test = {0,1,3,2};
        int encoding = Cube.getFactorialNumber(test);
        System.out.println(encoding);
        assertEquals(1, encoding);
    }

    public void testBase10Conversion(){
        byte[] test = {1,2,3,4,5,6};

        int encoding = Cube.getBase10FromBaseN(test, 8);

        assertEquals(42798, encoding);
    }

    public void testBase10Conversion2(){
        byte[] test = {0,0,0,0,0,0,0,0,0,0,0,0};

        int encoding = Cube.getBase10FromBaseN(test, 3);

        assertEquals(0, encoding);
    }

    public void testBase10Conversion3(){
        byte[] test = {1,1,1,1,1,1};
        int encoding = Cube.getBase10FromBaseN(test, 2);

        assertEquals(63, encoding);
    }

    /*
    public void testEncodeCorners(){
        CompactCube cube = new CompactCube();
        int result = CompactCube.encodeCorners();

        assertEquals(0, result);
    }
    */
    public void testEncodeEdges(){
        Cube cube = new Cube();

        long result = cube.encodeEdges();

        assertEquals(0, result);
    }

    public void testNPK(){
        byte[] test = {2,3};
        int index = Cube.getNPKIndex(test, 4);

        assertEquals(8 , index);
    }

    public void testNPK2(){
        byte[] test = {2,1,3};
        int index = Cube.getNPKIndex(test, 4);

        assertEquals(15, index);
    }
/*
    public void testEncode6Of12(){
        byte[] test = {0,2,4,6,8,10};

        int encoding = CompactCube.encode6Of12Edges(test);

        assertEquals(0, encoding);
    }

    public void testEncode6Of12Other(){
        byte[] test = {12,14,16, 18,20,22};
        int encoding = CompactCube.encode6Of12Edges(test);

        assertEquals(0, encoding);
    }

    public void testEncode6Of12Second(){
        byte[] test = {23,21,19,17,15,13};
        int encoding = CompactCube.encode6Of12Edges(test);

        assertEquals(42577919, encoding);
    }*/

    /*
    public void testencodeCorners(){
        CompactCube cube = new CompactCube();
        cube.move(1);
        int encoding = cube.encodeCorners();

    }*/
}
