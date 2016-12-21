package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/*
 * Creates the main window in a separate thread
 */

public class Window extends Application {


    public Window() {}

    void launchWindow() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane page = (BorderPane) FXMLLoader.load(Main.class.getResource("sample.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FXML is Simple");
        primaryStage.show();
    }
}
