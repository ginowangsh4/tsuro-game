package tsuro.admin;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tsuro.Board;
import tsuro.MPlayer;
import tsuro.Tile;
import tsuro.Token;
import tsuro.parser.BoardParser;
import tsuro.parser.Parser;
import tsuro.parser.TileParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

public class Admin {
    // public static final int PORT_NUM = 12345;
    private static Parser parser;
    private static MPlayer mPlayer;

    // Commend line arguments as "PORT_NUMBER PLAYER_NAME STRATEGY(R/MS/LS)"
    public static void main(String[] args) throws Exception {
        // set up connection to local host 
        String hostname = "127.0.0.1";
        int port = Integer.parseInt(args[0]);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        parser = new Parser(db);
        AdminSocket socket = new AdminSocket(hostname, port, db);
        switch (args[2]) {
            case "R":
                mPlayer = new MPlayer(MPlayer.Strategy.R, args[1]);
                break;
            case "LS":
                mPlayer = new MPlayer(MPlayer.Strategy.LS, args[1]);
                break;
            case "MS":
                mPlayer = new MPlayer(MPlayer.Strategy.MS, args[1]);
                break;
            default:
                throw new IllegalArgumentException("Admin: Invalid input strategy");
        }
        while (socket.connectionEstablished()) {
            String res = socket.readInputFromClient();
            // server has closed the connection
            if (res == null) break;
            Document doc = parser.stringToDocument(res);
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name":
                    processGetName(db, socket);
                    break;
                case "initialize":
                    processInitialize(db, socket, node);
                    break;
                case "place-pawn":
                    processPlacePawn(db, socket, node);
                    break;
                case "play-turn":
                    processPlayTurn(db, socket, node);
                    break;
                case "end-game":
                    processEndGame(db, socket, node);
                    break;
                default:
                    throw new IllegalArgumentException("Admin: Invalid method call over network");
            }
        }
    }

    public static void processInitialize(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        Node colorNode = node.getFirstChild();

        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = Parser.fromNodeToDoc(colorsNode, db);
        List<Integer> colors = parser.fromColorListSetXML(colorsDoc);

        mPlayer.initialize(color, colors);
        Document voidXML = parser.buildVoidXML();
        sendXMLToClient(socket, voidXML, "Admin: initialize complete ");
    }

    public static void processPlacePawn(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(boardNode, db);
        Board board = parser.boardParser.fromXML(boardDoc);

        Token token = mPlayer.placePawn(board);
        Document pawnLocXML = parser.buildPawnLocXML(token.getPosition(), token.getIndex());
        sendXMLToClient(socket, pawnLocXML, "Admin: place-pawn complete");
    }

    public static void processGetName(DocumentBuilder db, AdminSocket socket) throws Exception {
        String playerName = mPlayer.getName();
        Document getNameResXML = parser.buildPlayerNameXML(playerName);
        sendXMLToClient(socket, getNameResXML, "Admin: get-name complete");
    }

    public static void processPlayTurn(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(boardNode, db);
        Board board = parser.boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(setNode, db);
        List<Tile> hand = parser.fromTileSetXML(setDoc);

        Node nNode = setNode.getNextSibling();
        int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

        Tile tile = mPlayer.playTurn(board, hand, tilesLeft);
        Document tileXML = parser.tileParser.buildXML(tile);
        sendXMLToClient(socket, tileXML, "Admin: play-turn complete");
    }

    public static void processEndGame(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(boardNode, db);
        Board board = parser.boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(setNode, db);
        List<Integer> colors = parser.fromColorListSetXML(setDoc);

        mPlayer.endGame(board, colors);
        Document voidXML = parser.buildVoidXML();
        sendXMLToClient(socket, voidXML, "Admin: end-game complete");
    }

    public static void sendXMLToClient(AdminSocket socket, Document doc, String printMessage) throws Exception {
        String s = parser.documentToString(doc);
        System.out.println(printMessage + s);
        socket.writeOutputToClient(s);
    }
}
