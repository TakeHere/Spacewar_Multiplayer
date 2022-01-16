package fr.takehere.spacewarserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSideConnection implements Runnable{

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private int playerId;

    public ServerSideConnection(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;

        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            dataOut.writeInt(playerId);
            dataOut.flush();

            while (true){
                String strData = dataIn.readUTF();
                String[] data = strData.split(";");
                int locX = (int) Double.parseDouble(data[0]);
                int locY = (int) Double.parseDouble(data[1]);
                double rotation = Double.parseDouble(data[2]);

                for (ServerSideConnection player : GameServer.players) {
                    if (player != this){
                        player.dataOut.writeUTF(locX + ";" + locY + ";" + rotation + ";" + playerId);
                        player.dataOut.flush();
                    }
                }
            }
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
}
