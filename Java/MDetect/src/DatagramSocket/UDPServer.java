package DatagramSocket;

import Controllers.MainController;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;

/**
 * Created by Chizhov-AS on 14.10.2015.
 */

public class UDPServer implements Runnable {

    private MainController mainController;

    //Сетевые настройки
    private final int UDP_SERVER_PORT = 9191;   // Порт который слушает сервер
    //Пульс
    private long lastPulseTime = 0;             // Для хранения времени последнего пульса
    private final long pulseInterval = 35000;   // Интервал отправки пульса Arduino01 (1 мин) todo проверить

    //Сообщения от Arduino
    // A1 - Arduino - Node 01
    // AS - Arduino - Server
    // JS - Java - Server
    // Messages:
    // 00 - Arduino Server - Ready
    // 10 - Arduino01 - Ready
    // 11 - Arduino01 - Pulse
    // 12 - Arduino01 - Motion
    // 91 - Java Server Sound ON
    // 92 - Java Server Sound OFF

    private final String MESSAGE_PULSE = "11";                // Сообщение отправляемое Arduino01 - Pulse
    private final String MESSAGE_MOTION = "12";               // Сообщение отправляемое Arduino01 - Motion detected
    private final String MESSAGE_A1_READY = "10";             // Сообщение отправляемое Arduino01 - Status: Ready
    private final String MESSAGE_AS_READY = "00";             // Сообщение отправляемое Arduino Leonardo ETH - Status: Ready

    private boolean a1_isReady = false;
    private boolean as_isReady = false;

    public UDPServer(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void run() {

        try {
            DatagramSocket serverSocket = new DatagramSocket(UDP_SERVER_PORT);

            while (!Thread.interrupted()) {
                // Получаем сообщение
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.setSoTimeout(500); // receive() for this DatagramSocket will block for only this amount of time.

                try {
                    serverSocket.receive(receivePacket); //Todo вроде как на этом моменте все стопоиться пока не придет пакет

                }catch (SocketTimeoutException stEX){
                    //ignore
                }
                String string = new String(receivePacket.getData()).trim();

                // В зависимости от того что пришло:
                switch (string){
                    case MESSAGE_MOTION:
                        System.out.println("Arduino01 - Motion");   //Todo del
                        lastPulseTime = System.currentTimeMillis();
                        //Пока нашелся только такой вариант вызывать методы контроллера из другого потока
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mainController.motionDetected();
                            }
                        });
                        break;
                    case MESSAGE_PULSE:
                        System.out.println("Arduino01 - Pulse");
                        lastPulseTime = System.currentTimeMillis();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mainController.pulseReceived();
                            }
                        });
                        break;
                    case MESSAGE_A1_READY:
                        System.out.println("Arduino01 - Ready");    //Todo del
                        a1_isReady = true;
                        break;
                    case MESSAGE_AS_READY:
                        System.out.println("Arduino Server - Ready");   //Todo del
                        as_isReady = true;
                        break;
                    default:
                        //System.out.println("Unknown message: " + string);
                        break;
                }

                // Проверяем пульс, если пульс давно не приходил: устанавливаем соответствующие цвет и статус в GUI
                if(!checkPulseIsOk()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mainController.pulseNotReceived();
                        }
                    });
                }
                // Если от всех Arduino пришли сообщения Ready то устанавливаем соответствующие цвет и статус в GUI
                if(a1_isReady & as_isReady){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mainController.allArduinoReady();
                        }
                    });
                }
            }
            serverSocket.close();   // Todo дописать finaly
        }catch (IOException ioEX){
            ioEX.printStackTrace();
        }
    }

    private boolean checkPulseIsOk(){
        // Проверяем: если интервал времени между двумя пульсами больше чем pulseInterval, значит пульс не пришел
        if(System.currentTimeMillis() < (lastPulseTime + pulseInterval)){
            //System.out.println("checkPulseIsOk - true"); // TODO: 20.12.2015 del
            return true;
        }else {
            //System.out.println("checkPulseIsOk - false"); // TODO: 20.12.2015 del
            return false;
        }
    }
}