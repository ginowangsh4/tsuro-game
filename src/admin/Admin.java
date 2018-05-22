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
    public static void main(String[] args) throws Exception {
        // set up connection to local host 
        String hostname = "127.0.0.1";
        int port = Integer.parseInt("6666");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        AdminSocket socket = new AdminSocket(hostname, port, db);

        // initialize
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

        MPlayer mPlayer = new MPlayer(MPlayer.Strategy.R);  // how to assign strategy?
        mPlayer.initialize(color, colors);
        Document voidXML = Parser.buildVoidXML(db);
        String s = Parser.documentToString(voidXML);
        System.out.println("Admin: initialize complete " + s);
        socket.writeOutputToClient(s);

        // place-pawn
        s = socket.readInputFromClient();
        System.out.println(s);
        Document placePawnXML = Parser.stringToDocument(db, s);
        if (!placePawnXML.getFirstChild().getNodeName().equals("place-pawn")) {
            throw new IllegalArgumentException("Message is not place-pawn");
        }
        Node boardNode = placePawnXML.getFirstChild().getFirstChild();
        Document boardDoc = db.newDocument();
        imported = boardDoc.importNode(boardNode, true);
        boardDoc.appendChild(imported);
        Board board = boardParser.fromXML(boardDoc);

        Token token = mPlayer.placePawn(board);
        Document pawnLocXML = Parser.buildPawnLocXML(db, token.getPosition(), token.getIndex());
        s = Parser.documentToString(pawnLocXML);
        System.out.println("Admin: place-pawn complete " + s);
        socket.writeOutputToClient(s);

        while (socket.connectionEstablished()) {
            String res = socket.readInputFromClient();
            // server has closed the connection
            if (res == null) break;
            Document doc = Parser.stringToDocument(db, res);
            if (!doc.getFirstChild().getNodeName().equals("play-turn") &&
                    !doc.getFirstChild().getNodeName().equals("end-game") &&
                    !doc.getFirstChild().getNodeName().equals("get-name")) {
                throw new IllegalArgumentException("Message is not play-turn, end-game or get-name");
            }
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name":
                    String playerName = mPlayer.getName();
                    Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
                    s = Parser.documentToString(getNameResXML);
                    System.out.println("Admin: get-name complete " + s);
                    socket.writeOutputToClient(s);
                    break;

                case "play-turn":
                    boardNode = node.getFirstChild();
                    boardDoc = db.newDocument();
                    imported = boardDoc.importNode(boardNode, true);
                    boardDoc.appendChild(imported);
                    board = boardParser.fromXML(boardDoc);

                    Node setNode = boardNode.getNextSibling();
                    Document setDoc = db.newDocument();
                    imported = setDoc.importNode(setNode, true);
                    setDoc.appendChild(imported);
                    List<Tile> hand = Parser.fromTileSetXML(db, setDoc);

                    Node nNode = setNode.getNextSibling();
                    int tilesLeft = Integer.parseInt(nNode.getFirstChild().getTextContent());

                    Tile tile = mPlayer.playTurn(board, hand, tilesLeft);
                    Document tileXML = tileParser.buildXML(tile);
                    s = Parser.documentToString(tileXML);
                    System.out.println("Admin: play-turn complete " + s);
                    socket.writeOutputToClient(s);
                    break;

                case "end-game":
                    boardNode = node.getFirstChild();
                    boardDoc = db.newDocument();
                    imported = boardDoc.importNode(boardNode, true);
                    boardDoc.appendChild(imported);
                    board = boardParser.fromXML(boardDoc);

                    setNode = boardNode.getNextSibling();
                    setDoc = db.newDocument();
                    imported = setDoc.importNode(setNode, true);
                    setDoc.appendChild(imported);
                    colors = Parser.fromColorListSetXML(db, setDoc);

                    mPlayer.endGame(board, colors);
                    voidXML = Parser.buildVoidXML(db);
                    s = Parser.documentToString(voidXML);
                    System.out.println("Admin: end-game complete " + s);
                    socket.writeOutputToClient(s);
                    break;

                default:
                    break;
            }
        }
    }
}
