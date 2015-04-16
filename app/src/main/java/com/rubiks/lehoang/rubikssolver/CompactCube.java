package com.rubiks.lehoang.rubikssolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LeHoang on 12/04/2015.
 */
public class CompactCube {

    /**
     * Commonly used encodings
     */
    public static final int NO_CORNER_ENCODINGS = 88179840;
    public static final int NO_EDGE_ENCODINGS = 42577920;
    public static final int secondEdgeSolved = 23442432;
    public static final int firstEdgeSolved = 0;
    public static final int cornersSolved = 0;
    /**
     * Number of corner perms and orientations 8 * 3
     * Number of edge  perms and orientations 12 * 2
     */
    private static final int PO = 24;

    /**
     * 3 turns for each of the 6 moves
     */
    public static final int NUMMOVES =18;

    //Maps byte i to the corresponding Corners and Edges
    private static String[] byteToStringEdgeMap = {"UB","BU",
                                                   "UL","LU",
                                                   "UR","RU",
                                                   "UF","FU",
                                                   "LB","BL",
                                                   "RB","BR",
                                                   "LF","FL",
                                                   "RF","FR",
                                                   "DB","BD",
                                                   "DL","LD",
                                                   "DR","RD",
                                                   "DF","FD"};


    private static String[] byteToStringCornerMap = {"UBL", "URB", "ULF", "UFR", "DLB", "DBR", "DFL", "DRF",
                                                    "LUB", "BUR", "FUL", "RUF", "BDL", "RDB", "LDF", "FDR",
                                                    "BLU", "RBU", "LFU", "FRU", "LBD", "BRD", "FLD", "RFD"};


    private static Map<String, Byte> humanToCompMap;{
        humanToCompMap = new HashMap<String, Byte>();
        for(int i = 0 ; i < PO; i++){
            humanToCompMap.put(byteToStringCornerMap[i], (byte) i);
            humanToCompMap.put(byteToStringEdgeMap[i], (byte) i);
        }
    }

    /**
     * Trying out a super compact representation of a cube at a bit level to make
     * use of fast shifting operators
     */
    enum Face{
        U,F,R,D,B,L;

        public static Face getOpposite(Face face){
           Face[] faces = Face.values();
           return faces[(face.ordinal() + 3) % 6];
        }
    }


    public static final byte[] SOLVED_CORNERS;
    public static final byte[] SOLVED_EDGES;
    static{
        SOLVED_CORNERS = new byte[8];
        for(int i = 0; i < SOLVED_CORNERS.length; i++){
            SOLVED_CORNERS[i] = (byte) getCornerVal(i,0);
        }

        SOLVED_EDGES = new byte[12];
        for(int i = 0; i < SOLVED_EDGES.length; i++){
            SOLVED_EDGES[i] = (byte) getEdgeVal(i,0);
        }
    }


    public static final int U = 0;
    public static final int U2= 1;
    public static final int UPRIME = 2;
    public static final int F = 3;
    public static final int F2 =4;
    public static final int FPRIME =5;
    public static final int R = 6;
    public static final int R2 = 7;
    public static final int RPRIME = 8;
    public static final int D = 9;
    public static final int D2 = 10;
    public static final int DPRIME = 11;
    public static final int B = 12;
    public static final int B2 = 13;
    public static final int BPRIME = 14;
    public static final int L = 15;
    public static final int L2 = 16;
    public static final int LPRIME = 17;

    public static final String[] MoveToString= {"U","U2","U'",
                                                "F","F2","F'",
                                                "R","R2","R'",
                                                "D","D2","D'",
                                                "B","B2","B'",
                                                "L","L2","L'"};

    public static final int[] INV_MOVES;

    /**
     * So we can quickly search if a move flips an edge
     * Only R and L moves can flip
     * UFRDBL
     */
    private static final byte[] edgeFlip = {0,0,1,0,0,1};

    /**
     * U and D turns can never change corner perms
     */
    private static final byte[][] cornerFlip = {{0,0,0,0},{1,2,1,2},{1,2,1,2},{0,0,0,0},{1,2,1,2},{1,2,1,2}};


    /**
     * Shows how edges change with a move i.e. 0->2 2->3 3->1 1->0 for U
     */
    private static final byte[][] edgePerm = {{0,2,3,1},{3,7,11,6},{2,5,10,7},{9,11,10,8},{0,4,8,5},{1,6,9,4}};


