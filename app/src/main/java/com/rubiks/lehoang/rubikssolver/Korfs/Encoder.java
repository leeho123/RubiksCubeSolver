package com.rubiks.lehoang.rubikssolver.Korfs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LeHoang on 10/04/2015.
 */
public class Encoder {

    private static Map<String, Integer> cache = new HashMap<String, Integer>();
    /**
     * Find the numbering for this sequence
     * @param sequence
     * @return
     */
    public static int encode(String sequence){
        if(sequence.equals("abcdef")){
            return 0;
        }else{
            return encode(getLastNonDup(sequence));
        }
    }

    private static String getLastNonDup(String sequence) {
        return null;
    }
}
