package tsuro;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.parser.BoardParser;
import tsuro.parser.Parser;
import tsuro.parser.SPlayerParser;
import tsuro.parser.TileParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;


public class PlayATurn {
    public static void main(String[] args) throws Exception {
        for (String s : args) {
            System.out.println(s);
        }
        String deckStr = args[0];
        String inPlayerStr = args[1];
        String outPlayerStr = args[2];
        String boardStr = args[3];
        String tileStr = args[4];

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        SPlayerParser sPlayerParser = new SPlayerParser(db);
        TileParser tileParser = new TileParser(db);
        BoardParser boardParser = new BoardParser(db);

        // ***************************************************
        // Parse input XML to game objects
        // ***************************************************
        Board board = boardParser.fromXML(Parser.stringToDocument(db, boardStr));

        List<SPlayer> inSPlayer = new ArrayList<>();
        List<SPlayer> outSPlayer = new ArrayList<>();
        Document inPlayerDoc = Parser.stringToDocument(db, inPlayerStr);
        Document outPlayerDoc = Parser.stringToDocument(db, outPlayerStr);
        NodeList inPlayerList = inPlayerDoc.getFirstChild().getChildNodes();
        NodeList outPlayerList = outPlayerDoc.getFirstChild().getChildNodes();

        SPlayer dragonOwner = null;
        for (int i = 0; i < inPlayerList.getLength(); i++) {
            Node inPlayerNode = inPlayerList.item(i);
            Document doc = db.newDocument();
            Node imported = doc.importNode(inPlayerNode, true);
            doc.appendChild(imported);

            Token token = findToken(board, Token.getColorInt(inPlayerNode.getFirstChild().getTextContent()));

            Pair<SPlayer, Boolean> inPlayer = sPlayerParser.fromXML(doc, token);
            if (inPlayer.second) {
                dragonOwner = inPlayer.first;
            }
            inSPlayer.add(inPlayer.first);
        }

        for (int i = 0; i < outPlayerList.getLength(); i++) {
            Node outPlayerNode = outPlayerList.item(i);
            Document doc = db.newDocument();
            Node imported = doc.importNode(outPlayerNode, true);
            doc.appendChild(imported);

            Token token = findToken(board, Token.getColorInt(outPlayerNode.getFirstChild().getTextContent()));

            Pair<SPlayer, Boolean> outPlayer = sPlayerParser.fromXML(doc, token);
            outSPlayer.add(outPlayer.first);
        }

        Tile tileToPlay = tileParser.fromXML(Parser.stringToDocument(db, tileStr));
        List<Tile> tileList = Parser.fromTileSetXML(db, Parser.stringToDocument(db, deckStr));
        Deck deck = new Deck(tileList);
        // ***************************************************
        // Play a turn
        // ***************************************************
        Server server = Server.getInstance();
        server.setState(board, inSPlayer, outSPlayer, deck);
        server.giveDragon(dragonOwner);
        List<SPlayer> winners = server.playATurn(tileToPlay);

        // ***************************************************
        // Parse game objects to output XMLs
        // ***************************************************
        Document tileRes = Parser.buildTileListXML(db, server.drawPile.getPile());

        Document inPlayerRes = db.newDocument();
        Node inList = inPlayerRes.createElement("list");
        for (SPlayer sp : server.inSPlayer) {
            Document spRes = sPlayerParser.buildXML(sp, hasDragon(server, sp));
            inList.appendChild(inPlayerRes.importNode(spRes.getFirstChild(), true));
        }
        inPlayerRes.appendChild(inList);

        Document outPlayerRes = db.newDocument();
        Element outList = outPlayerRes.createElement("list");
        for (SPlayer sp : server.outSPlayer) {
            Document spRes = sPlayerParser.buildXML(sp, false);
            outList.appendChild(outPlayerRes.importNode(spRes.getFirstChild(), true));
        }
        outPlayerRes.appendChild(outList);

        Document boardRes = boardParser.buildXML(server.board);

        Document winnersRes = db.newDocument();
        if (winners == null) {
            Element f = winnersRes.createElement("false");
            winnersRes.appendChild(f);
        } else {
            Element l = winnersRes.createElement("list");
            for (SPlayer sp : server.inSPlayer) {
                Document spElement = sPlayerParser.buildXML(sp, hasDragon(server, sp));
                l.appendChild(winnersRes.importNode(spElement.getFirstChild(), true));
            }
            winnersRes.appendChild(l);
        }

        System.out.println("**** RESULT ****");
        System.out.println(Parser.documentToString(tileRes));
        System.out.println(Parser.documentToString(inPlayerRes));
        System.out.println(Parser.documentToString(outPlayerRes));
        System.out.println(Parser.documentToString(boardRes));
        System.out.println(Parser.documentToString(winnersRes));
    }

    public static Token findToken(Board board, int color) {
        for (Token t : board.tokenList) {
            if (t.getColor() == color) {
                return t;
            }
        }
        throw new IllegalArgumentException("Cannot find token on board");
    }

    public static boolean hasDragon(Server server, SPlayer sp) {
        return (server.dragonHolder != null && server.dragonHolder.isSamePlayer(sp));
    }
}
