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

    public static final int PORT_NUM = 12345;
    public static MPlayer mPlayer;

    // Enter from stdin "name strategy (M, LS, MS)"
    public static void main(String[] args) throws Exception {
        // set up connection to local host 
        String hostname = "127.0.0.1";
        int port = PORT_NUM;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        AdminSocket socket = new AdminSocket(hostname, port, db);
        String s = null;

        switch (args[1]) {
            case "R":
                mPlayer = new MPlayer(MPlayer.Strategy.R, args[0]);
                break;
            case "LS":
                mPlayer = new MPlayer(MPlayer.Strategy.LS, args[0]);
                break;
            case "MS":
                mPlayer = new MPlayer(MPlayer.Strategy.MS, args[0]);
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy!");
        }

        while (socket.connectionEstablished()) {
            String res = socket.readInputFromClient();
            // server has closed the connection
            if (res == null) break;
            Document doc = Parser.stringToDocument(db, res);
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name":
                    processGetName(db, socket);
                    break;
                case "initialize":
                    processInitialize(db, socket, node);
                    System.out.println("["+mPlayer.state.toString()+"]");

                    break;
                case "place-pawn":
                    processPlacePawn(db, socket, node);
                    System.out.println("["+mPlayer.state.toString()+"]");

                    break;
                case "play-turn":
                    processPlayTurn(db, socket, node);
                    System.out.println("["+mPlayer.state.toString()+"]");

                    break;
                case "end-game":
                    processEndGame(db, socket, node);
                    System.out.println("["+mPlayer.state.toString()+"]");

                    break;
                default:
                    break;
            }
        }
    }

    public static void processInitialize(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        Node colorNode = node.getFirstChild();

        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = Parser.fromNodeToDoc(db, colorsNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, colorsDoc);

        mPlayer.initialize(color, colors);
        Document voidXML = Parser.buildVoidXML(db);
        sendXMLToClient(socket, voidXML, "Admin: initialize complete ");
    }

    public static void processPlacePawn(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Token token = mPlayer.placePawn(board);
        Document pawnLocXML = Parser.buildPawnLocXML(db, token.getPosition(), token.getIndex());
        sendXMLToClient(socket, pawnLocXML, "Admin: place-pawn complete");
    }

    public static void processGetName(DocumentBuilder db, AdminSocket socket) throws Exception {
        String playerName = mPlayer.getName();
        Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
        sendXMLToClient(socket, getNameResXML, "Admin: get-name complete");
    }

    public static void processPlayTurn(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(db, setNode);
        List<Tile> hand = Parser.fromTileSetXML(db, setDoc);

        Node nNode = setNode.getNextSibling();
        int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

        Tile tile = mPlayer.playTurn(board, hand, tilesLeft);
        Document tileXML = tileParser.buildXML(tile);
        sendXMLToClient(socket, tileXML, "Admin: play-turn complete");
    }

    public static void processEndGame(DocumentBuilder db, AdminSocket socket, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(db, setNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, setDoc);

        mPlayer.endGame(board, colors);
        Document voidXML = Parser.buildVoidXML(db);
        sendXMLToClient(socket, voidXML, "Admin: end-game complete");
    }

    public static void sendXMLToClient(AdminSocket socket, Document doc, String printMessage) throws Exception {
        String s = Parser.documentToString(doc);
        System.out.println(printMessage + s);
        socket.writeOutputToClient(s);
    }
}
