package tsuro.admin;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.io.*;

public class UISuite {
    private static Stage stage;
    private static BorderPane border;
    private static HBox topHbox;
    private static VBox leftVbox;
    private static VBox rightVbox;
    private static HBox bottomHbox;
    private static Image boardImage;
    private static AnchorPane middlePane;

    public UISuite(Stage s) {
        stage = s;
        topHbox = new HBox();
        bottomHbox = new HBox();
        leftVbox = new VBox();
        rightVbox = new VBox();
        middlePane = new AnchorPane();
        border = new BorderPane(middlePane, topHbox, rightVbox, bottomHbox, leftVbox);
        middlePane.setPrefSize(400, 400);
        topHbox.setPrefSize(800, 50);
        bottomHbox.setPrefSize(800, 50);
        rightVbox.setPrefSize(200, 400);
        leftVbox.setPrefSize(200, 400);
    }



    public static void startGame() throws FileNotFoundException {
        Text text = new Text();
        text.setText("it's your turn to place pawn wooo");

        leftVbox.getChildren().add(text);
        leftVbox.setMargin(text, new Insets(20,20,20,20));

        Button button = new Button();
        rightVbox.getChildren().add(button);

        button.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> button.setEffect(new DropShadow()));

        boardImage = new Image(new FileInputStream("board.png"), 400, 400, false, true);
        ImageView imageView = new ImageView(boardImage);

//        ImageInput imageInput = new ImageInput();
//        imageInput.setSource(boardImage);

//        anchorPane.addEventHandler(MouseEvent.MOUSE_ENTERED,
//                new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent e) {
//                        anchorPane.setEffect(imageInput);
//
//                    }
//                });
//        anchorPane.addEventHandler(MouseEvent.MOUSE_EXITED,
//                new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent e) {
//                        anchorPane.setEffect(null);
//                    }
//                });

        middlePane.getChildren().add(imageView);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }

    public static void generateBoardImage(Document doc) throws Exception {
        String command = "./visualize -b -i board.png";
        String line;
        Process p = Runtime.getRuntime().exec(command);
        PrintWriter out = new PrintWriter(p.getOutputStream(), true);
        out.println(Parser.documentToString(doc));
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    public static int generateTileImage(DocumentBuilder db, Document doc) throws Exception {
        NodeList list = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            String command = "./visualize -t -i tile" + i + ".png";
            String line;
            Process p = Runtime.getRuntime().exec(command);
            PrintWriter out = new PrintWriter(p.getOutputStream(), true);
            Document tileDoc = db.newDocument();
            tileDoc.appendChild(doc.importNode(list.item(i), true));
            out.println(Parser.documentToString(tileDoc));
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
        }
        return list.getLength();
    }
}
