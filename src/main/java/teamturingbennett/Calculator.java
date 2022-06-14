/*
    Calculator.java
    Main Application class
 */
package teamturingbennett;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Calculator extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL url = new File("src/main/resources/teamturingbennett/main.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(900);
        stage.setResizable(true);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
