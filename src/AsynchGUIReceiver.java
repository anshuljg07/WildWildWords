import javafx.application.Platform;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AsynchGUIReceiver implements Runnable{
    private DatagramSocket socket;
    private Integer clientID;
    private String playerName;

    public AsynchGUIReceiver(DatagramSocket socket){
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    public AsynchGUIReceiver(DatagramSocket socket, Integer ID, String playerName){
        this.socket = socket;
        clientID = ID;
        this.playerName = playerName;
    }


    @Override
    public void run() {
        System.out.println("GUI Thread now waiting :)");
        while (true){
            try{
                byte[] data = new byte[10000];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                socket.receive(receivePacket); //wait to receive packet from server.

                Information info = (Information) Information.byteToObj(receivePacket.getData());
                System.out.println("GUI Thread got API Message: \t" + info.getAPImessage());
                readPackets(receivePacket);


            } catch (IOException e) {
                System.out.println(playerName + "'s GUI thread has thrown an IOException");
                Thread.currentThread().interrupt(); //possibly not the right code
                throw new RuntimeException(e);
            }
        }
        //always wait for packets from the server

        //process the packets in another method
    }

    private void readPackets(DatagramPacket receivePacket) {
        Information info = (Information) Information.byteToObj(receivePacket.getData());

        if(info.getAPImessage().equalsIgnoreCase("new client")){
            System.out.println(playerName + "'s GUI thread received " + info.getAPImessage() + " message");
        }
        else if (info.getAPImessage().equalsIgnoreCase("other client ready state changed")){
            System.out.println(playerName + "'s GUI thread received " + info.getAPImessage() + " message");
        }
        else if(info.getAPImessage().equalsIgnoreCase("other client chat")){
            System.out.println(playerName + "'s GUI thread received " + info.getAPImessage() + " message");
        }
    }
}
