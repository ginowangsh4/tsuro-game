package tsuro.admin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
    private static HBox leftHbox;
    private static VBox rightVbox;
    private static HBox rightHbox;
    private static HBox bottomHbox;
    private static AnchorPane middlePane;

    private static Image boardImage;
    private static Image pawnImage;
    private static ImageInput pawnImageInput;

    private static final int PAWN_SIZE = 33;
    private static final int NUM_POS = 12;
    public static enum Side { TOP, LEFT, RIGHT, BOTTOM }

    public static Side startSide;
    public static int startIndex;

    public UISuite(Stage s) throws FileNotFoundException {
        stage = s;
        border = new BorderPane();

        topHbox = new HBox();
        bottomHbox = new HBox();
        leftVbox = new VBox();
        leftHbox = new HBox();
        rightVbox = new VBox();
        rightHbox = new HBox();
        middlePane = new AnchorPane();

        setPaneSize(topHbox, 800, 50);
        setPaneSize(bottomHbox, 800, 50);
        setPaneSize(rightVbox, 200, 400);
        setPaneSize(leftVbox, 200, 400);
        setPaneSize(middlePane, 400, 400);

        pawnImage = new Image(new FileInputStream("image/uipawn.png"), PAWN_SIZE, PAWN_SIZE, false, true);
        pawnImageInput = new ImageInput();
        pawnImageInput.setSource(pawnImage);
    }

    public static void setPaneSize(Pane p, float width, float height) {
        p.setMinSize(width, height);
        p.setMaxSize(width, height);
    }

    public static void initializeSideAndIndex(Side s, int index) {
        startSide = s;
        startIndex = index;
        System.out.println("A pawn location is clicked!");
    }

    public static AnchorPane createPawnPane(Side s, int index) {
        AnchorPane ap = new AnchorPane();
        setPaneSize(ap, PAWN_SIZE, PAWN_SIZE);

        ap.addEventHandler(MouseEvent.MOUSE_ENTERED,
                event -> ap.setEffect(pawnImageInput));
        ap.addEventHandler(MouseEvent.MOUSE_EXITED,
                event -> ap.setEffect(null));
        ap.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> initializeSideAndIndex(s, index));

        return ap;
    }

    public static void startGame() throws FileNotFoundException {
        GridPane topGrid = new GridPane();
        GridPane bottomGrid = new GridPane();
        GridPane leftGrid = new GridPane();
        GridPane rightGrid = new GridPane();

        for (int i = 0; i < NUM_POS; i++) {
            topGrid.add(createPawnPane(Side.TOP, i), i, 0);
            bottomGrid.add(createPawnPane(Side.BOTTOM, i), i, 0);
            leftGrid.add(createPawnPane(Side.LEFT, i), 0, i);
            rightGrid.add(createPawnPane(Side.RIGHT, i), 0, i);
        }
        topHbox.getChildren().add(topGrid);
        topHbox.setAlignment(Pos.CENTER);
        setPaneSize(topHbox, 800, PAWN_SIZE);

        bottomHbox.getChildren().add(bottomGrid);
        bottomHbox.setAlignment(Pos.CENTER);
        setPaneSize(bottomHbox, 800, PAWN_SIZE);

        VBox leftText = new VBox();
        Text text = new Text("it's your turn to place pawn");

        text.setWrappingWidth(200 - PAWN_SIZE);
        leftText.getChildren().add(text);
        setPaneSize(leftText, 200 - PAWN_SIZE, 400);
        leftText.setStyle("-fx-border-color: black;");
        leftHbox = new HBox(leftText, leftGrid);

        rightVbox.getChildren().add(rightGrid);

        boardImage = new Image(new FileInputStream("image/uiboard.png"), 400, 400, false, true);
        ImageView imageView = new ImageView(boardImage);
        middlePane.getChildren().add(imageView);

        middlePane.setStyle("-fx-border-color: black;");
        topHbox.setStyle("-fx-border-color: black;");
        bottomHbox.setStyle("-fx-border-color: black;");
        leftHbox.setStyle("-fx-border-color: black;");
        rightVbox.setStyle("-fx-border-color: black;");

        border = new BorderPane(middlePane, topHbox, rightVbox, bottomHbox, leftHbox);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }

    public static void generateBoardImage(Document doc) throws Exception {
        String command = "./visualize -b -i image/uiboard.png";
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
            String command = "./visualize -t -i image/uitile" + i + ".png";
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
