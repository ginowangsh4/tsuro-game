package tsuro.admin;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tsuro.*;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

// Localhost IP: "127.0.0.1"
// Richie IP: "10.105.75.35"
// Jin IP: "10.105.16.16"
// Jennifer IP: "10.105.35.58"
public class Admin {
    private static DocumentBuilder db;
    private static AdminSocket socket;
    private static Parser parser;
    private static APlayer player;

    // CML arguments:
    // 0: Port_Number,
    // 1: Player_Name,
    // 2: Player_Type (H/M),
    // 3: Strategy (R/MS/LS) if Player_Type is M"
    public static void main(String[] args) throws Exception {
        db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        parser = new Parser(db);
        socket = new AdminSocket("localhost", Integer.parseInt(args[0]));
        if (args[2].equals("M")) {
            switch (args[3]) {
                case "R":
                    player = new MPlayerRandom(args[1]);
                    break;
                case "LS":
                    player = new MPlayerLeastSym(args[1]);
                    break;
                case "MS":
                    player = new MPlayerMostSym(args[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Entered invalid strategy for MPlayer!");
            }
        } else {
            player = new HPlayer(args[1]);
        }

        while (socket.connectionEstablished()) {
            String res = socket.readInputFromServer();
            // server has closed the connection
            if (res == null) break;
            Document doc = parser.stringToDocument(res);
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name":
                    processGetName();
                    break;
                case "initialize":
                    processInitialize(node);
                    break;
                case "place-pawn":
                    processPlacePawn(node);
                    break;
                case "play-turn":
                    processPlayTurn(node);
                    break;
                case "end-game":
                    processEndGame(node);
                    break;
                default:
                    throw new IllegalArgumentException("Admin: Invalid method call over network");
            }
        }
    }

    private static void processGetName() throws Exception {
        String playerName = player.getName();
        Document getNameResXML = parser.buildPlayerNameXML(playerName);
        sendXMLToClient(getNameResXML, "Admin: get-name complete");
    }

    private static void processInitialize(Node node) throws Exception {
        Node colorNode = node.getFirstChild();
        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = Parser.fromNodeToDoc(colorsNode, db);
        List<Integer> colors = parser.fromColorListSetXML(colorsDoc);

        player.initialize(color, colors);
        Document voidXML = parser.buildVoidXML();
        sendXMLToClient(voidXML, "Admin: initialize complete ");
    }

    private static void processPlacePawn(Node node) throws Exception {
        Board board = parser.boardParser.fromNode(node.getFirstChild());

        Token token = player.placePawn(board);
        Document pawnLocXML = parser.buildPawnLocXML(token.getPosition(), token.getIndex());
        sendXMLToClient(pawnLocXML, "Admin: place-pawn complete");
    }

    private static void processPlayTurn(Node node) throws Exception {
        Node boardNode = node.getFirstChild();
        Board board = parser.boardParser.fromNode(boardNode);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(setNode, db);
        List<Tile> hand = parser.fromTileSetXML(setDoc);

        Node nNode = setNode.getNextSibling();
        int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

        Tile tile = player.playTurn(board, hand, tilesLeft);
        Document tileXML = parser.tileParser.buildXML(tile);
        sendXMLToClient(tileXML, "Admin: play-turn complete");
    }

    private static void processEndGame(Node node) throws Exception {
        Node boardNode = node.getFirstChild();
        Board board = parser.boardParser.fromNode(boardNode);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(setNode, db);
        List<Integer> colors = parser.fromColorListSetXML(setDoc);

        player.endGame(board, colors);
        Document voidXML = parser.buildVoidXML();
        sendXMLToClient(voidXML, "Admin: end-game complete");
    }

    private static void sendXMLToClient(Document doc, String printMessage) throws Exception {
        String s = parser.documentToString(doc);
        System.out.println(printMessage + s);
        socket.writeOutputToServer(s);
    }
}
