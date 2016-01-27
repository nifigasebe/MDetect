package UDPServer;

import javafx.application.Platform;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalTime;

/**
 * Created by alexeychizhov on 07.11.2015.
 */
public class UDPServer implements Runnable {

    private final int UDP_SERVER_PORT = 9191;

    ControllerServer controller;

    UDPServer(ControllerServer controllerServer){
        this.controller = controllerServer;
    }

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(UDP_SERVER_PORT);

            while (!Thread.interrupted()){
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                final String string = new String(receivePacket.getData());
                System.out.println("RECEIVED: " + string);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.mainTextArea.appendText(LocalTime.now() + ": " + string  + "\n");
                    }
                });
            }
        }catch (SocketException sEX) {
            sEX.printStackTrace();
        }catch (IOException ioEX){
            ioEX.printStackTrace();
        }
    }
}