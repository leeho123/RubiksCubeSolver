package com.rubiks.lehoang.rubikssolver;
import java.lang.StringBuilder;
/**
 * Created by LeHoang on 02/01/2015.
 */
public class Square {
    public enum Colour {
        RED,BLUE,GREEN,YELLOW,WHITE,ORANGE,UNKNOWN;

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
    }
    private Colour colour;

    public Square(Colour colour){
        this.colour = colour;
    }

    public Square(char c) throws Exception{
        this.colour = Colour.letterToColour(c);
        if(this.colour == null){
            throw new Exception("Invalid colour");
        }
    }

    public Colour getColour(){
        return colour;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(colour.toString().charAt(0));

        return builder.toString();
    }

}
