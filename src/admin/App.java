package tsuro.admin;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    public static AdminSocket socket;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        socket = new AdminSocket("127.0.0.1", 9000);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("PlacePawn.fxml"));
        BorderPane startGameView = loader.load();
        Scene scene = new Scene(startGameView);
        stage.setScene(scene);
        stage.setTitle("Tsuro Game");
        stage.show();
    }

    public static void changeScene(Node node, String source) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource(source));
        BorderPane newView = null;
        try {
            newView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(new Scene(newView));
    }
}



