package com.rubiks.lehoang.rubikssolver;

import com.rubiks.lehoang.rubikssolver.Cube.Cube;
import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import org.kociemba.twophase.Search;
import org.kociemba.twophase.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

/**
 * Created by LeHoang on 11/05/2015.
 */
public class Server {

    public static int portNumber = 12345;

    private boolean terminate;

    public void setup(){
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(portNumber);



            while(true) {
                //Listen for a connection
                clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket.getInetAddress());
                final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket
                        .getInputStream()));

                terminate = false;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(terminate){
                                break;
                            }
                            System.out.println("Waiting for input");
                            String input = null;
                            try {
                                input = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            handleMessage(input);
                        }
                    }
                });

                solution = "WORKING";

                thread.start();


                Thread solutionPoll = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        synchronized (solutionLock) {
                            while (true) {
                                if (!solution.equals("WORKING")) {
                                    break;
                                }
                                try {
                                    solutionLock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            System.out.println("Solution found " + solution);

                            out.println(solution);
                            out.flush();

                            terminate = true;
                        }
                    }
                });

                solutionPoll.start();

                /*
                //solve
                CompactCube cube = new CompactCube(input);

                int maxDepth = 24, maxTime = 5;
                boolean useSeparator = false;
                solution = Search.solution(CompactCube.toKociemba(cube), maxDepth, maxTime, useSeparator);
*/

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                clientSocket.close();
            }catch (IOException e){

            }
        }
    }

    Korfs korfs;
    String solution;
    String solution2;
    Object solutionLock = new Object();
    public void handleMessage(String input){
        if(input == null){
            return;
        }
        input = input.trim();
        System.out.println("Got " + input);

        if(Tools.verify(input) == 0){
            korfs = new Korfs();
            final Cube cube = new Cube(input);
            solution2 = Search.solution(Cube.toKociemba(cube),30,5,false);
            int length = Util.countMoves(transform(solution2));
            length = length > 20 ? 20 : length;
            final int finalLength = length;
            System.out.println("Got " + transform(solution2) + " of length "+ finalLength);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (solutionLock) {
                        try {
                            solution = korfs.idaStarMultiKorfs(finalLength, cube);
                            System.out.println("Got something here");
                            try {
                                solution = pickBest(solution, solution2);
                            } catch (Cube.InvalidMoveException e) {
                                e.printStackTrace();
                            }
                            solutionLock.notifyAll();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }else if (input.equals("STOP")){
            System.out.println("Stopping...");
            korfs.cancelMulti();
            System.out.println("STOPPED");
        }
    }


    public static String transform(String solution){
        String replaced = solution.replace('\'', '3');
        replaced = replaced.replaceAll("\\s+","");
        return replaced;
    }
    public static String pickBest(String solution1 , String solution2) throws Cube.InvalidMoveException {
        System.out.println("Picking best between " + solution1 + " and " + solution2);
        if(solution1 == null){
            return solution2;
        }
        if(solution2 == null){
            return solution1;
        }
        solution1 = transform(solution1);
        solution2 = transform(solution2);
        return Korfs.estimateCost(solution1) < Korfs.estimateCost(solution2) ? solution1 : solution2;

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Korfs korfs = new Korfs();
        korfs.idaStarMultiKorfs(20, new Cube());
        //Search.solution(CompactCube.toKociemba(new CompactCube()), 30, 5, false);

        Server server = new Server();
        server.setup();
    }

}
