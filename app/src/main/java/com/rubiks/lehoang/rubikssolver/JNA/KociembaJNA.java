package com.rubiks.lehoang.rubikssolver.JNA;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.sql.Struct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LeHoang on 21/04/2015.
 */
public class KociembaJNA{
    public interface Koc extends Library {
        public void initSymCubes();
        public void initMoveCubes();
        public void initInvSymIdx();
        public void initSymIdxMultiply();
        public void initMoveConjugate();
        public void initMoveBitsConjugate();
        public void initGESymmetries();
        public void initTwistConjugate();
        public void initRawFLipSliceRep();
        public void initTwistMove();
        public void initCorn6PosMove();
        public void initEdge4PosMove();
        public void initEdge6PosMove();
        public void initSymFlipSliceClassMove();
        public void initMovesCloserToTarget();
        public void initNextMove();
        public void initMem();
        public void setParam();
        public void initTables();

        public CubieCube.ByValue stringToCubieCube(String defString);
        public String solveOptimal(CubieCube.ByValue cu);


        public static class CubieCube extends Structure {
            public static class ByReference extends CubieCube implements Structure.ByReference{};
            public static class ByValue extends  CubieCube implements Structure.ByValue{};

            public corner_o[] co = (corner_o[]) new corner_o().toArray(8);
            public edge_o[] eo = (edge_o[]) new edge_o().toArray(12);

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"co","eo"});
            }
        }

        public static class corner_o extends Structure{
            public static class ByReference extends corner_o implements Structure.ByReference{};
            public int c;
            public byte o;

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"c","o"});
            }
        }

        public static class edge_o extends Structure{
            public static class ByReference extends corner_o implements Structure.ByReference{};
            public int e;
            public byte o;

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"e","o"});
            }
        }

        public interface Corner{
            public static final int URF = 0;
            public static final int UFL = 1;
            public static final int ULB = 2;
            public static final int UBR = 3;
            public static final int DFR = 4;
            public static final int DLF = 5;
            public static final int DBL = 6;
            public static final int DRB = 7;
        }

        public interface Edge{
            public static final int UR = 0;
            public static final int UF = 1;
            public static final int UL = 2;
            public static final int UB = 3;
            public static final int DR = 4;
            public static final int DF = 5;
            public static final int DL = 6;
            public static final int DB = 7;
            public static final int FR = 8;
            public static final int FL = 9;
            public static final int BL = 10;
            public static final int BR = 11;
        }


    }

    Koc koc;

    public KociembaJNA() throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("jna.library.path", "/Users/LeHoang/AndroidStudioProjects/RubiksSolver/app/src/Kociemba/optiqtmSrc");
        Native.setProtected(true);

        Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        fieldSysPath.setAccessible( true );
        fieldSysPath.set(null, null);

        koc = (Koc) Native.loadLibrary("Koc", Koc.class);
        koc.setParam();

        koc.initMem();
        koc.initTables();
        koc.initSymCubes();
        koc.initMoveCubes();
        koc.initInvSymIdx();
        koc.initSymIdxMultiply();
        koc.initSymCubes();
        koc.initMoveCubes();
        koc.initInvSymIdx();
        koc.initSymIdxMultiply();
        koc.initMoveConjugate();
        koc.initMoveBitsConjugate();
        koc.initGESymmetries();
        koc.initTwistConjugate();
        koc.initRawFLipSliceRep();
        koc.initTwistMove();
        koc.initCorn6PosMove();
        koc.initEdge4PosMove();
        koc.initEdge6PosMove();
        koc.initSymFlipSliceClassMove();
        koc.initMovesCloserToTarget();
        koc.initNextMove();
    }

    public String solveOptimal(String singMaster){
        Koc.CubieCube.ByValue cube = koc.stringToCubieCube(singMaster);
        String solution = koc.solveOptimal(cube);
        return solution;
    }

    public static void main(String[] argv) throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("jna.library.path", "/Users/LeHoang/AndroidStudioProjects/RubiksSolver/app/src/Kociemba/optiqtmSrc");
        Native.setProtected(true);

        Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        fieldSysPath.setAccessible( true );
        fieldSysPath.set( null, null );

        Koc koc = (Koc) Native.loadLibrary("Koc", Koc.class);
        koc.setParam();

        koc.initMem();
        koc.initTables();
        koc.initSymCubes();
        koc.initMoveCubes();
        koc.initInvSymIdx();
        koc.initSymIdxMultiply();
        koc.initSymCubes();
        koc.initMoveCubes();
        koc.initInvSymIdx();
        koc.initSymIdxMultiply();
        koc.initMoveConjugate();
        koc.initMoveBitsConjugate();
        koc.initGESymmetries();
        koc.initTwistConjugate();
        koc.initRawFLipSliceRep();
        koc.initTwistMove();
        koc.initCorn6PosMove();
        koc.initEdge4PosMove();
        koc.initEdge6PosMove();
        koc.initSymFlipSliceClassMove();
        koc.initMovesCloserToTarget();
        koc.initNextMove();

        Koc.CubieCube.ByValue cube = koc.stringToCubieCube("SDFS");
        System.out.println(cube.toString(true));
        String solution = koc.solveOptimal(cube);
        System.out.println(solution);
    }
}
