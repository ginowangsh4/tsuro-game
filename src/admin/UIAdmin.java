package tsuro.admin;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.ImageInput;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.Board;
import tsuro.HPlayer;
import tsuro.Tile;
import tsuro.Token;
import tsuro.parser.BoardParser;
import tsuro.parser.Parser;
import tsuro.parser.TileParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import java.io.*;
import java.util.List;

public class UIAdmin extends Application {

    public static UISuite uiSuite;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        uiSuite = new UISuite(stage);
        // set up connection to local host
        try {
            String hostname = "127.0.0.1";
            int port = Integer.parseInt("6666");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;

            db = dbf.newDocumentBuilder();

            AdminSocket socket = new AdminSocket(hostname, port, db);
            String s = null;

            // initialize
            HPlayer hPlayer = processInitialize(db, socket, s);

            // place-pawn
            processPlacePawn(db, socket, s, hPlayer, stage);
//
//            while (socket.connectionEstablished()) {
//                String res = socket.readInputFromClient();
//                // server has closed the connection
//                if (res == null) break;
//                Document doc = Parser.stringToDocument(db, res);
//                if (!doc.getFirstChild().getNodeName().equals("play-turn")
//                        && !doc.getFirstChild().getNodeName().equals("end-game")
//                        && !doc.getFirstChild().getNodeName().equals("get-name")) {
//                    throw new IllegalArgumentException("Message is not play-turn, end-game or get-name");
//                }
//                Node node = doc.getFirstChild();
//                switch (node.getNodeName()) {
//                    case "get-name":
//                        processGetName(db, socket, s, hPlayer);
//                        break;
//                    case "play-turn":
//                        processPlayTurn(db, socket, s, hPlayer, node);
//                        break;
//                    case "end-game":
//                        processEndGame(db, socket, s, hPlayer, node);
//                        break;
//                    default:
//                        break;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HPlayer processInitialize(DocumentBuilder db, AdminSocket socket, String s) throws Exception {
        Document initializeXML = Parser.stringToDocument(db, socket.readInputFromClient());
        if (!initializeXML.getFirstChild().getNodeName().equals("initialize")) {
            throw new IllegalArgumentException("Message is not initialize");
        }

        Node initializeNode = initializeXML.getFirstChild();
        Node colorNode = initializeNode.getFirstChild();
        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = db.newDocument();
        Node imported = colorsDoc.importNode(colorsNode, true);
        colorsDoc.appendChild(imported);
        List<Integer> colors = Parser.fromColorListSetXML(db, colorsDoc);

        HPlayer hPlayer = new HPlayer(HPlayer.Strategy.R);  // how to assign strategy?
        hPlayer.initialize(color, colors);
        Document voidXML = Parser.buildVoidXML(db);
        s = Parser.documentToString(voidXML);
        System.out.println("UI Admin: initialize complete " + s);
        socket.writeOutputToClient(s);

        return hPlayer;
    }


    public static void processPlacePawn(DocumentBuilder db, AdminSocket socket, String s, HPlayer hPlayer, Stage stage) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        s = socket.readInputFromClient();
        Document placePawnXML = Parser.stringToDocument(db, s);
        if (!placePawnXML.getFirstChild().getNodeName().equals("place-pawn")) {
            throw new IllegalArgumentException("Message is not place-pawn");
        }

        Node boardNode = placePawnXML.getFirstChild().getFirstChild();
        Document boardDoc = db.newDocument();
        Node imported = boardDoc.importNode(boardNode, true);
        boardDoc.appendChild(imported);
        Board board = boardParser.fromXML(boardDoc);

        uiSuite.generateBoardImage(boardDoc);
        uiSuite.startGame();
//
//        Token token = hPlayer.placePawn(board);
//        Document pawnLocXML = Parser.buildPawnLocXML(db, token.getPosition(), token.getIndex());
//        s = Parser.documentToString(pawnLocXML);
//        System.out.println("Admin: place-pawn complete " + s);
//        socket.writeOutputToClient(s);
    }

    public static void processGetName(DocumentBuilder db, AdminSocket socket, String s, HPlayer hPlayer) throws Exception {
        String playerName = hPlayer.getName();
        Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
        s = Parser.documentToString(getNameResXML);
        System.out.println("UI Admin: get-name complete " + s);
        socket.writeOutputToClient(s);
    }

    public static void processPlayTurn(DocumentBuilder db, AdminSocket socket, String s, HPlayer hPlayer, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = db.newDocument();
        Node imported = boardDoc.importNode(boardNode, true);
        boardDoc.appendChild(imported);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = db.newDocument();
        imported = setDoc.importNode(setNode, true);
        setDoc.appendChild(imported);
        List<Tile> hand = Parser.fromTileSetXML(db, setDoc);

        Node nNode = setNode.getNextSibling();
        int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

        Tile tile = hPlayer.playTurn(board, hand, tilesLeft);
        Document tileXML = tileParser.buildXML(tile);
        s = Parser.documentToString(tileXML);
        System.out.println("UI Admin: play-turn complete " + s);
        socket.writeOutputToClient(s);
    }

    public static void processEndGame(DocumentBuilder db, AdminSocket socket, String s, HPlayer hPlayer, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = db.newDocument();
        Node imported = boardDoc.importNode(boardNode, true);
        boardDoc.appendChild(imported);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = db.newDocument();
        imported = setDoc.importNode(setNode, true);
        setDoc.appendChild(imported);
        List<Integer> colors = Parser.fromColorListSetXML(db, setDoc);

        hPlayer.endGame(board, colors);
        Document voidXML = Parser.buildVoidXML(db);
        s = Parser.documentToString(voidXML);
        System.out.println("UI Admin: end-game complete " + s);
        socket.writeOutputToClient(s);
    }
}
