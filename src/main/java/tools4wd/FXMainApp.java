package tools4wd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMainApp extends Application {
    public void start(Stage stage) {
        Scene scene = new Scene( new Mainframe(), 400, 300);

        stage.setTitle("4WD Controller");
        stage.setScene(scene);
        stage.show();
    }
}