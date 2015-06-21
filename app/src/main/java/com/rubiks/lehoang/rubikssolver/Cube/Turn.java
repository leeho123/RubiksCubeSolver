package com.rubiks.lehoang.rubikssolver.Cube;

/**
 * Created by LeHoang on 04/01/2015.
 */
public class Turn {
    Move move;
    boolean clockwise;


    public Turn(Move move, boolean clockwise){
        this.move = move;
        this.clockwise = clockwise;
    }

    public Move getMove(){
        return move;
    }

    public boolean isClockwise(){
        return clockwise;
    }


    public enum Move{
        U,D,R,L,F,B,Z,Y,X;

        public static Move tokenise(char c){
            Move[] moves = Move.values();
            for(Move move : moves){
                if(c == move.name().charAt(0)){
                    return move;
                }
            }
            return null;
        }

        /**
         * Inverts a sequence
         * @param s
         * @return
         */
        public static String invert(String s){
            //TODO
            return null;
        }


    }
}
