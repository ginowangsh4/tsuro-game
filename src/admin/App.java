package tsuro.admin;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("StartGame.fxml"));
        BorderPane startGameView = loader.load();
        Scene scene = new Scene(startGameView);
        stage.setScene(scene);
        stage.setTitle("Tsuro Game");
        stage.show();
    }
}



