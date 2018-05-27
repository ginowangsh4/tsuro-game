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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayATurnAdapter {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // input from stdin
            String deckStr = br.readLine();
            if (deckStr == null) break;
            String inPlayerStr = br.readLine();
            String outPlayerStr = br.readLine();
            String boardStr = br.readLine();
            String tileStr = br.readLine();

            State state = new State();
            state.setPlayATurnState(deckStr, inPlayerStr, outPlayerStr, boardStr, tileStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            SPlayerParser sPlayerParser = new SPlayerParser(db);
            TileParser tileParser = new TileParser(db);
            BoardParser boardParser = new BoardParser(db);

            // ***************************************************
            // Parse input XMLs to game objects
            // ***************************************************
            // parse board XML
            Board board = boardParser.fromXML(Parser.stringToDocument(db, state.boardStr));
            // parse inSPlayer XML
            List<SPlayer> inSPlayer = new ArrayList<>();
            Document inPlayerDoc = Parser.stringToDocument(db, state.inSPlayerStr);
            NodeList inPlayerList = inPlayerDoc.getFirstChild().getChildNodes();
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
            // parse outSPlayer XML
            List<SPlayer> outSPlayer = new ArrayList<>();
            Document outPlayerDoc = Parser.stringToDocument(db, state.outSPlayerStr);
            NodeList outPlayerList = outPlayerDoc.getFirstChild().getChildNodes();
            for (int i = 0; i < outPlayerList.getLength(); i++) {
                Node outPlayerNode = outPlayerList.item(i);
                Document doc = db.newDocument();
                Node imported = doc.importNode(outPlayerNode, true);
                doc.appendChild(imported);

                Token token = findToken(board, Token.getColorInt(outPlayerNode.getFirstChild().getTextContent()));
                Pair<SPlayer, Boolean> outPlayer = sPlayerParser.fromXML(doc, token);
                outSPlayer.add(outPlayer.first);
            }
            // parse tile XML to play this turn
            Tile tileToPlay = tileParser.fromXML(Parser.stringToDocument(db, state.tileStr));
            // parse deck/draw pile XML
            List<Tile> tileList = Parser.fromTileSetXML(db, Parser.stringToDocument(db, state.drawPileStr));
            Deck deck = new Deck(tileList);

            // ***************************************************
            // Play a turn
            // ***************************************************
            Server server = Server.getInstance();
            server.setState(board, inSPlayer, outSPlayer, new ArrayList<>(), deck);
            server.giveDragon(dragonOwner);
            List<SPlayer> winners = server.playATurn(tileToPlay);

            // ***************************************************
            // Parse game objects to output XMLs
            // ***************************************************
            // parse back deck/draw pile
            Document tileRes = Parser.buildTileListXML(db, server.drawPile.getPile());
            // parse back inSPlayer
            Document inPlayerRes = db.newDocument();
            Node inList = inPlayerRes.createElement("list");
            for (SPlayer sp : server.inSPlayer) {
                Document spRes = sPlayerParser.buildXML(sp, hasDragon(server, sp));
                inList.appendChild(inPlayerRes.importNode(spRes.getFirstChild(), true));
            }
            inPlayerRes.appendChild(inList);
            // parse back outSPlayer
            Document outPlayerRes = db.newDocument();
            Element outList = outPlayerRes.createElement("list");
            for (SPlayer sp : server.outSPlayer) {
                Document spRes = sPlayerParser.buildXML(sp, false);
                outList.appendChild(outPlayerRes.importNode(spRes.getFirstChild(), true));
            }
            outPlayerRes.appendChild(outList);
            // parse back board
            Document boardRes = boardParser.buildXML(server.board);
            // parse back winners
            Document winnersRes = db.newDocument();
            if (winners == null) {
                Element f = winnersRes.createElement("false");
                winnersRes.appendChild(f);
            } else {
                Element l = winnersRes.createElement("list");
                for (SPlayer sp : server.winners) {
                    Document spElement = sPlayerParser.buildXML(sp, hasDragon(server, sp));
                    l.appendChild(winnersRes.importNode(spElement.getFirstChild(), true));
                }
                winnersRes.appendChild(l);
            }
            // output to stdout
            System.out.println(Parser.documentToString(tileRes));
            System.out.println(Parser.documentToString(inPlayerRes));
            System.out.println(Parser.documentToString(outPlayerRes));
            System.out.println(Parser.documentToString(boardRes));
            System.out.println(Parser.documentToString(winnersRes));
        }

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
