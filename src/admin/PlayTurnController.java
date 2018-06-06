package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PlayTurnController {
    private Parser parser;
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

    private Map tileMap;

    private Button currTileButton;

    @FXML
    public void initialize() {
        boardImage.setOnMouseClicked(event -> {

        });

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

            Image image = null;
            try {
                image = new Image(new FileInputStream("image/uiboard.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            boardImage.setImage(image);
            System.out.println("Board image updated!");
        });

        commitMoveButton.setOnAction(event -> {

        });
    }
}
