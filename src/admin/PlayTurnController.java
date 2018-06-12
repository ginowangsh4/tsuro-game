package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tsuro.Pair;

import java.io.*;

public class PlayTurnController {
    @FXML
    private ImageView boardImage;
    @FXML
    private ImageView tile0Image;
    @FXML
    private ImageView tile1Image;
    @FXML
    private ImageView tile2Image;
    @FXML
    private Button chooseTile0Button;
    @FXML
    private Button chooseTile1Button;
    @FXML
    private Button chooseTile2Button;
    @FXML
    private Button rotateTileButton;
    @FXML
    private Button commitMoveButton;

    // first of pair: selected button
    // second of pair: [index of tile in hand, index of rotation of tile]
    private Pair<Button, int[]> currTileButton;

    @FXML
    public void initialize() throws Exception {
        setBoardAndTileImages();

        chooseTile0Button.setOnAction(event -> {
            tileButtonEvents(chooseTile0Button, 0);
        });

        chooseTile1Button.setOnAction(event -> {
            tileButtonEvents(chooseTile1Button, 1);
        });

        chooseTile2Button.setOnAction(event -> {
            tileButtonEvents(chooseTile2Button, 2);
        });

        rotateTileButton.setOnAction(event -> {
            handleRotateTileButton();
        });

        commitMoveButton.setOnAction(event -> {
            handleCommitMoveButton();
        });
    }

    private void updateBoardImage() {
        try {
            boardImage.setImage(new Image(new FileInputStream("image/board/" + currTileButton.second[0] + "/" +
                    currTileButton.second[1] + ".png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void tileButtonEvents(Button button, int index) {
        currTileButton = new Pair<>(button, new int[]{index, 0});
        rotateTileButton.setDisable(false);
        commitMoveButton.setDisable(false);
        updateBoardImage();
    }

    private void handleRotateTileButton() {
        System.out.println("Before rotation [" + currTileButton.second[0] + ", " + currTileButton.second[1] + "]");
        currTileButton.second[1] = (currTileButton.second[1] + 1) % 4;
        System.out.println("After rotation: [" + currTileButton.second[0] + ", " + currTileButton.second[1] + "]");
        updateBoardImage();
    }

    private void handleCommitMoveButton() {
        App.socket.writeOutputToServer(String.valueOf(currTileButton.second[0]) + "," + String.valueOf(currTileButton.second[1]));
        App.generateAlert(Alert.AlertType.INFORMATION, "Please click \"OK\" and wait for others finish to turn.");
        try {
            // let UI block until next turn
            String response = App.socket.readInputFromServer();
            System.out.println(response);
            if (response == null || (!response.equals("<play-turn>") && !response.equals("<end-game>"))) {
                throw new IllegalArgumentException("Response for play turn view is invalid");
            } else if (response.equals("<end-game>")) {
                // switch to end game scene
                App.changeScene(commitMoveButton, "EndGame.fxml");
            }
            setBoardAndTileImages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBoardAndTileImages() throws Exception {
        boardImage.setImage(new Image(new FileInputStream("image/board/board.png")));
        int handSize = Integer.parseInt(App.socket.readInputFromServer());

        tile0Image.setImage(new Image(new FileInputStream("image/hand/0.png")));
        chooseTile0Button.setDisable(false);

        tile1Image.setImage(null);
        tile2Image.setImage(null);
        chooseTile1Button.setDisable(true);
        chooseTile2Button.setDisable(true);
        if (handSize == 3) {
            tile1Image.setImage(new Image(new FileInputStream("image/hand/1.png")));
            tile2Image.setImage(new Image(new FileInputStream("image/hand/2.png")));
            chooseTile1Button.setDisable(false);
            chooseTile2Button.setDisable(false);
        } else if (handSize == 2) {
            tile1Image.setImage(new Image(new FileInputStream("image/hand/1.png")));
            chooseTile1Button.setDisable(false);
        }
        
        // disable both rotate tile and commit move button at the start of a turn
        rotateTileButton.setDisable(true);
        commitMoveButton.setDisable(true);
    }
}
