package com.rubiks.lehoang.rubikssolver;

import android.content.Context;
import android.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by LeHoang on 02/01/2015.
 */
public class Cube {

    private int size;
    private Face back;
    private Face front;
    private Face top;
    private Face bottom;
    private Face right;
    private Face left;
    private Context context;
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
        this.context = context;
        extractFaces(filename);

    }


    public boolean isSolved(){
        return front.isSolved() && back.isSolved() && top.isSolved() &&
                bottom.isSolved() && right.isSolved() && left.isSolved();
    }

    /**
     * Parse the given file for configs
     * @param filename
     * @return
     * @throws IOException
     */
    private void extractFaces(String filename) throws IOException, Exception{
        BufferedReader br = new BufferedReader(
                        new InputStreamReader(context.getResources().openRawResource(R.raw.state)));

        Square.Colour topCol;
        Square.Colour leftCol;
        Square.Colour rightCol;

        try{
            StringBuilder sb = new StringBuilder();
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

    @Override
    public String toString(){
        return "top\n" + top.toString() +
                "bottom\n" + bottom.toString() +
                "left\n" + left.toString() +
                "right\n" + right.toString() +
                "back\n" + back.toString() +
                "front\n" + front.toString();
    }

    public void performMove(Turn turn) throws Exception{

        Util.LogDebug("Performing move: " + turn.getMove().toString() );
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
                performSequence("Z'" + (clockwise ? "U" : "U'") + "Z");
                return;
            case L:
                //L is wrong
                performSequence("Z" + (clockwise ? "U" : "U'") + "Z'");
                return;
            case F:
                performSequence("X" + (clockwise ? "U" : "U'") + "X'");
                return;
            case B:
                performSequence("X'" + (clockwise ? "U" : "U'") + "X" );
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
        }else{
            Util.LogDebug("No face to rotate");
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
                    Util.LogError("Parsing in performSequence()", "Invalid move");
                    throw new Exception("Parsing in performSequence() Invalid move");
            }
        }

        Util.LogDebug("About to perform moves");
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

    public class Face{
        private Row topRow;
        private Row centreRow;
        private Row bottomRow;


        public Face(Row top, Row centre, Row bottom){
            topRow = top;
            centreRow = centre;
            bottomRow = bottom;
        }


        public Face flipX(){
            return new Face(bottomRow, centreRow, topRow);
        }

        public Face flipY(){
            return new Face(topRow.flip(), centreRow.flip(), bottomRow.flip());
        }

        public boolean isSolved(){
            return topRow.isSolved() && centreRow.isSolved() && bottomRow.isSolved();
        }
        public Square.Colour getFaceColour(){
            return centreRow.centre.getColour();
        }

        public void rotate(boolean clockwise){
            Util.LogDebug("Asked to rotate " + (clockwise ? "clockwise" : "anticlockwise") + " for " + getFaceColour().toString());
            Util.LogDebug("Was : " + this.toString());
            Row newTopRow = null;
            Row newCentreRow = null;
            Row newBottomRow = null;
            if(clockwise){
                Util.LogDebug(centreRow.left.toString());
                Util.LogDebug(bottomRow.left.toString());
                Util.LogDebug(topRow.left.toString());
                newTopRow = new Row(bottomRow.left, centreRow.left, topRow.left);
                Util.LogDebug("newTopRow" + newTopRow.toString());
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

            Util.LogDebug("Now : " + this.toString());
        }

        @Override
        public String toString() {

            return topRow.toString() + "\n"
                    + centreRow.toString() + "\n"
                    + bottomRow.toString() + "\n";
        }
    }

    public class Row {
        private Square left;
        private Square right;
        private Square centre;

        public Row(Square left, Square centre, Square right){
            this.left = left;
            this.right = right;
            this.centre = centre;
        }

        public Row flip(){
            return new Row(right, centre, left);
        }

        public boolean isSolved(){
            return left == right && left == centre && right == centre;
        }

        @Override
        public String toString() {
            return left.toString() + centre.toString() + right.toString();
        }
    }
}
