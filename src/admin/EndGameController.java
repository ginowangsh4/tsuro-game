package tsuro.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tsuro.parser.Parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EndGameController {
    @FXML
    private ImageView boardImageView;
    @FXML
    private Label winnerList;

    public void initialize() throws Exception {
        boardImageView.setImage(new Image(new FileInputStream("image/board/board.png")));
        String winners = App.socket.readInputFromServer();
        winnerList.setText(winners);
    }
}
