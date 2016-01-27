package UDPClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainClient.fxml"));
        primaryStage.setTitle("UdpClient");
        primaryStage.setScene(new Scene(root, 200, 150));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
