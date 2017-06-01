package Windows;

import Main.Main;
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
        BorderPane page = FXMLLoader.load(Main.class.getResource("/Windows/FXML/main_screen.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Super Project - Desktop");
        primaryStage.show();
    }

    @Override
    public void stop() {
        Main.close();
    }
}
