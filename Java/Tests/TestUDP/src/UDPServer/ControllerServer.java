package UDPServer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerServer {

    @FXML TextArea mainTextArea;
    @FXML Button buttonClean;

    ExecutorService executorService;
    UDPServer udpServer;

    @FXML
    private void initialize() {
        initializeListeners();
        startUDPServer();
    }

    private void startUDPServer(){
        // Стартуем UDP сервер в новом потоке
        udpServer = new UDPServer(this);
        executorService = Executors.newCachedThreadPool();
        executorService.execute(udpServer);
        //executorService.shutdown();
    }

    private void initializeListeners(){
        buttonClean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainTextArea.clear();
            }
        });
    }

//    void exit(){
//        Window window = mainTextArea.getScene().getWindow();
//        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                System.out.println("EXIT");
//                executorService.shutdownNow();
//            }
//        });
//    }

}