    /**
     * Shows how moves change corner permutations
     * first index is the move performed and gives an array containing the faclets that changed
     *
     */
    private static final byte[][] cornerPerm = {{0,1,3,2},{2,3,7,6},{3,1,5,7},{4,6,7,5},{1,0,4,5},{0,2,6,4}};

    /**
     * Shows the full transition of a move on a particular faclet of a corner.
     * Defaults to nothing changing at all
     */
    private static final byte[][] cornerTransitions;

    /**
     * Shows a full transition of a move on a particular faclet of an edge
     * Defaults to nothing changing at all
     */
    private static final byte[][] edgeTransitions;
    static{
        INV_MOVES = new int[NUMMOVES];
        for(int i = 0; i < NUMMOVES; i++){
            INV_MOVES[i] = 3 * (i/3) + (NUMMOVES -i -1) % 3;
        }

        cornerTransitions = new byte[NUMMOVES][PO];
        edgeTransitions = new byte[NUMMOVES][PO];
        for(int m = 0; m < NUMMOVES; m++){
            for(int c = 0; c < PO; c++){
                cornerTransitions[m][c] = (byte) c;
                edgeTransitions[m][c] = (byte) c;
            }
        }
        //For each face turn
        for(int f=0; f<6; f++){
            //For each possible rotation for each face turn
            for(int turn=0; turn<3;turn++){
                //For each move we can do
                int move = f * 3 + turn;

                boolean isQuarterTurn = (turn==0 || turn==2);
                //How much to increment the permutation
                int permInc = turn+1;

                //For each cubies per face. 4 edges, 4 corners
                //i is the current cubie for face f
                for(int i = 0; i < 4;i++) {
                    int numTwists = ((i + (permInc))) % 4;
                    //For each edge orientation
                    for(int orient = 0; orient < 2; orient++) {
                        //Intial orientation
                        int newOrientation = orient;

                        //If the turn is a quarter turn then flip the
                        if(isQuarterTurn){
                            newOrientation ^= edgeFlip[f];
                        }
                        // m on edge val gives
                        edgeTransitions[move][getEdgeVal(edgePerm[f][i], orient)] =
                                (byte) getEdgeVal(edgePerm[f][numTwists], newOrientation);
                    }
                    //Corner orientations
                    for(int orient = 0; orient < 3; orient++){
                        int newOrientation = orient;
                        if(isQuarterTurn){
                            newOrientation = (cornerFlip[f][i] + newOrientation) % 3;
                        }
                        cornerTransitions[move][getCornerVal(cornerPerm[f][i], orient)] =
                                (byte) getCornerVal(cornerPerm[f][numTwists], newOrientation);
                    }
                }
            }
        }

    }


    /**
     * 3 bits per corner
     * 8 corners so 3 bytes total
     * requires more memory accesses but saves space
     * 3 possible orientations. Correct orientation = 0 if Blue or Green are facing Up or Down.
     * Anticlockwise turns = 1
     * Clockwise turns = 2
     * so 2 bits per corner = 16 bits total
     *
     * First 3 bits determine perm, next 2 bits determine orientation
     */
    private byte[] corners;


    /**
     * 12 edges total need 4 bits per edge so 48 bits total = 6 bytes
     *
     * 1 bit per edge orientation but 12 not divisible by 8 so make 16 bits
     * edge orientations flip with R or L moves
     * First bit determines orientation, next 4 bits determine perm
     */
    private byte[] edges;

    /**
     * Gets an edges value based on its orientation and permutation
     * @param perm
     * @param ori
     * @return
     */
    private static int getEdgeVal(int perm, int ori){
        return perm * 2 + ori;
    }

    /**
     * Gets a corners value based on its orientation and permutation.
     * Slightly different to edge because orientation value comes after perm value
     * @param perm
     * @param ori
     * @return
     */
    private static int getCornerVal(int perm, int ori){
        return ori * 8 + perm;
    }


    /**
     * Gives the indexes for the edges in the strRep
     */
    private static final int[][] convEdgeLookup = {{1, 46}, {46,1}, //UB BU
                                             {3, 37}, {37,3}, //UL LU
                                             {5, 10}, {10,5}, //UR RU
                                             {7, 19}, {19,7}, //UF FU
                                             {39, 50},{50,39},//LB BL
                                             {14, 48},{48,14},//RB BR
                                             {41, 21},{21,41},//LF FL
                                             {12, 23},{23,12},//RF FR
                                             {34, 52},{52,34},//DB BD
                                             {30, 43},{43,30},//DL LD
                                             {32, 16},{16,32},//DR
                                             {28, 25},{25,28} //DF
                                             };

