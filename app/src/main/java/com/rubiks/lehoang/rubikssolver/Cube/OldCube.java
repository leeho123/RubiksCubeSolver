package com.rubiks.lehoang.rubikssolver.Cube;

import android.content.Context;

import com.rubiks.lehoang.rubikssolver.R;
import com.rubiks.lehoang.rubikssolver.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LeHoang on 02/01/2015.
 */
public class OldCube implements Iterable<OldCube.Face>{

    public static String[] moves = {"R","L","U","F","B","D",
            "R'","L'","U'","F'","B'","D'"};


    private Face[] faces = new Face[6];
    public static final int TOP = 0;
    public static final int RIGHT = 1;
    public static final int FRONT = 2;
    public static final int BOTTOM = 3;
    public static final int LEFT = 4;
    public static final int BACK = 5;

    private static int[] faceOrder = {TOP, LEFT, RIGHT, BOTTOM, FRONT, BACK};

    private Map<String, Face> faceMap = new HashMap<String, Face>();

    public Iterator<Face> iterator(){
        return new CubeIterator();
    }

    public static String SOLVED = "Top\nB B B\nB B B\nB B B\n" +
            "Bottom\nG G G\nG G G\nG G G\n" +
            "Front\nR R R\nR R R\nR R R\n" +
            "Right\nY Y Y\nY Y Y\nY Y Y\n"+
            "Left\nW W W\nW W W\nW W W\n"+
            "Back\nO O O\nO O O\nO O O\n";

    public static String SOLVED_COMPACT = "BBBBBBBBBWWWWWWWWWYYYYYYYYYGGGGGGGGGRRRRRRRRROOOOOOOOO";

    private static Map<String,Integer> cornerMap = initCornerMap();

    private static Map<String, Integer> initCornerMap(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("BRW", 0);
        map.put("BRY", 1);
        map.put("BOW", 2);
        map.put("BOY", 3);
        map.put("GRW", 4);
        map.put("GRY", 5);
        map.put("GOW", 6);
        map.put("GOY", 7);

        return map;
    }

    public enum Edge{
        BW, RW, OW, BR, GO, GY,
        BY, RY, BO, OY, GR, GW
    }
    /*
        OY,OW,RY,RW,BR,GO,
        BO,GR,BY,BW,GY,GW;
    }*/

    public static Edge[] firstEdges = {Edge.BW, Edge.RW, Edge.OW, Edge.BR, Edge.GO, Edge.GY};
    public static Edge[] secondEdges = {Edge.BY, Edge.RY, Edge.BO, Edge.OY, Edge.GR, Edge.GW};


