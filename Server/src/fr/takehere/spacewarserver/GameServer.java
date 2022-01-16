package fr.takehere.spacewarserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private ServerSocket ss;
    private int numPlayers = 0;
    public static List<ServerSideConnection> players = new ArrayList<>();

    public GameServer(){
        System.out.println("<-- Spacewar Gameserver >--");

        try {
            ss = new ServerSocket(18000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections(){
        System.out.println("Waiting for connection");
        try {
            while (true){
                Socket s = ss.accept();
                numPlayers++;

                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                players.add(ssc);
                Thread t = new Thread(ssc);
                t.start();

                System.out.println("Player" + numPlayers + " is now connected !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
