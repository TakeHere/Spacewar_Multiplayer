package fr.takehere.spacewarmultiplayer;

import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.MathUtils;
import fr.takehere.ethereal.utils.RessourcesManager;
import fr.takehere.ethereal.utils.Vector2;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ClientConnection implements Runnable{

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private int playerId;

    public static HashMap<Integer, Actor> players = new HashMap<>();

    public ClientConnection() {
        System.out.println("Client connection initialization");
        try {
            socket = new Socket("localhost", 18000);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());

            playerId = dataIn.readInt();
            System.out.println("Connected to server as player id: " + playerId);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendString(String str){
        try {
            dataOut.writeUTF(str);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                //----< Send players data >----
                Actor player = SpacewarMulti.getInstance().player;
                sendString(player.location.x + ";" + player.location.y + ";" + player.rotation);

                //----< Receive players data >----
                String strData = dataIn.readUTF();
                System.out.println(strData);
                String[] data = strData.split(";");
                int locX = (int) Double.parseDouble(data[0]);
                int locY = (int) Double.parseDouble(data[1]);
                double rotation = Double.parseDouble(data[2]);

                if (!players.containsKey(Integer.parseInt(data[3]))){
                    System.out.println("create");
                    players.put(Integer.parseInt(data[3]), new Actor(new Vector2(locX, locY), new Dimension(40,40), RessourcesManager.getImage("player"), "player", SpacewarMulti.getInstance()));
                }else {
                    players.get(Integer.parseInt(data[3])).location = new Vector2(locX, locY);
                    players.get(Integer.parseInt(data[3])).rotation = rotation;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
