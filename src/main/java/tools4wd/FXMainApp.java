package tools4wd;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMainApp extends Application {
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene( new Mainframe(), 800, 600);

        stage.setTitle("4WD Controller");
        stage.setScene(scene);
        stage.show();
    }
}