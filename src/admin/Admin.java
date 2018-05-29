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

    public static MPlayer mPlayer;

    public static void main(String[] args) throws Exception {
        // set up connection to local host 
        String hostname = "127.0.0.1";
        int port = 8000;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        AdminSocket socket = new AdminSocket(hostname, port, db);
        String s = null;

        // initialize
        mPlayer = processInitialize(db, socket);

        // place-pawn
        processPlacePawn(db, socket, mPlayer);

        while (socket.connectionEstablished()) {
            String res = socket.readInputFromClient();
            // server has closed the connection
            if (res == null) break;
            Document doc = Parser.stringToDocument(db, res);
            if (!doc.getFirstChild().getNodeName().equals("play-turn")
                    && !doc.getFirstChild().getNodeName().equals("end-game")
                    && !doc.getFirstChild().getNodeName().equals("get-name")) {
                throw new IllegalArgumentException("Message is not play-turn, end-game or get-name");
            }
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name":
                    processGetName(db, socket, mPlayer);
                    break;
                case "play-turn":
                    processPlayTurn(db, socket, mPlayer, node);
                    break;
                case "end-game":
                    processEndGame(db, socket, mPlayer, node);
                    break;
                default:
                    break;
            }
        }
    }

    public static MPlayer processInitialize(DocumentBuilder db, AdminSocket socket) throws Exception {
        Document initializeXML = Parser.stringToDocument(db, socket.readInputFromClient());
        if (!initializeXML.getFirstChild().getNodeName().equals("initialize")) {
            throw new IllegalArgumentException("Message is not initialize");
        }

        Node initializeNode = initializeXML.getFirstChild();
        Node colorNode = initializeNode.getFirstChild();
        int color = Token.getColorInt(colorNode.getTextContent());

        Node colorsNode = colorNode.getNextSibling();
        Document colorsDoc = Parser.fromNodeToDoc(db, colorsNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, colorsDoc);

        // TODO: how to assign strategy?
        MPlayer mPlayer = new MPlayer(MPlayer.Strategy.R);
        mPlayer.initialize(color, colors);
        Document voidXML = Parser.buildVoidXML(db);
        sendXMLToClient(socket,voidXML, "Admin: initialize complete ");

        return mPlayer;
    }

    public static void processPlacePawn(DocumentBuilder db, AdminSocket socket, MPlayer mPlayer) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        String s = socket.readInputFromClient();
        Document placePawnXML = Parser.stringToDocument(db, s);
        if (!placePawnXML.getFirstChild().getNodeName().equals("place-pawn")) {
            throw new IllegalArgumentException("Message is not place-pawn");
        }

        Node boardNode = placePawnXML.getFirstChild().getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Token token = mPlayer.placePawn(board);
        Document pawnLocXML = Parser.buildPawnLocXML(db, token.getPosition(), token.getIndex());
        sendXMLToClient(socket,pawnLocXML, "Admin: place-pawn complete ");
    }

    public static void processGetName(DocumentBuilder db, AdminSocket socket, MPlayer mPlayer) throws Exception {
        String playerName = mPlayer.getName();
        Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
        sendXMLToClient(socket,getNameResXML, "Admin: get-name complete ");
    }

    public static void processPlayTurn(DocumentBuilder db, AdminSocket socket, MPlayer mPlayer, Node node) throws Exception {
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
        sendXMLToClient(socket,tileXML, "Admin: play-turn complete ");
    }

    public static void processEndGame(DocumentBuilder db, AdminSocket socket, MPlayer mPlayer, Node node) throws Exception {
        BoardParser boardParser = new BoardParser(db);

        Node boardNode = node.getFirstChild();
        Document boardDoc = Parser.fromNodeToDoc(db, boardNode);
        Board board = boardParser.fromXML(boardDoc);

        Node setNode = boardNode.getNextSibling();
        Document setDoc = Parser.fromNodeToDoc(db, setNode);
        List<Integer> colors = Parser.fromColorListSetXML(db, setDoc);

        mPlayer.endGame(board, colors);
        Document voidXML = Parser.buildVoidXML(db);
        sendXMLToClient(socket,voidXML, "Admin: end-game complete ");
    }

    public static void sendXMLToClient(AdminSocket socket, Document doc, String printMessage) throws Exception {
        String s = Parser.documentToString(doc);
        System.out.println(printMessage + s);
        socket.writeOutputToClient(s);
    }
}
