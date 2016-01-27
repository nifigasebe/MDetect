
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TDFillTransition extends Application {

    private FillTransition ft;

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 100,100));
        Rectangle rect = new Rectangle(200,200);
        root.getChildren().add(rect);

        ft = new FillTransition();
        ft.setDuration(Duration.millis(250));
        ft.setShape(rect);
        ft.setFromValue(Color.RED);
        ft.setToValue(Color.WHITE);
        ft.setCycleCount(30);
    }

    public void play() {
        ft.play();
    }

    @Override public void stop() {
        ft.stop();
    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
        play();
    }
    public static void main(String[] args) { launch(args); }
}