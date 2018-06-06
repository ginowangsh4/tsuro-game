package tsuro.admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class StartGameController {
    public static enum Side { TOP, LEFT, RIGHT, BOTTOM }

    @FXML
    private ImageView boardImageView;

    @FXML
    private ChoiceBox<String> sideDropdown;

    @FXML
    private ChoiceBox<Integer> indexDropdown;

    @FXML
    private Button submitButton;

    private String side;
    private Integer index;
    private int BOARD_SIZE = 360;


    public void initialize() throws FileNotFoundException {

        Image boardImage = new Image(new FileInputStream("board/board.png"), BOARD_SIZE, BOARD_SIZE, false, true);
        boardImageView.setImage(boardImage);

        boardImageView.setOnMouseClicked(event -> {
            System.out.println("image clicked");
        });

        sideDropdown.getItems().addAll("top", "down", "left", "right");

        for(int i = 0; i < 12; i++){
            indexDropdown.getItems().add(i);
        }
        sideDropdown.setOnMouseClicked(event -> {
            System.out.println("side dropdown clicked");
        });

        indexDropdown.setOnMouseClicked(event -> {
            System.out.println("index dropdown clicked");
        });

        submitButton.setOnMouseClicked(event -> {
            System.out.println("submit clicked");
            side = sideDropdown.getValue();
            index = indexDropdown.getValue();
            if(side != null && index != null){
                System.out.println(side);
                System.out.println(index);
            }
            else{
                System.out.println("should not submit now");
            }
        });
    }
}
