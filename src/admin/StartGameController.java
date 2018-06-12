package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StartGameController {
    @FXML
    private Button startGameButton;
    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() throws FileNotFoundException {
        logoImage.setImage(new Image(new FileInputStream("image/dragon.png")));

        startGameButton.setOnMouseClicked(event -> {
            handleStartGameButton();
        });
    }

    private void handleStartGameButton() {
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
    }
}
