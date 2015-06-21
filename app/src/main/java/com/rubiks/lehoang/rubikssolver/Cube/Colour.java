package com.rubiks.lehoang.rubikssolver.Cube;

/**
 * Created by LeHoang on 02/01/2015.
 */

public enum Colour {
    BLUE,GREEN,ORANGE,RED,YELLOW,WHITE,UNKNOWN;

    public static Colour opposite(Colour colour){
        switch(colour){
            case RED:
                return ORANGE;
            case BLUE:
                return GREEN;
            case GREEN:
                return BLUE;
            case ORANGE:
                return RED;
            case WHITE:
                return YELLOW;
            case YELLOW:
                return WHITE;
            default:
                return null;
        }
    }

    public static char ColourToLetter(Colour c){
        return c.name().charAt(0);
    }
    public static Colour letterToColour(char c){
        char upper = Character.toUpperCase(c);

        Colour[] cols = Colour.values();

        for(Colour col: cols){
            if (col.toString().charAt(0) == upper){
                return col;
            }
        }
        return null;
    }


    @Override
    public String toString(){
        return Character.toString(ColourToLetter(this));
    }

}