    public Edge getEdgeColour(Edge edge){
        char[] edgeChar = new char[2];
        switch(edge){
            case OY:
                //BR
                edgeChar[0] = Colour.ColourToLetter(faces[BACK].getCentreRow().getRight());
                edgeChar[1] = Colour.ColourToLetter(faces[RIGHT].getCentreRow().getLeft());
                break;
            case OW:
                //BL
                edgeChar[0] = Colour.ColourToLetter(faces[BACK].getCentreRow().getLeft());
                edgeChar[1] = Colour.ColourToLetter(faces[LEFT].getCentreRow().getLeft());
                break;
            case RY:
                //FR
                edgeChar[0] = Colour.ColourToLetter(faces[FRONT].getCentreRow().getRight());
                edgeChar[1] = Colour.ColourToLetter(faces[RIGHT].getCentreRow().getRight());
                break;
            case GO:
                //BD
                edgeChar[0] = Colour.ColourToLetter(faces[BOTTOM].getTopRow().getCentre());
                edgeChar[1] = Colour.ColourToLetter(faces[BACK].getBottomRow().getCentre());
                break;
            case BO:
                //UB
                edgeChar[0] = Colour.ColourToLetter(faces[TOP].getTopRow().getCentre());
                edgeChar[1] = Colour.ColourToLetter(faces[BACK].getTopRow().getCentre());
                break;
            case GR:
                //FD
                edgeChar[0] = Colour.ColourToLetter(faces[BOTTOM].getBottomRow().getCentre());
                edgeChar[1] = Colour.ColourToLetter(faces[FRONT].getBottomRow().getCentre());
                break;
            case BY:
                //RU
                edgeChar[0] = Colour.ColourToLetter(faces[TOP].getCentreRow().getRight());
                edgeChar[1] = Colour.ColourToLetter(faces[RIGHT].getTopRow().getCentre());
                break;
            case BW:
                //UL
                edgeChar[0] = Colour.ColourToLetter(faces[TOP].getCentreRow().getLeft());
                edgeChar[1] = Colour.ColourToLetter(faces[LEFT].getTopRow().getCentre());
                break;
            case GY:
                //DR
                edgeChar[0] = Colour.ColourToLetter(faces[BOTTOM].getCentreRow().getRight());
                edgeChar[1] = Colour.ColourToLetter(faces[RIGHT].getBottomRow().getCentre());
                break;
            case RW:
                //FL
                edgeChar[0] = Colour.ColourToLetter(faces[FRONT].getCentreRow().getLeft());
                edgeChar[1] = Colour.ColourToLetter(faces[LEFT].getCentreRow().getRight());
                break;
            case BR:
                //FU
                edgeChar[0] = Colour.ColourToLetter(faces[TOP].getBottomRow().getCentre());
                edgeChar[1] = Colour.ColourToLetter(faces[FRONT].getTopRow().getCentre());
                break;
            case GW:
                //DL
                edgeChar[0] = Colour.ColourToLetter(faces[BOTTOM].getCentreRow().getLeft());
                edgeChar[1] = Colour.ColourToLetter(faces[LEFT].getBottomRow().getCentre());
                break;
            default:
                return null;
        }
        Util.order(edgeChar);
        return Edge.valueOf(new String(edgeChar));
    }


    /**
     * Give a unique encoding for corner by giving the corners in this order:
     *
     * ULF
     * URF
     * ULB
     * URB
     *
     * DLF
     * DRF
     * DLB
     * DRB
     *
     * @return
     */
    public String encodeCorners(){
        StringBuilder encoding = new StringBuilder();

        char[] ULF = new char[3];
        ULF[0] = Colour.ColourToLetter(faces[TOP].getBottomRow().getLeft());
        ULF[1] = Colour.ColourToLetter(faces[LEFT].getTopRow().getRight());
        ULF[2] = Colour.ColourToLetter(faces[FRONT].getTopRow().getLeft());
        Arrays.sort(ULF);
        int ULFenc = cornerMap.get(new String(ULF));
        encoding.append(ULFenc);

        char[] URF = new char[3];
        URF[0] = Colour.ColourToLetter(faces[TOP].getBottomRow().getRight());
        URF[1] = Colour.ColourToLetter(faces[RIGHT].getTopRow().getRight());
        URF[2] = Colour.ColourToLetter(faces[FRONT].getTopRow().getRight());
        Arrays.sort(URF);
        int URFenc = cornerMap.get(new String(URF));
        encoding.append(URFenc);

        //ULB
        char[] ULB = new char[3];
        ULB[0] = Colour.ColourToLetter(faces[TOP].getTopRow().getLeft());
        ULB[1] = Colour.ColourToLetter(faces[LEFT].getTopRow().getLeft());
        ULB[2] = Colour.ColourToLetter(faces[BACK].getTopRow().getLeft());
        Arrays.sort(ULB);
        int ULBenc = cornerMap.get(new String(ULB));
        encoding.append(ULBenc);

        //URB
        char[] URB = new char[3];
        URB[0] = Colour.ColourToLetter(faces[TOP].getTopRow().getRight());
        URB[1] = Colour.ColourToLetter(faces[RIGHT].getTopRow().getLeft());
        URB[2] = Colour.ColourToLetter(faces[BACK].getTopRow().getRight());
        Arrays.sort(URB);
        int URBenc = cornerMap.get(new String(URB));
        encoding.append(URBenc);

        //DLF
        char[] DLF = new char[3];
        DLF[0] = Colour.ColourToLetter(faces[BOTTOM].getBottomRow().getLeft());
        DLF[1] = Colour.ColourToLetter(faces[LEFT].getBottomRow().getRight());
        DLF[2] = Colour.ColourToLetter(faces[FRONT].getBottomRow().getLeft());
        Arrays.sort(DLF);
        int DLFenc = cornerMap.get(new String(DLF));
        encoding.append(DLFenc);

        //DRF
        char[] DRF = new char[3];
        DRF[0] = Colour.ColourToLetter(faces[BOTTOM].getBottomRow().getRight());
        DRF[1] = Colour.ColourToLetter(faces[RIGHT].getBottomRow().getRight());
        DRF[2] = Colour.ColourToLetter(faces[FRONT].getBottomRow().getRight());
        Arrays.sort(DRF);
        int DRFenc = cornerMap.get(new String(DRF));
        encoding.append(DRFenc);

        //DLB
        char[] DLB = new char[3];
        DLB[0] = Colour.ColourToLetter(faces[BOTTOM].getTopRow().getLeft());
        DLB[1] = Colour.ColourToLetter(faces[LEFT].getBottomRow().getLeft());
        DLB[2] = Colour.ColourToLetter(faces[BACK].getBottomRow().getLeft());
        Arrays.sort(DLB);
        int DLBenc = cornerMap.get(new String(DLB));
        encoding.append(DLBenc);

        //DRB
        char[] DRB = new char[3];
        DRB[0] = Colour.ColourToLetter(faces[BOTTOM].getTopRow().getRight());
        DRB[1] = Colour.ColourToLetter(faces[RIGHT].getBottomRow().getLeft());
        DRB[2] = Colour.ColourToLetter(faces[BACK].getBottomRow().getRight());
        Arrays.sort(DRB);
        int DRBenc = cornerMap.get(new String(DRB));
        encoding.append(DRBenc);

        return encoding.toString();
    }

