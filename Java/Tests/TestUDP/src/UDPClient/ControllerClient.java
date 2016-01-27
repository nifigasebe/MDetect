package UDPClient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ControllerClient {

    @FXML Button buttonSend;

    private final int PORT = 9191;
    private final String IP_ADDRESS_STRING = "10.0.0.145";   // 10.0.0.145 // 10.10.65.191
    private final String SEND_BUZZER_ON = "91";
    private final String SEND_BUZZER_OFF = "92";
    private boolean soundStatusIsON = true;

    @FXML
    private void initialize() {
        initializeListeners();
    }

    private void initializeListeners(){

        buttonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress severIpAddress = InetAddress.getByName(IP_ADDRESS_STRING);
                    DatagramPacket sendPacket;
                    if(soundStatusIsON){
                        sendPacket = new DatagramPacket(SEND_BUZZER_OFF.getBytes(), SEND_BUZZER_OFF.getBytes().length, severIpAddress, PORT);
                        soundStatusIsON = false;
                    }else {
                        sendPacket = new DatagramPacket(SEND_BUZZER_ON.getBytes(), SEND_BUZZER_ON.getBytes().length, severIpAddress, PORT);
                        soundStatusIsON = true;
                    }
                    socket.send(sendPacket);
                    socket.close();
                }catch (SocketException sEX){
                    sEX.printStackTrace();
                }catch (IOException ioEX){
                    ioEX.printStackTrace();
                }
            }
        });
    }
}

   // char soundOff[] = "92";  // Сервер посылает Откоючить Buzzer
   // char soundOn[] = "91";   // Сервер посылает Вкоючить Buzzer
   // byte ip[] = { 10, 0, 0, 145 };  // 10, 10, 65, 191 // 10, 0, 0, 145