package tsuro.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

// WORK IN PROGRESS
public class PlayTurnController {
    @FXML
    private ImageView boardImage;
    @FXML
    private ImageView tile1Image;
    @FXML
    private ImageView tile2Image;
    @FXML
    private ImageView tile3Image;
    @FXML
    private Button chooseTile1Button;
    @FXML
    private Button chooseTile2Button;
    @FXML
    private Button chooseTile3Button;
    @FXML
    private Button rotateTileButton;
    @FXML
    private Button commitMoveButton;

    private Button currTileButton;

    @FXML
    public void initialize() throws Exception {
        Image image = new Image(new FileInputStream("image/board/board.png"));
        boardImage.setImage(image);

        tile1Image.setImage(new Image(new FileInputStream("image/hand/tile.png")));
        tile2Image.setImage(new Image(new FileInputStream("image/hand/tile.png")));
        tile3Image.setImage(new Image(new FileInputStream("image/hand/tile.png")));

        rotateTileButton.setDisable(true);
        commitMoveButton.setDisable(true);

        chooseTile1Button.setOnAction(event -> {
            currTileButton = chooseTile1Button;
        });

        chooseTile2Button.setOnAction(event -> {
            currTileButton = chooseTile2Button;
        });

        chooseTile3Button.setOnAction(event -> {
            currTileButton = chooseTile3Button;
        });

        rotateTileButton.setOnAction(event -> {
            System.out.println("Rotating tile [" + currTileButton.getText() + "]");
        });

        commitMoveButton.setOnAction(event -> {

        });

        commitMoveButton.setOnMouseClicked(event -> {
            App.socket.writeOutputToServer("Testing sending back stuff from play turn controller");
        });
    }
}