    public String encode(Edge[] edges){
        StringBuilder encoding = new StringBuilder(edges.length);
        for(Edge edge : edges){
            encoding.append((char) ('a' + getEdgeColour(edge).ordinal()));
        }
        return encoding.toString();
    }

    public String encodeAllEdges(){
        return encode(Edge.values());
    }

    private class CubeIterator implements Iterator<Face>{
  
        int counter = 0;

        @Override
        public boolean hasNext() {
            return counter < faces.length;
        }

        @Override
        public Face next() {
            Face toReturn = faces[counter];
            counter++;
            return toReturn;
        }

        @Override
        public void remove() {

        }
    }

    /**
     * Takes a config file to build cube representation
     *
     *
     * Top
     * W W W
     * W W W
     * W W W
     *
     * Left
     * B B B
     * B B B
     * B B B
     *
     * Right
     * G G G
     * G G G
     * G G G
     *
     * @param filename
     */
    public OldCube(String filename, Context context) throws IOException, Exception{
        BufferedReader br = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(R.raw.state)));
        extractFaces(br);
    }
/*
    public String encode(Edge[] edges){
        StringBuilder encoding = new StringBuilder(edges.length);
        for(Edge edge : edges){
            encoding.append((char) ('a' + getEdgeColour(edge).ordinal()));
        }
        return encoding.toString();

    }
       public enum Edge{
        BW, RW, OW, BR, GO, GY,
        BY, RY, BO, OY, GR, GW


    }*/


    private static int U_START = 0;
    private static int L_START = 9;
    private static int R_START = 18;
    private static int D_START = 27;
    private static int F_START = 36;
    private static int B_START = 45;

    /**          U
     * 1 2 3 | 1 2 3 | 1 2 3 | 1 2 3
     * 4 L 6 | 4 F 6 | 4 R 6 | 4 B 6
     * 7 8 9 | 7 8 9 | 7 8 9 | 7 8 9
     *           D
     * @param compactRep
     * @return
     */
    public static String quickEncodeAll(String compactRep){
        //UR = U6 R2
        //UF = U8 F2
        //UL = U4 L2-
        //UB = U2 B2
        //
        //FL = F4 L6-
        //FR = F6 R4
        //BL = B6 L4-
        //BR = B4 R6
        //
        //DR = R8 D6
        //DF = F8 D2
        //DL = L8 D4-
        //DB = B8 D8

        builder.setLength(0);
        //UL
        char[] buffer = {compactRep.charAt(U_START+3), compactRep.charAt(L_START+1)};
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));

        //FL
        buffer[0] = compactRep.charAt(F_START+3);
        buffer[1] = compactRep.charAt(L_START+5);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //BL
        buffer[0] = compactRep.charAt(B_START+5);
        buffer[1] = compactRep.charAt(L_START+3);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //FU
        buffer[0] = compactRep.charAt(U_START + 7);
        buffer[1] = compactRep.charAt(F_START + 1);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //BD
        buffer[0] = compactRep.charAt(B_START+7);
        buffer[1] = compactRep.charAt(D_START+7);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //RD
        buffer[0] = compactRep.charAt(R_START+7);
        buffer[1] = compactRep.charAt(D_START+5);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //RU
        buffer[0] = compactRep.charAt(U_START+5);
        buffer[1] = compactRep.charAt(R_START+1);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //FR
        buffer[0] = compactRep.charAt(F_START + 5);
        buffer[1] = compactRep.charAt(R_START + 3);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //BU
        buffer[0] = compactRep.charAt(B_START + 1);
        buffer[1] = compactRep.charAt(U_START + 1);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //BR
        buffer[0] = compactRep.charAt(B_START + 3);
        buffer[1] = compactRep.charAt(R_START + 5);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //FD
        buffer[0] = compactRep.charAt(F_START + 7);
        buffer[1] = compactRep.charAt(D_START + 1);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));
        //DL
        buffer[0] = compactRep.charAt(L_START+7);
        buffer[1] = compactRep.charAt(D_START+3);
        Util.order(buffer);
        builder.append((char) ('a' + Edge.valueOf(new String(buffer)).ordinal()));

        return builder.toString();

    }
    /**
     * For constructing a cube faaaaaast
     *
     * U1U2U3U4U5U6U7U8U9L1L2L3L4L5L6L7L8L9R1R2R3R4R5R6R7R8R9
     * D1D2D3D4D5D6D7D8D9F1F2F3F4F5F6F7F8F9B1B2B3B4B5B6B7B8B9
     *
     * @param compactString
     */
    public OldCube(String compactString){
        assert(compactString.length() == 54);
        String[] facesStr = new String[6];
        for(int i = 0; i < facesStr.length;i++){
            facesStr[i] = compactString.substring(i*9,(i*9)+9);
        }
        buildCube(facesStr);
    }

    private void buildCube(String[] facesStr){
        int j = 0;
        for(int i = 0; i < facesStr.length; i++){
            faces[faceOrder[i]] = new Face(facesStr[i]);
        }

        faces[RIGHT] = faces[RIGHT].flipY();
        faces[BACK] = faces[BACK].flipY();
        faces[BOTTOM] = faces[BOTTOM].flipX();
    }


    public OldCube(BufferedReader br) throws IOException, Exception{
        extractFaces(br);
    }

    public Face getFace(int face){
        return faces[face];
    }

    public boolean isSolved(){
        return faces[FRONT].isSolved() && faces[BACK].isSolved() && faces[TOP].isSolved() &&
                faces[BOTTOM].isSolved() && faces[RIGHT].isSolved() && faces[LEFT].isSolved();
    }


    private void extractFaces(BufferedReader br) throws IOException, Exception{


        /*BufferedReader br = new BufferedReader(
                new InputStreamReader(context.openFileInput(filename)));*/

        try{
            String line = br.readLine();

            while(line != null){
                line = line.toUpperCase();
                /*
                 * Extract the configuration
                 */
                if(line.contains("TOP")){
                    faces[TOP] = new Face(br);
                } else if(line.contains("LEFT")){
                    faces[LEFT] = new Face(br);
                } else if(line.contains("RIGHT")){
                    faces[RIGHT] = new Face(br);
                } else if(line.contains("BOTTOM")){
                    faces[BOTTOM] = new Face(br);
                }else if(line.contains("FRONT")){
                    faces[FRONT] = new Face(br);
                }else if(line.contains("BACK")){
                    faces[BACK] = new Face(br);
                }
                line = br.readLine();
            }
        }catch (IOException e){
            Util.LogError("Cube.extractFaces()", "IOException when reading from file");
        }
    }

    //Use only one string builder and reuse it.
    private static StringBuilder builder = new StringBuilder();

    @Override
    public String toString(){
        builder.setLength(0);
        builder.append("Top\n").append(faces[TOP].toString());
        builder.append("Bottom\n").append(faces[BOTTOM].toString());
        builder.append("Left\n").append(faces[LEFT].toString());
        builder.append("Right\n").append(faces[RIGHT].toString());
        builder.append("Back\n").append(faces[BACK].toString());
        builder.append("Front\n").append(faces[FRONT].toString());

        return builder.toString();
    }

    public String toCompactString(){
        builder.setLength(0);
        for(int i = 0 ; i <faceOrder.length; i++){
            Face face = faces[faceOrder[i]];
            Face faceToAppend = faceOrder[i] == RIGHT || faceOrder[i] == BACK ? face.flipY() :
                                faceOrder[i] == BOTTOM ? face.flipX() : face;

            builder.append(faceToAppend.toCompactString());
        }
        return builder.toString();
    }

    public void performMove(Turn turn) throws Exception{

        Face faceToRotate = null;

        Face newFrontFace = null;
        Face newRightFace = null;
        Face newLeftFace = null;
        Face newBackFace = null;
        Face newTopFace = null;
        Face newBottomFace = null;

        boolean clockwise = turn.isClockwise();

        switch (turn.getMove()) {
            case Z:
                newTopFace = clockwise ? faces[LEFT] : faces[RIGHT].flipY();
                newBottomFace = clockwise ? faces[RIGHT] : faces[LEFT].flipY();
                newRightFace = clockwise? faces[TOP].flipX() : faces[BOTTOM];
                newLeftFace = clockwise ? faces[BOTTOM].flipX() : faces[TOP];

                newTopFace.rotate(clockwise);
                newBottomFace.rotate(clockwise);
                newRightFace.rotate(clockwise);
                newLeftFace.rotate(clockwise);

                faces[FRONT].rotate(clockwise);
                faces[BACK].rotate(clockwise);
                newBackFace = faces[BACK];
                newFrontFace = faces[FRONT];
                break;
            case X:
                newTopFace = clockwise ? faces[FRONT] : faces[BACK].flipX();
                newBottomFace = clockwise ? faces[BACK] : faces[FRONT].flipX();
                newBackFace = clockwise? faces[TOP].flipX() : faces[BOTTOM];
                newFrontFace = clockwise? faces[BOTTOM].flipX() : faces[TOP];

                faces[RIGHT].rotate(!clockwise);
                faces[LEFT].rotate(!clockwise);
                newRightFace = faces[RIGHT];
                newLeftFace = faces[LEFT];
                break;
            case Y:
                newRightFace = clockwise ? faces[BACK] : faces[FRONT].flipY();
                newLeftFace = clockwise ? faces[FRONT] : faces[BACK].flipY();
                newFrontFace = clockwise ? faces[RIGHT].flipY() : faces[LEFT];
                newBackFace = clockwise ? faces[LEFT].flipY() : faces[RIGHT];

                faces[TOP].rotate(clockwise);
                faces[BOTTOM].rotate(clockwise);
                newTopFace = faces[TOP];
                newBottomFace = faces[BOTTOM];

                break;
            case R:
                performSequence("Z'" + (clockwise ? "U" : "U'") + 'Z');
                return;
            case L:
                //L is wrong
                performSequence('Z' + (clockwise ? "U" : "U'") + "Z'");
                return;
            case F:
                performSequence('X' + (clockwise ? "U" : "U'") + "X'");
                return;
            case B:
                performSequence("X'" + (clockwise ? "U" : "U'") + 'X' );
                return;
            case U:
                faceToRotate = faces[TOP];
                newTopFace = faces[TOP];
                newBottomFace = faces[BOTTOM];

                newFrontFace = new Face(clockwise ? faces[RIGHT].topRow.flip() : faces[LEFT].topRow, faces[FRONT].centreRow, faces[FRONT].bottomRow);
                newLeftFace = new Face(clockwise ? faces[FRONT].topRow : faces[BACK].topRow.flip(), faces[LEFT].centreRow, faces[LEFT].bottomRow);

                newRightFace = new Face(clockwise ? faces[BACK].topRow : faces[FRONT].topRow.flip(), faces[RIGHT].centreRow, faces[RIGHT].bottomRow);
                newBackFace = new Face(clockwise ? faces[LEFT].topRow.flip() : faces[RIGHT].topRow, faces[BACK].centreRow, faces[BACK].bottomRow);
                break;
            case D:
                //WRONG
                performSequence("XX" + (clockwise ? "U" : "U'") + "XX");
                return;
            default:
                throw new Exception("Invalid move!");
        }

        if(faceToRotate != null) {
            faceToRotate.rotate(clockwise);
        }

        faces[FRONT] = newFrontFace;
        faces[RIGHT] = newRightFace;
        faces[LEFT] = newLeftFace;
        faces[BACK] = newBackFace;
        faces[BOTTOM] = newBottomFace;
        faces[TOP] = newTopFace;


    }

    public void performSequence(String sequence) throws Exception{
        //Need to parse backwards and build token array U D U F B D2 D

        Deque<Turn> moves = new ArrayDeque<Turn>();

        for(int i = sequence.length()-1; i >= 0; i--){
            char nextChar = sequence.charAt(i);

            switch(nextChar){
                case '\'':
                    //Anticlockwise turn
                    i--;
                    if(i < 0){
                        throw new Exception("Invalid move, prime notation must be preceded by a turn");
                    }else{
                        char charTurn = sequence.charAt(i);
                        Turn turn = new Turn(Turn.Move.tokenise(charTurn), false);
                        moves.addFirst(turn);
                    }
                    break;
                case '2':
                    //Turn twice
                    i--;
                    if(i < 0){
                        throw new Exception("Invalid move, 2 notation must be preceded by a turn");
                    }else{
                        char charTurn = sequence.charAt(i);
                        Turn turn = new Turn(Turn.Move.tokenise(charTurn), false);
                        //Add same move twice
                        moves.addFirst(turn);
                        moves.addFirst(turn);
                    }
                    break;
                case 'U':
                case 'D':
                case 'F':
                case 'B':
                case 'R':
                case 'L':
                case 'Z':
                case 'Y':
                case 'X':
                    //Turn clockwise
                    Turn turn = new Turn(Turn.Move.tokenise(nextChar), true);
                    moves.addFirst(turn);
                    break;
                default:
                    throw new Exception("Parsing in performSequence() Invalid move:" + nextChar);
            }
        }
        for(Turn turn : moves){
            performMove(turn);
        }
    }

    public class Face implements Iterable<Colour>{
        private Row topRow;
        private Row centreRow;
        private Row bottomRow;

        /**
         * Build face from string in format CCCCCCCCC where C is a colour
         * @param faceStr
         */
        public Face(String faceStr){
            assert(faceStr.length() == 9);
            topRow = new Row(faceStr.substring(0,3));
            centreRow = new Row(faceStr.substring(3,6));
            bottomRow = new Row(faceStr.substring(6,9));
        }

        public Face(BufferedReader br){
            String topRowStr;
            String middleRowStr;
            String bottomRowStr;
            try {
                topRowStr = br.readLine();
                middleRowStr = br.readLine();
                bottomRowStr = br.readLine();


                String[] topRowArr = topRowStr.split(" ");

                topRow = new Row(Colour.letterToColour(topRowArr[0].charAt(0)),
                        Colour.letterToColour(topRowArr[1].charAt(0)),
                        Colour.letterToColour(topRowArr[2].charAt(0)));

                String[] middleRowArr = middleRowStr.split(" ");

                centreRow = new Row(Colour.letterToColour(middleRowArr[0].charAt(0)),
                        Colour.letterToColour(middleRowArr[1].charAt(0)),
                        Colour.letterToColour(middleRowArr[2].charAt(0)));

                String[] bottomRowArr = bottomRowStr.split(" ");

                bottomRow = new Row(Colour.letterToColour(bottomRowArr[0].charAt(0)),
                        Colour.letterToColour(bottomRowArr[1].charAt(0)),
                        Colour.letterToColour(bottomRowArr[2].charAt(0)));

            }catch(IOException e){
                Util.LogError("Can't open file in creating face", "IOException when reading from file");
            }
        }
    
        public Face(Row top, Row centre, Row bottom){
            topRow = top;
            centreRow = centre;
            bottomRow = bottom;
        }

        public Row getTopRow(){
            return topRow;
        }

        public Row getCentreRow(){
            return centreRow;
        }

        public Row getBottomRow() {
            return bottomRow;
        }

        /**
         * Gets a X axis flipped version of the face
         * @return
         */
        public Face flipX(){
            return new Face(bottomRow, centreRow, topRow);
        }

        /**
         * Gets a Y axis flipped version of the face
         * @return
         */
        public Face flipY(){
            return new Face(topRow.flip(), centreRow.flip(), bottomRow.flip());
        }

        public boolean isSolved(){
            return topRow.isSolved() && centreRow.isSolved() && bottomRow.isSolved() &&
                    topRow.equals(centreRow) && centreRow.equals(bottomRow) && topRow.equals(bottomRow);
        }
        public Colour getFaceColour(){
            return centreRow.centre;
        }
        
        public String toCompactString(){
            StringBuilder builder = new StringBuilder();
            builder.append(topRow.toCompactString());
            builder.append(centreRow.toCompactString());
            builder.append(bottomRow.toCompactString());

            return builder.toString();
        }

        public void rotate(boolean clockwise){

            Row newTopRow = null;
            Row newCentreRow = null;
            Row newBottomRow = null;
            if(clockwise){
                newTopRow = new Row(bottomRow.left, centreRow.left, topRow.left);
                newCentreRow = new Row(bottomRow.centre, centreRow.centre, topRow.centre);
                newBottomRow = new Row(bottomRow.right, centreRow.right, topRow.right);


            }else{
                newTopRow = new Row(topRow.right, centreRow.right, bottomRow.right);
                newCentreRow = new Row(topRow.centre, centreRow.centre, bottomRow.centre);
                newBottomRow = new Row(topRow.left, centreRow.left, bottomRow.left);
            }
            topRow = newTopRow;
            centreRow = newCentreRow;
            bottomRow = newBottomRow;

        }

        @Override
        public String toString() {
            return topRow.toString() + "\n"
                    + centreRow.toString() + "\n"
                    + bottomRow.toString() + "\n";
        }

        @Override
        public Iterator<Colour> iterator() {
            return new FaceIterator();
        }

        /**
         * Class for iterating over a face. Goes row-wise across each row.
         * 0 1 2
         * 3 4 5
         * 6 7 8
         */
        private class FaceIterator implements Iterator<Colour>{
            Colour[] colours = {topRow.getLeft(), topRow.getCentre(), topRow.getRight(),
                    centreRow.getLeft(), centreRow.getCentre(),centreRow.getRight(),
                    bottomRow.getLeft(), bottomRow.getCentre(),bottomRow.getRight()};
            int count = 0;
            @Override
            public boolean hasNext() {
                return count < colours.length;
            }

            @Override
            public Colour next() {
                Colour toReturn = colours[count];
                count++;
                return toReturn;
            }

            @Override
            public void remove() {

            }
        }

    }

    /**
     * Class for rows
     */
    public class Row {
        private Colour left;
        private Colour right;
        private Colour centre;

        public Row(Colour left, Colour centre, Colour right){
            this.left = left;
            this.right = right;
            this.centre = centre;
        }

        public Row(String rowStr) {
            assert(rowStr.length() == 3);

            left = Colour.letterToColour(rowStr.charAt(0));
            centre = Colour.letterToColour(rowStr.charAt(1));
            right = Colour.letterToColour(rowStr.charAt(2));
        }

        public Colour getLeft(){
            return left;
        }

        public Colour getRight(){
            return right;
        }

        public Colour getCentre(){
            return centre;
        }

        public Row flip(){
            return new Row(right, centre, left);
        }

        public boolean isSolved(){
            return left == right && left == centre && right == centre;
        }

        @Override
        public boolean equals(Object row){
            Row b = (Row) row;

            boolean result = b.left == left &&
                    b.right == right &&
                    b.centre == centre;

            return result;
        }

        @Override
        public String toString() {
            return left.toString() +" "+ centre.toString()+ " "+ right.toString();
        }

        public String toCompactString() {
            StringBuilder builder = new StringBuilder();
            builder.append(left.toString());
            builder.append(centre.toString());
            builder.append(right.toString());
            return builder.toString();
        }
    }
}
