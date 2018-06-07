package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StartGameController {
    public static enum Side { TOP, LEFT, RIGHT, BOTTOM }

    @FXML
    private ImageView boardImageView;

    @FXML
    private ChoiceBox<Side> sideDropdown;

    @FXML
    private ChoiceBox<Integer> indexDropdown;

    @FXML
    private Button submitButton;

    public static Side startSide;
    public static Integer startIndex;
    private int BOARD_SIZE = 360;


    public void initialize() throws FileNotFoundException {

        Image boardImage = new Image(new FileInputStream("image/board/board.png"), BOARD_SIZE, BOARD_SIZE, false, true);
        boardImageView.setImage(boardImage);

        boardImageView.setOnMouseClicked(event -> {
            System.out.println("image clicked");
        });

        sideDropdown.getItems().addAll(Side.TOP, Side.BOTTOM, Side.LEFT, Side.RIGHT);

        for(int i = 0; i < 12; i++){
            indexDropdown.getItems().add(i);
        }
        sideDropdown.setOnMouseClicked(event -> {
            System.out.println("startSide dropdown clicked");
        });

        indexDropdown.setOnMouseClicked(event -> {
            System.out.println("startIndex dropdown clicked");
        });

        submitButton.setOnMouseClicked(event -> {
            System.out.println("submit clicked");
            startSide = sideDropdown.getValue();
            startIndex = indexDropdown.getValue();
            if(startSide != null && startIndex != null){
                System.out.println(startSide);
                System.out.println(startIndex);
            }
            else{
                System.out.println("should not submit now");
            }
        });
    }
}
