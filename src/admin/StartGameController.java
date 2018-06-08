package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartGameController {

    @FXML
    private Button startGameButton;


    @FXML
    public void initialize() throws Exception{
        startGameButton.setOnMouseClicked(event -> {
            System.out.println("startGameButton clicked");
            App.changeScene(startGameButton, "PlacePawn.fxml");
        });
    }


}
