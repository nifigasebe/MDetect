package Test;

/**
 * Created by Chizhov-AS on 19.10.2015.
 */
import GUI.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TDMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        URL location = getClass().getResource("../GUI/Main.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);

        Parent root = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        primaryStage.setTitle("Motion Detection");
        Scene scene = new Scene(root,350,200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
        TDServer server = new TDServer();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(server);
        executorService.shutdown();
    }
}