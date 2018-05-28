package tsuro.admin;

import javafx.application.Application;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tsuro.Board;
import tsuro.HPlayer;
import tsuro.Tile;
import tsuro.Token;
import tsuro.parser.BoardParser;
import tsuro.parser.Parser;
import tsuro.parser.TileParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import java.io.*;
import java.util.List;

import static tsuro.admin.Admin.sendXMLToClient;
import static tsuro.parser.Parser.fromNodeToDoc;

public class UIAdmin extends Application {

    public static UISuite uiSuite;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // set up connection to local host
        try {
            uiSuite = new UISuite(stage);

            String hostname = "127.0.0.1";
            int port = 6666;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;

            db = dbf.newDocumentBuilder();

            AdminSocket socket = new AdminSocket(hostname, port, db);
            String s = null;

            // initialize
            HPlayer hPlayer = processInitialize(db, socket);

            // place-pawn
            processPlacePawn(db, socket, hPlayer, stage);

            //TODO: implement these three
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

    public static HPlayer processInitialize(DocumentBuilder db, AdminSocket socket) throws Exception {
        Document initializeXML = Parser.stringToDocument(db, socket.readInputFromClient());
        if (!initializeXML.getFirstChild().getNodeName().equals("initialize")) {
            throw new IllegalArgumentException("Message is not initialize");
        }

        Node initializeNode = initializeXML.getFirstChild();
        Node colorNode = initializeNode.getFirstChild();
        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = fromNodeToDoc(db, colorsNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, colorsDoc);

        // TODO: human shouldn't have strategy
        HPlayer hPlayer = new HPlayer(HPlayer.Strategy.R);
        hPlayer.initialize(color, colors);
        Document voidXML = Parser.buildVoidXML(db);

        sendXMLToClient(socket, voidXML, "UI Admin: initialize complete ");
        return hPlayer;
    }


    public static void processPlacePawn(DocumentBuilder db, AdminSocket socket, HPlayer hPlayer, Stage stage) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        String s = socket.readInputFromClient();
        Document placePawnXML = Parser.stringToDocument(db, s);
        if (!placePawnXML.getFirstChild().getNodeName().equals("place-pawn")) {
            throw new IllegalArgumentException("Message is not place-pawn");
        }

        Node boardNode = placePawnXML.getFirstChild().getFirstChild();
        Document boardDoc = fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        uiSuite.generateBoardImage(boardDoc);
        uiSuite.startGame();

        Token token = hPlayer.placePawn(board);
        Document pawnLocXML = Parser.buildPawnLocXML(db, token.getPosition(), token.getIndex());

        sendXMLToClient(socket, pawnLocXML, "UI Admin: place-pawn complete ");
    }

    public static void processGetName(DocumentBuilder db, AdminSocket socket, HPlayer hPlayer) throws Exception {
        String playerName = hPlayer.getName();
        Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
        sendXMLToClient(socket, getNameResXML, "UI Admin: get-name complete ");
    }

    public static void processPlayTurn(DocumentBuilder db, AdminSocket socket, HPlayer hPlayer, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = fromNodeToDoc(db, setNode);
        List<Tile> hand = Parser.fromTileSetXML(db, setDoc);

        Node nNode = setNode.getNextSibling();
        int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

        Tile tile = hPlayer.playTurn(board, hand, tilesLeft);
        Document tileXML = tileParser.buildXML(tile);

        sendXMLToClient(socket, tileXML, "UI Admin: play-turn complete ");
    }

    public static void processEndGame(DocumentBuilder db, AdminSocket socket, HPlayer hPlayer, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = fromNodeToDoc(db, setNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, setDoc);

        hPlayer.endGame(board, colors);
        Document voidXML = Parser.buildVoidXML(db);

        sendXMLToClient(socket, voidXML, "UI Admin: end-game complete ");
    }
}
