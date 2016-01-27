package DatagramSocket;

import java.io.IOException;
import java.net.*;

/**
 * Created by alexeychizhov on 01.11.2015.
 */
public class UDPClient implements Runnable {

    //Сетевые настройки
    private final int ARDUINO_ETH_PORT = 9191;                           // Порт который слушает Arduino Leonardo ETH
    private final String ARDUINO_ETH_IP_ADDRESS_STRING = "10.0.0.145";   // IP адрес Arduino Leonardo ETH (10.0.0.145 10.10.65.191)

    //Звук
    private final String SEND_BUZZER_ON = "91";                          // Сообщение отправляемое на Arduino Leonardo ETH что бы включить звук
    private final String SEND_BUZZER_OFF = "92";                         // Сообщение отправляемое на Arduino Leonardo ETH что бы выключить звук
    private boolean soundIsOn;

    public UDPClient(boolean soundIsON){
        this.soundIsOn = soundIsON;                                      // Значение CheckMenuItem menuSoundArduino
    }

    @Override
    public void run() {

        String sendData;

        try {

            DatagramSocket socket = new DatagramSocket();
            InetAddress arduinoEthIpAddress = InetAddress.getByName(ARDUINO_ETH_IP_ADDRESS_STRING);

            // В зависимости от soundIsOn отправляем сообщение включения или отключения звука на Arduino ETH
            if(soundIsOn){
                sendData = SEND_BUZZER_OFF;
            }else{
                sendData = SEND_BUZZER_ON;
            }

            DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.getBytes().length, arduinoEthIpAddress, ARDUINO_ETH_PORT);
            socket.send(sendPacket);
            socket.close();

        }catch (UnknownHostException uhEX) {
            System.out.println("UnknownHostException: " + uhEX);
        }catch (SocketException sEX){
            sEX.printStackTrace();
        }catch (IOException ioEX){
            ioEX.printStackTrace();
        }
    }
}