    /**
     * Gives the indexes for the corners in the strRep
     */
    private static final int[][] convCornerLookup = {{0,47,36},//UBL
                                               {2,11,45},//URB
                                               {6,38,18},//ULF
                                               {8,20,9},//UFR
                                               {33,42,53},//DLB
                                               {35,51,17},//DBR
                                               {27,24,44},//DFL
                                               {29,15,26},//DRF
                                               {36,0,47}, //LUB
                                               {45,2,11}, //BUR
                                               {18,6,38}, //FUL
                                               {9,8,20}, //RUF
                                               {53,33,42}, //BDL
                                               {17,35,51}, //RDB
                                               {44,27,24}, //LDF
                                               {26,29,15}, //FDR
                                               {47,36,0}, //BLU
                                               {11,45,2}, //RBU
                                               {38,18,6}, //LFU
                                               {20,9,8}, //FRU
                                               {42,53,33}, //LBD
                                               {51,17,35}, //BRD
                                               {24,44,27}, //FLD
                                               {15,26,29}, //RFD
                                               };

    public CompactCube(CompactCube cube){
        corners = Arrays.copyOf(cube.corners, cube.corners.length);
        edges = Arrays.copyOf(cube.edges, cube.edges.length);
    }

    /**
     * For constructing a cube
     *
     * U1, U2, U3, U4, U5, U6, U7, U8, U9, R1, R2, R3, R4, R5, R6, R7, R8, R9,
     * F1, F2, F3, F4, F5, F6, F7, F8, F9, D1, D2, D3, D4, D5, D6, D7, D8, D9,
     * L1, L2, L3, L4, L5, L6, L7, L8, L9, B1, B2, B3, B4, B5, B6, B7, B8, B9
     *
     * U1U2U3U4U5U6U7U8U9L1L2L3L4L5L6L7L8L9R1R2R3R4R5R6R7R8R9
     * D1D2D3D4D5D6D7D8D9F1F2F3F4F5F6F7F8F9B1B2B3B4B5B6B7B8B9
     *
     * @param
     */
    public CompactCube(String strRep){
        edges = new byte[12];
        //Edges
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < edges.length; i++){
            for(int j = 0; j<2;j++) {

                builder.append(strRep.charAt(convEdgeLookup[i * 2][j]));

            }

            int val = humanToCompMap.get(builder.toString());


            //System.out.println("Edge:"+ (val/2) + " is in position " + getEdgeVal(i, (val%2)));

            edges[val/2] = (byte) getEdgeVal(i ,(val % 2));

            builder.setLength(0);
        }

