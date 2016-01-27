package Start;

import Controllers.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;


public class Main extends Application {

    MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        URL location = getClass().getResource("/Fxml/Main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        // Заголовок и минимальный размер окна
        primaryStage.setTitle("Motion Detection");
        primaryStage.setMinWidth(250);
        primaryStage.setMinHeight(200);
        Scene scene = new Scene(root,350,200);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                //controller.closeAllThreads(); //todo переписать
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}