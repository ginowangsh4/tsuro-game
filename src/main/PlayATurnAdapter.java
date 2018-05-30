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

@SuppressWarnings("Duplicates")
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

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            SPlayerParser sPlayerParser = new SPlayerParser(db);
            TileParser tileParser = new TileParser(db);
            BoardParser boardParser = new BoardParser(db);

            // ***************************************************
            // Parse input XMLs to game objects
            // ***************************************************
            // parse board XML
            Board board = boardParser.fromXML(Parser.stringToDocument(db, boardStr));

            // parse inSPlayer XML
            SPlayer dragonOwner = null;
            Pair<List<SPlayer>, SPlayer> inRes = parseSPlayersXML(db, inPlayerStr, sPlayerParser, board);
            List<SPlayer> inSPlayer = inRes.first;
            dragonOwner = inRes.second;

            // parse outSPlayer XML
            Pair<List<SPlayer>, SPlayer> outRes = parseSPlayersXML(db, outPlayerStr, sPlayerParser, board);
            List<SPlayer> outSPlayer = outRes.first;

            // parse tile XML to play this turn
            Tile tileToPlay = tileParser.fromXML(Parser.stringToDocument(db, tileStr));

            // parse deck/draw pile XML
            List<Tile> tileList = Parser.fromTileSetXML(db, Parser.stringToDocument(db, deckStr));
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
            Document inPlayerRes = parseSPlayersToXML(db, server, sPlayerParser, server.inSPlayer);

            // parse back outSPlayer
            Document outPlayerRes = parseSPlayersToXML(db, server, sPlayerParser, server.outSPlayer);

            // parse back board
            Document boardRes = boardParser.buildXML(server.board);

            // parse back winners
            Document winnersRes = parseWinnersToXML(db, sPlayerParser, server, winners);

            // output to stdout
            printResult(tileRes, inPlayerRes, outPlayerRes, boardRes, winnersRes);
        }

    }

    private static Document parseWinnersToXML(DocumentBuilder db, SPlayerParser sPlayerParser, Server server, List<SPlayer> winners) {
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
        return winnersRes;
    }

    public static Document parseSPlayersToXML(DocumentBuilder db, Server server, SPlayerParser sPlayerParser, List<SPlayer> sPlayers) {
        Document playerRes = db.newDocument();
        Node inList = playerRes.createElement("list");
        for (SPlayer sp : sPlayers) {
            Document spRes = sPlayerParser.buildXML(sp, hasDragon(server, sp));
            inList.appendChild(playerRes.importNode(spRes.getFirstChild(), true));
        }
        playerRes.appendChild(inList);
        return playerRes;
    }

    public static Pair<List<SPlayer>, SPlayer> parseSPlayersXML(DocumentBuilder db, String inSPlayerStr,
                                                                SPlayerParser sPlayerParser, Board board) throws Exception {
        // parse inSPlayer XML
        List<SPlayer> inSPlayer = new ArrayList<>();
        Document inPlayerDoc = Parser.stringToDocument(db, inSPlayerStr);
        NodeList inPlayerList = inPlayerDoc.getFirstChild().getChildNodes();
        SPlayer dragonOwner = null;
        for (int i = 0; i < inPlayerList.getLength(); i++) {
            Node inPlayerNode = inPlayerList.item(i);
            Document doc = Parser.fromNodeToDoc(db, inPlayerNode);

            SPlayer sp = findSPlayer(board, Token.getColorInt(inPlayerNode.getFirstChild().getTextContent()));

            Boolean hasDragon = sPlayerParser.fromXML(doc, sp);
            if (hasDragon) {
                dragonOwner = sp;
            }
            inSPlayer.add(sp);
        }
        return new Pair(inSPlayer, dragonOwner);
    }
    

    private static void printResult(Document tileRes, Document inPlayerRes,Document outPlayerRes,
                                    Document boardRes,Document winnersRes) throws Exception {
        System.out.println(Parser.documentToString(tileRes));
        System.out.println(Parser.documentToString(inPlayerRes));
        System.out.println(Parser.documentToString(outPlayerRes));
        System.out.println(Parser.documentToString(boardRes));
        System.out.println(Parser.documentToString(winnersRes));
    }

    private static SPlayer findSPlayer(Board board, int color) {
        for (SPlayer sp : board.getSPlayerList()) {
            if (sp.getToken().getColor() == color) {
                return sp;
            }
        }
        throw new IllegalArgumentException("Cannot find token on board");
    }

    private static boolean hasDragon(Server server, SPlayer sp) {
        return (server.dragonHolder != null && server.dragonHolder.isSamePlayer(sp));
    }
}
