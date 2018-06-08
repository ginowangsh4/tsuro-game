package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class StartGameController {
    @FXML
    private Button startGameButton;

    @FXML
    public void initialize() {
        startGameButton.setOnMouseClicked(event -> {
            try {
                String response = App.socket.readInputFromServer();
                System.out.println(response);
                if (response == null || !response.equals("<place-pawn>")) {
                    throw new IllegalArgumentException("Response for start game view is invalid");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            App.changeScene(startGameButton, "PlacePawn.fxml");
        });
    }
}
