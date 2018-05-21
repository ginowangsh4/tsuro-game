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
        String hostname = "127.0.0.1";
        int port = Integer.parseInt("52656");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        AdminSocket socket = new AdminSocket(hostname, port, db);

        // 1. initialize
        String s = socket.readInputFromClient();
        System.out.println(s);
        Document initializeXML = Parser.stringToDocument(db, s);
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
        s = Parser.documentToString(voidXML);
        System.out.println(s);
        socket.writeOutputToClient(s);

        // 2. place-pawn
        Document placePawnXML = Parser.stringToDocument(db, socket.readInputFromClient());
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
        System.out.println(s);
        socket.writeOutputToClient(s);

        while (socket.connectionEstablished()) {
            Document doc = Parser.stringToDocument(db, socket.readInputFromClient());
            if (!doc.getFirstChild().getNodeName().equals("play-turn") &&
                    !doc.getFirstChild().getNodeName().equals("end-game") &&
                    !doc.getFirstChild().getNodeName().equals("get-name")) {
                throw new IllegalArgumentException("Message is not play-turn or end-game");
            }
            Node node = doc.getFirstChild();
            switch (node.getNodeName()) {
                case "get-name": {
                    String playerName = mPlayer.getName();
                    System.out.println("Player's name is " + playerName);

                    Document getNameResXML = Parser.buildPlayerNameXML(db, playerName);
                    socket.writeOutputToClient(Parser.documentToString(getNameResXML));
                    break;
                }
                case "play-turn": {
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
                    socket.writeOutputToClient(Parser.documentToString(tileXML));
                    break;
                }
                case "end-game": {
                    boardNode = node.getFirstChild();
                    boardDoc = db.newDocument();
                    imported = boardDoc.importNode(boardNode, true);
                    boardDoc.appendChild(imported);
                    board = boardParser.fromXML(boardDoc);

                    Node setNode = boardNode.getNextSibling();
                    Document setDoc = db.newDocument();
                    imported = setDoc.importNode(setNode, true);
                    setDoc.appendChild(imported);
                    colors = Parser.fromColorListSetXML(db, setDoc);

                    mPlayer.endGame(board, colors);
                    voidXML = Parser.buildVoidXML(db);
                    socket.writeOutputToClient(Parser.documentToString(voidXML));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Invalid message tag");
            }
        }
    }
}
