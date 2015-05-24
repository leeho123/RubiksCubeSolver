package com.rubiks.lehoang.rubikssolver;

import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import org.kociemba.twophase.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by LeHoang on 11/05/2015.
 */
public class Server {

    public static int portNumber = 12345;

    public static void setup(){
        String solution = "RLU";
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);



            while(true){
                //Listen for a connection
                clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket.getInetAddress());
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket
                        .getInputStream()));

                System.out.println("Waiting for input");
                String input = in.readLine();
                input = input.trim();
                System.out.println("Got " + input);
                if(input == null){
                    break;
                }
                //solve
                CompactCube cube = new CompactCube(input);

                int maxDepth = 24, maxTime = 5;
                boolean useSeparator = false;
                solution = Search.solution(CompactCube.toKociemba(cube), maxDepth, maxTime, useSeparator);

                out.println(solution);
                clientSocket.close();
                System.out.println("sent back solution");
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

    public static void main(String[] args){
        Server.setup();
    }

}
