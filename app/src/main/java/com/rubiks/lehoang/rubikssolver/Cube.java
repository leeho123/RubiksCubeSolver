package com.rubiks.lehoang.rubikssolver;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LeHoang on 02/01/2015.
 */
public class Cube implements Iterable<Cube.Face>{

    public static String[] moves = {"R","L","U","F","B","D",
            "R'","L'","U'","F'","B'","D'"};

    private int size;
    private Face back;
    private Face front;
    private Face top;
    private Face bottom;
    private Face right;
    private Face left;

    public static String BACK_FACE = "back";
    public static String FRONT_FACE = "front";
    public static String RIGHT_FACE = "right";
    public static String LEFT_FACE = "left";
    public static String TOP_FACE = "top";
    public static String BOTTOM_FACE = "bottom";

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
        OY,OW,RY,RW,BR,GO,
        BO,GR,BY,BW,GY,GW;
    }

    public static Edge[] firstEdges = {Edge.RY, Edge.OY, Edge.RW, Edge.OW, Edge.BR, Edge.GO};
    public static Edge[] secondEdges = {Edge.BO, Edge.GR, Edge.BY, Edge.BW, Edge.GY, Edge.GW};

    public Edge getEdgeColour(Edge edge){
        char[] edgeChar = new char[2];
        switch(edge){
            case OY:
                //BR
                edgeChar[0] = Square.Colour.ColourToLetter(back.getCentreRow().getRight().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(right.getCentreRow().getLeft().getColour());
                break;
            case OW:
                //BL
                edgeChar[0] = Square.Colour.ColourToLetter(back.getCentreRow().getLeft().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(left.getCentreRow().getLeft().getColour());
                break;
            case RY:
                //FR
                edgeChar[0] = Square.Colour.ColourToLetter(front.getCentreRow().getRight().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(right.getCentreRow().getRight().getColour());
                break;
            case GO:
                //BD
                edgeChar[0] = Square.Colour.ColourToLetter(bottom.getTopRow().getCentre().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(back.getBottomRow().getCentre().getColour());
                break;
            case BO:
                //UB
                edgeChar[0] = Square.Colour.ColourToLetter(top.getTopRow().getCentre().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(back.getTopRow().getCentre().getColour());
                break;
            case GR:
                //FD
                edgeChar[0] = Square.Colour.ColourToLetter(bottom.getBottomRow().getCentre().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(front.getBottomRow().getCentre().getColour());
                break;
            case BY:
                //RU
                edgeChar[0] = Square.Colour.ColourToLetter(top.getCentreRow().getRight().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(right.getTopRow().getCentre().getColour());
                break;
            case BW:
                //UL
                edgeChar[0] = Square.Colour.ColourToLetter(top.getCentreRow().getLeft().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(left.getTopRow().getCentre().getColour());
                break;
            case GY:
                //DR
                edgeChar[0] = Square.Colour.ColourToLetter(bottom.getCentreRow().getRight().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(right.getBottomRow().getCentre().getColour());
                break;
            case RW:
                //FL
                edgeChar[0] = Square.Colour.ColourToLetter(front.getCentreRow().getLeft().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(left.getCentreRow().getRight().getColour());
                break;
            case BR:
                //FU
                edgeChar[0] = Square.Colour.ColourToLetter(top.getBottomRow().getCentre().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(front.getTopRow().getCentre().getColour());
                break;
            case GW:
                //DL
                edgeChar[0] = Square.Colour.ColourToLetter(bottom.getCentreRow().getLeft().getColour());
                edgeChar[1] = Square.Colour.ColourToLetter(left.getBottomRow().getCentre().getColour());
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
        ULF[0] = Square.Colour.ColourToLetter(top.getBottomRow().getLeft().getColour());
        ULF[1] = Square.Colour.ColourToLetter(left.getTopRow().getRight().getColour());
        ULF[2] = Square.Colour.ColourToLetter(front.getTopRow().getLeft().getColour());
        Arrays.sort(ULF);
        int ULFenc = cornerMap.get(new String(ULF));
        encoding.append(ULFenc);

        char[] URF = new char[3];
        URF[0] = Square.Colour.ColourToLetter(top.getBottomRow().getRight().getColour());
        URF[1] = Square.Colour.ColourToLetter(right.getTopRow().getRight().getColour());
        URF[2] = Square.Colour.ColourToLetter(front.getTopRow().getRight().getColour());
        Arrays.sort(URF);
        int URFenc = cornerMap.get(new String(URF));
        encoding.append(URFenc);

        //ULB
        char[] ULB = new char[3];
        ULB[0] = Square.Colour.ColourToLetter(top.getTopRow().getLeft().getColour());
        ULB[1] = Square.Colour.ColourToLetter(left.getTopRow().getLeft().getColour());
        ULB[2] = Square.Colour.ColourToLetter(back.getTopRow().getLeft().getColour());
        Arrays.sort(ULB);
        int ULBenc = cornerMap.get(new String(ULB));
        encoding.append(ULBenc);

        //URB
        char[] URB = new char[3];
        URB[0] = Square.Colour.ColourToLetter(top.getTopRow().getRight().getColour());
        URB[1] = Square.Colour.ColourToLetter(right.getTopRow().getLeft().getColour());
        URB[2] = Square.Colour.ColourToLetter(back.getTopRow().getRight().getColour());
        Arrays.sort(URB);
        int URBenc = cornerMap.get(new String(URB));
        encoding.append(URBenc);

        //DLF
        char[] DLF = new char[3];
        DLF[0] = Square.Colour.ColourToLetter(bottom.getBottomRow().getLeft().getColour());
        DLF[1] = Square.Colour.ColourToLetter(left.getBottomRow().getRight().getColour());
        DLF[2] = Square.Colour.ColourToLetter(front.getBottomRow().getLeft().getColour());
        Arrays.sort(DLF);
        int DLFenc = cornerMap.get(new String(DLF));
        encoding.append(DLFenc);

        //DRF
        char[] DRF = new char[3];
        DRF[0] = Square.Colour.ColourToLetter(bottom.getBottomRow().getRight().getColour());
        DRF[1] = Square.Colour.ColourToLetter(right.getBottomRow().getRight().getColour());
        DRF[2] = Square.Colour.ColourToLetter(front.getBottomRow().getRight().getColour());
        Arrays.sort(DRF);
        int DRFenc = cornerMap.get(new String(DRF));
        encoding.append(DRFenc);

        //DLB
        char[] DLB = new char[3];
        DLB[0] = Square.Colour.ColourToLetter(bottom.getTopRow().getLeft().getColour());
        DLB[1] = Square.Colour.ColourToLetter(left.getBottomRow().getLeft().getColour());
        DLB[2] = Square.Colour.ColourToLetter(back.getBottomRow().getLeft().getColour());
        Arrays.sort(DLB);
        int DLBenc = cornerMap.get(new String(DLB));
        encoding.append(DLBenc);

        //DRB
        char[] DRB = new char[3];
        DRB[0] = Square.Colour.ColourToLetter(bottom.getTopRow().getRight().getColour());
        DRB[1] = Square.Colour.ColourToLetter(right.getBottomRow().getLeft().getColour());
        DRB[2] = Square.Colour.ColourToLetter(back.getBottomRow().getRight().getColour());
        Arrays.sort(DRB);
        int DRBenc = cornerMap.get(new String(DRB));
        encoding.append(DRBenc);

        return encoding.toString();
    }

    public String encode(Edge[] edges){
        StringBuilder encoding = new StringBuilder(12);
        for(Edge edge : edges){
            encoding.append((char) ('a' + getEdgeColour(edge).ordinal()));
        }
        return encoding.toString();
    }

    private class CubeIterator implements Iterator<Face>{
        Face[] faces = {top, right, front, bottom, left, back};
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
    public Cube(String filename, Context context) throws IOException, Exception{
        BufferedReader br = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(R.raw.state)));
        extractFaces(br);
        faceMapInit();
    }

    /**
     * For constructing a cube faaaaaast
     * @param compactString
     */
    public Cube(String compactString){

    }
    private void faceMapInit(){
        faceMap.put(BACK_FACE, back);
        faceMap.put(FRONT_FACE, front);
        faceMap.put(RIGHT_FACE, right);
        faceMap.put(LEFT_FACE, left);
        faceMap.put(BOTTOM_FACE, bottom);
        faceMap.put(TOP_FACE, top);
    }

    public Cube(BufferedReader br) throws IOException, Exception{
        extractFaces(br);
        faceMapInit();
    }

    public Face getFace(String face){
        return faceMap.get(face);
    }

    public boolean isSolved(){
        return front.isSolved() && back.isSolved() && top.isSolved() &&
                bottom.isSolved() && right.isSolved() && left.isSolved();
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
                    top = extractFace(br);
                } else if(line.contains("LEFT")){
                    left = extractFace(br);
                } else if(line.contains("RIGHT")){
                    right = extractFace(br);
                } else if(line.contains("BOTTOM")){
                    bottom = extractFace(br);
                }else if(line.contains("FRONT")){
                    front = extractFace(br);
                }else if(line.contains("BACK")){
                    back = extractFace(br);
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
        builder.append("Top\n").append(top.toString());
        builder.append("Bottom\n").append(bottom.toString());
        builder.append("Left\n").append(left.toString());
        builder.append("Right\n").append(right.toString());
        builder.append("Back\n").append(back.toString());
        builder.append("Front\n").append(front.toString());

        return builder.toString();
    }

    public String toCompactString(){

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
                newTopFace = clockwise ? left : right.flipY();
                newBottomFace = clockwise ? right : left.flipY();
                newRightFace = clockwise? top.flipX() : bottom;
                newLeftFace = clockwise ? bottom.flipX() : top;

                newTopFace.rotate(clockwise);
                newBottomFace.rotate(clockwise);
                newRightFace.rotate(clockwise);
                newLeftFace.rotate(clockwise);

                front.rotate(clockwise);
                back.rotate(clockwise);
                newBackFace = back;
                newFrontFace = front;
                break;
            case X:
                newTopFace = clockwise ? front : back.flipX();
                newBottomFace = clockwise ? back : front.flipX();
                newBackFace = clockwise? top.flipX() : bottom;
                newFrontFace = clockwise? bottom.flipX() : top;

                right.rotate(!clockwise);
                left.rotate(!clockwise);
                newRightFace = right;
                newLeftFace = left;
                break;
            case Y:
                newRightFace = clockwise ? back : front.flipY();
                newLeftFace = clockwise ? front : back.flipY();
                newFrontFace = clockwise ? right.flipY() : left;
                newBackFace = clockwise ? left.flipY() : right;

                top.rotate(clockwise);
                bottom.rotate(clockwise);
                newTopFace = top;
                newBottomFace = bottom;

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
                faceToRotate = top;
                newTopFace = top;
                newBottomFace = bottom;

                newFrontFace = new Face(clockwise ? right.topRow.flip() : left.topRow, front.centreRow, front.bottomRow);
                newLeftFace = new Face(clockwise ? front.topRow : back.topRow.flip(), left.centreRow, left.bottomRow);

                newRightFace = new Face(clockwise ? back.topRow : front.topRow.flip(), right.centreRow, right.bottomRow);
                newBackFace = new Face(clockwise ? left.topRow.flip() : right.topRow, back.centreRow, back.bottomRow);
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

        front = newFrontFace;
        right = newRightFace;
        left = newLeftFace;
        back = newBackFace;
        bottom = newBottomFace;
        top = newTopFace;


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

    private Face extractFace(BufferedReader br) throws Exception{
        String topRow;
        String middleRow;
        String bottomRow;

        Face extracted = null;
        try {
            topRow = br.readLine();
            middleRow = br.readLine();
            bottomRow = br.readLine();


            String[] topRowArr = topRow.split(" ");

            Row top = new Row(new Square(topRowArr[0].charAt(0)),
                              new Square(topRowArr[1].charAt(0)),
                              new Square(topRowArr[2].charAt(0)));

            String[] middleRowArr = middleRow.split(" ");

            Row middle = new Row(new Square(middleRowArr[0].charAt(0)),
                                 new Square(middleRowArr[1].charAt(0)),
                                 new Square(middleRowArr[2].charAt(0)));

            String[] bottomRowArr = bottomRow.split(" ");

            Row bottom = new Row(new Square(bottomRowArr[0].charAt(0)),
                                 new Square(bottomRowArr[1].charAt(0)),
                                 new Square(bottomRowArr[2].charAt(0)));

            extracted = new Face(top, middle, bottom);
        }catch(IOException e){
            Util.LogError("Cube.extractFace()", "IOException when reading from file");
        }
        return extracted;
    }

    public class Face implements Iterable<Square>{
        private Row topRow;
        private Row centreRow;
        private Row bottomRow;


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
        public Square.Colour getFaceColour(){
            return centreRow.centre.getColour();
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
        public Iterator<Square> iterator() {
            return new FaceIterator();
        }

        /**
         * Class for iterating over a face. Goes row-wise across each row.
         * 0 1 2
         * 3 4 5
         * 6 7 8
         */
        private class FaceIterator implements Iterator<Square>{
            Square[] squares = {topRow.getLeft(), topRow.getCentre(), topRow.getRight(),
                    centreRow.getLeft(), centreRow.getCentre(),centreRow.getRight(),
                    bottomRow.getLeft(), bottomRow.getCentre(),bottomRow.getRight()};
            int count = 0;
            @Override
            public boolean hasNext() {
                return count < squares.length;
            }

            @Override
            public Square next() {
                Square toReturn = squares[count];
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
        private Square left;
        private Square right;
        private Square centre;

        public Row(Square left, Square centre, Square right){
            this.left = left;
            this.right = right;
            this.centre = centre;
        }

        public Square getLeft(){
            return left;
        }

        public Square getRight(){
            return right;
        }

        public Square getCentre(){
            return centre;
        }

        public Row flip(){
            return new Row(right, centre, left);
        }

        public boolean isSolved(){
            return left.getColour() == right.getColour() && left.getColour() == centre.getColour() && right.getColour() == centre.getColour();
        }

        @Override
        public boolean equals(Object row){
            Row b = (Row) row;

            boolean result = b.left.getColour() == left.getColour() &&
                    b.right.getColour() == right.getColour() &&
                    b.centre.getColour() == centre.getColour();

            return result;
        }

        @Override
        public String toString() {
            return left.toString() +" "+ centre.toString()+ " "+ right.toString();
        }
    }
}
