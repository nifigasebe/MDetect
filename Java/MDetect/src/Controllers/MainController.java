package Controllers;

import DatagramSocket.UDPClient;
import DatagramSocket.UDPServer;
import Sound.PlaySound;

import javafx.animation.FillTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainController {

    @FXML Pane paneRectResize;
    @FXML Rectangle rectangleAlert;
    @FXML Label labelStatus;
    @FXML MenuItem menuExit;
    @FXML CheckMenuItem menuSoundPC;
    @FXML CheckMenuItem menuSoundArduino;

    private FillTransition fillTransition;

    private DatagramSocket.UDPClient udpClient;
    private DatagramSocket.UDPServer udpServer;

    private final String STATUS_CONNECTED = "Status: Connected.";
    private final String STATUS_DISCONNECTED = "Status: Disconnected.";
    private final String STATUS_MOTION_DETECTED = "Status: Alert! Motion detected.";

    PlaySound playSound;
    private boolean soundIsOn = true;

    ExecutorService executorService;

    @FXML
    private void initialize() {
        // Привязываем размер rectangleAlert к размеру paneRectResize что бы работала автоматическая подстройка под размер окна
        rectangleAlert.widthProperty().bind(paneRectResize.widthProperty());
        rectangleAlert.heightProperty().bind(paneRectResize.heightProperty());
        // Инициализация анимации
        initializeFillTransition();
        // Инициализация слушателей
        initializeListeners();
        // Стартуем сервер в новом потоке
        startUDPServer();
    }

    @FXML
    private void setMenuExit() {
        System.exit(0);
    }

    private void startUDPServer(){
        // Стартуем UDP сервер в новом потоке
        udpServer = new UDPServer(this);
        executorService = Executors.newCachedThreadPool();
        executorService.execute(udpServer);
    }

    // Не используется, но возможно потом будет нужно
    public void closeAllThreads(){
        executorService.shutdownNow();
    }

    private void initializeFillTransition(){
        // Создаем и настраиваем анимацию FillTransition
        fillTransition = new FillTransition();
        fillTransition.setDuration(Duration.millis(500));   //Продлжительность анимации
        fillTransition.setShape(rectangleAlert);
        fillTransition.setFromValue(Color.RED);
        fillTransition.setToValue(Color.WHITE);
        fillTransition.setCycleCount(30);                   //Колличество повторов анимации
        // После окончания анимации
        fillTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rectangleAlert.setFill(Color.GREEN);
                labelStatus.setText(STATUS_CONNECTED);
            }
        });
    }

    private void initializeListeners(){
        // Включение/выключение звука на ArduinoServer Отправляет в новом потоке UDP пакет.
        menuSoundArduino.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                udpClient = new UDPClient(!menuSoundArduino.isSelected());
                ExecutorService executorService = Executors.newCachedThreadPool();
                executorService.execute(udpClient);
                executorService.shutdown();
            }
        });
        //Вклбчение отключения звука в приложении.
        menuSoundPC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                soundIsOn = menuSoundPC.isSelected();
                System.out.println(soundIsOn);
            }
        });
    }

    public void motionDetected() {
        // Проигрываем анимацию,звук,меняем статус
        if(soundIsOn){
            playSound = new PlaySound();
            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(playSound);
            executorService.shutdown();
        }
        fillTransition.play();
        labelStatus.setText(STATUS_MOTION_DETECTED);
    }

    public void pulseReceived(){
        // Меняем цвет и статус
        rectangleAlert.setFill(Color.GREEN);
        labelStatus.setText(STATUS_CONNECTED);
    }

    public void pulseNotReceived(){
        // Меняем цвет и статус
        rectangleAlert.setFill(Color.WHITE);
        labelStatus.setText(STATUS_DISCONNECTED);
    }

    public void allArduinoReady(){
        // Меняем цвет и статус
        rectangleAlert.setFill(Color.GREEN);
        labelStatus.setText(STATUS_CONNECTED);
    }
}