        corners = new byte[8];
        //Corners
        for(int i = 0; i < corners.length;  i++){
            for(int j = 0; j < 3; j++){
                builder.append(strRep.charAt(convCornerLookup[i][j]));
            }

            int val = humanToCompMap.get(builder.toString());

            //System.out.println("Corner:"+(val%8) + " is in position " + getCornerVal(i, (val/8)));

            corners[val % 8] = (byte) getCornerVal(i, (3-(val/8))%3);

            builder.setLength(0);
        }
    }

    /**
     * Convert compact string to kociemba
     * @param cube
     * @return
     */
    public static String toKociemba(CompactCube cube) {
        StringBuilder builder = new StringBuilder(54);
        builder.append("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");

        byte[] corners = cube.corners;
        byte[] edges = cube.edges;
        //UFRDBL

        for (int i = 0; i < edges.length; i++) {
            //i is the edge we want the location of

            String edge = byteToStringEdgeMap[i * 2];


            for (int j = 0; j < 2; j++) {
                char col = edge.charAt(j);
                //Get indexs for this edge
                int location = convEdgeLookup[edges[i]][j];
                builder.setCharAt(location, col);
            }
        }

        for (int i = 0; i < corners.length;i++) {
            String corner = byteToStringCornerMap[i];
            for (int j = 0; j < 3; j++) {
                char col = corner.charAt(j);
                int location = convCornerLookup[corners[i]][j];
                builder.setCharAt(location, col);
            }
        }


        assert (builder.length() == 54);
        return builder.toString();
    }

    /**
     * Initialise to solved cube
     */
    public CompactCube(){
        edges = Arrays.copyOf(SOLVED_EDGES, SOLVED_EDGES.length);
        corners = Arrays.copyOf(SOLVED_CORNERS, SOLVED_CORNERS.length);
    }

    /**
     * Peform a move 0 - 17. Total of 18 moves U, U2, U',R ,R2, R'....
     * We dont want to call any methods here to improve the speed
     * @param move
     */
    public void move(int move){
        corners[0] = cornerTransitions[move][corners[0]];
        corners[1] = cornerTransitions[move][corners[1]];
        corners[2] = cornerTransitions[move][corners[2]];
        corners[3] = cornerTransitions[move][corners[3]];
        corners[4] = cornerTransitions[move][corners[4]];
        corners[5] = cornerTransitions[move][corners[5]];
        corners[6] = cornerTransitions[move][corners[6]];
        corners[7] = cornerTransitions[move][corners[7]];


        edges[0] = edgeTransitions[move][edges[0]];
        edges[1] = edgeTransitions[move][edges[1]];
        edges[2] = edgeTransitions[move][edges[2]];
        edges[3] = edgeTransitions[move][edges[3]];
        edges[4] = edgeTransitions[move][edges[4]];
        edges[5] = edgeTransitions[move][edges[5]];
        edges[6] = edgeTransitions[move][edges[6]];
        edges[7] = edgeTransitions[move][edges[7]];
        edges[8] = edgeTransitions[move][edges[8]];
        edges[9] = edgeTransitions[move][edges[9]];
        edges[10] = edgeTransitions[move][edges[10]];
        edges[11] = edgeTransitions[move][edges[11]];
    }

    public static void moveEdges(int move, byte[] edges){
        edges[0] = edgeTransitions[move][edges[0]];
        edges[1] = edgeTransitions[move][edges[1]];
        edges[2] = edgeTransitions[move][edges[2]];
        edges[3] = edgeTransitions[move][edges[3]];
        edges[4] = edgeTransitions[move][edges[4]];
        edges[5] = edgeTransitions[move][edges[5]];
        edges[6] = edgeTransitions[move][edges[6]];
        edges[7] = edgeTransitions[move][edges[7]];
        edges[8] = edgeTransitions[move][edges[8]];
        edges[9] = edgeTransitions[move][edges[9]];
        edges[10] = edgeTransitions[move][edges[10]];
        edges[11] = edgeTransitions[move][edges[11]];
    }
    public static void moveCorners(int move, byte[] corners){

        corners[0] = cornerTransitions[move][corners[0]];
        corners[1] = cornerTransitions[move][corners[1]];
        corners[2] = cornerTransitions[move][corners[2]];
        corners[3] = cornerTransitions[move][corners[3]];
        corners[4] = cornerTransitions[move][corners[4]];
        corners[5] = cornerTransitions[move][corners[5]];
        corners[6] = cornerTransitions[move][corners[6]];
        corners[7] = cornerTransitions[move][corners[7]];
    }

    /**
     * Byte has 5 bits. LSB bit is orientation. Last 4 are perm.
     * @param piece
     * @return
     */
    public static byte getEdgeOri(byte piece){
        return (byte) (piece & 1);
    }

    /**
     * Byte has 5 bits. First 3 are perm (RIGHT TO LEFT), last 2 are orientation. Mask by 00011
     * to get the last 2 bits.
     * @param piece
     * @return
     */
    public static byte getCornerOri(byte piece){
        return (byte) (piece >>> 3);
    }

    public static byte getEdgePerm(byte piece){
        return (byte) (piece >>> 1);
    }

    public static byte getCornerPerm(byte piece){
        return (byte) (piece & 7);
    }

    /**
     * Uses factorial numbering system to get a unique number for the corner/edge permutations
     * @return
     */
    public static int getFactorialNumber(byte[] toEncode){

        byte[] factorialBase = new byte[toEncode.length];

        for(int i = 0; i < toEncode.length; i++){
            int diff = 0;
            int maxVal = toEncode.length - 1 - i;
            for(int j = toEncode.length-1; j >=0; j--){

                if(toEncode[j] == i){
                    break;
                }

                if(toEncode[j] > i){
                    diff++;
                }
            }
            factorialBase[i] = (byte) (maxVal - diff);
        }


        //Convert to base 10
        int encoding = factorialBase[0];
        for(int i = 1; i < factorialBase.length; i++){

            int base = factorialBase.length - i;
            encoding = (encoding * base) + factorialBase[i];
        }
        return encoding;
    }

    /**
     * Only 1 third of all corner orientations are reachable so instead of 3^8 states we have
     * 3^7
     * @param toEncode
     * @param base
     * @return
     */
    public static int getBase10FromBaseN(byte[] toEncode, int base){
        int result = toEncode[0];
        for(int i = 1; i < toEncode.length; i++){
            result = (result * base) + toEncode[i];
        }
        return result;
    }

    public int encodeCorners(){
        return encodeCorners(corners);
    }

    public static int encodeCorners(byte[] corners){
        //Extract permutations
        byte[] cornerPerms = new byte[corners.length];
        byte[] cornerOri = new byte[corners.length];
        for(int i=0; i < corners.length;i++){
            cornerPerms[i] = getCornerPerm(corners[i]);
            cornerOri[i] = getCornerOri(corners[i]);
        }

        //Get rid of first one because it can be determined by the others.
        //Sum of corner orientations must be divisible by 3
        cornerOri[0] = 0;


        int permNum = getFactorialNumber(cornerPerms);
        int oriNum = getBase10FromBaseN(cornerOri, 3);

        //Combine them like counting the cartesian product of the set
        //2187 possible orientations (3^7) precomputed for efficiency

        return oriNum + permNum * 2187;
    }

    /**
     * npk index for lexicographical ordering
     * @param toEncode
     * @param n
     * @return
     */
    public static int getNPKIndex(byte[]toEncode, int n){
        int k = toEncode.length;
        int result = 0;
        for(int i = 0; i < k;i++){
            int c = coeff(n-(i+1), n - k);
            int diff = 0;

            // Count number of used digits before i
            for(int j = 0; j < i; j++){
                if(toEncode[j] < toEncode[i]){
                    diff++;
                }
            }

            result += (toEncode[i] - diff)*c;
        }

        return result;
    }

    //n!/k! assumes n >= k
    public static int coeff(int n, int k){
        assert(n >= k);
        int coeff = 1;
        for(int i = n; i > k;i--){
            coeff *= i;
        }
        return coeff;
    }

    public int encodeEdges(){
        byte[] edgePerms = new byte[edges.length];
        byte[] edgeOri = new byte[edges.length];

        for(int i = 0; i < edges.length; i++){
            edgePerms[i] = getEdgePerm(edges[i]);
            edgeOri[i] = getEdgeOri(edges[i]);
        }

        int permNum = getFactorialNumber(edgePerms);
        int oriNum = getBase10FromBaseN(edgeOri, 2);

        //2^12 precomputed for speed
        return oriNum + permNum * 4096;
    }

    public int encodeFirst(){
        return encodeFirst(edges);
    }

    public int encodeSecond(){
        return encodeSecond(edges);
    }
    public static int encodeFirst(byte[] edges){
        return encode6Of12Edges(edges,0);
    }

    public static int encodeSecond(byte[] edges){
        return encode6Of12Edges(edges,6);

    }

    /**
     * Given 6 edges, find its encoding in the 12 edge space
     * @param edges6Of12
     * @return
     */
    public static int encode6Of12Edges(byte[] edges6Of12, int start){
        byte[] edgePerms = new byte[6];
        byte[] edgeOri = new byte[6];

        for(int i = 0; i < 6; i++){
            edgePerms[i] = getEdgePerm(edges6Of12[i+start]);
            edgeOri[i] = getEdgeOri(edges6Of12[i+start]);
        }

        int perm = getNPKIndex(edgePerms, 12);
        int oriNum = getBase10FromBaseN(edgeOri,2);

        return oriNum + perm * 64;
    }

    public static boolean isSolved(CompactCube cube){
        return Arrays.equals(cube.corners, SOLVED_CORNERS) &&
                Arrays.equals(cube.edges, SOLVED_EDGES);
    }

    @Override
    public boolean equals(Object b){
        CompactCube cube = (CompactCube) b;
        return Arrays.equals(corners, cube.corners) &&
                 Arrays.equals(edges, cube.edges);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(corners);
        result = 31 * result + Arrays.hashCode(edges);
        return result;
    }
}
