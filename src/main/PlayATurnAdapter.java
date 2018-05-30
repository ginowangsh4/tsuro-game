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
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Parser parser = new Parser(db);
        while (true) {
            // input from stdin
            String deckStr = br.readLine();
            if (deckStr == null) break;
            String inPlayerStr = br.readLine();
            String outPlayerStr = br.readLine();
            String boardStr = br.readLine();
            String tileStr = br.readLine();


            // ***************************************************
            // Parse input XMLs to game objects
            // ***************************************************
            // parse board XML
            Board board = parser.boardParser.fromXML(parser.stringToDocument(boardStr));

            // parse inSPlayer XML
            SPlayer dragonOwner = null;
            Pair<List<SPlayer>, SPlayer> inRes = parser.fromSPlayerListXML(inPlayerStr, board);
            List<SPlayer> inSPlayer = inRes.first;
            dragonOwner = inRes.second;

            // parse outSPlayer XML
            Pair<List<SPlayer>, SPlayer> outRes = parser.fromSPlayerListXML(outPlayerStr, board);
            List<SPlayer> outSPlayer = outRes.first;

            // parse tile XML to play this turn
            Tile tileToPlay = parser.tileParser.fromXML(parser.stringToDocument(tileStr));

            // parse deck/draw pile XML
            List<Tile> tileList = parser.fromTileSetXML(parser.stringToDocument(deckStr));
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
            Document tileRes = parser.buildTileListXML(server.drawPile.getPile());

            // parse back inSPlayer
            Document inPlayerRes = parser.buildSPlayerListXML(server, server.inSPlayer);

            // parse back outSPlayer
            Document outPlayerRes = parser.buildSPlayerListXML(server, server.outSPlayer);

            // parse back board
            Document boardRes = parser.boardParser.buildXML(server.board);

            // parse back winners
            Document winnersRes = parser.buildWinnersXML(server, winners);

            // output to stdout
            printResult(parser, tileRes, inPlayerRes, outPlayerRes, boardRes, winnersRes);
        }

    }

    private static void printResult(Parser parser, Document tileRes, Document inPlayerRes,Document outPlayerRes,
                                    Document boardRes,Document winnersRes) throws Exception {
        System.out.println(parser.documentToString(tileRes));
        System.out.println(parser.documentToString(inPlayerRes));
        System.out.println(parser.documentToString(outPlayerRes));
        System.out.println(parser.documentToString(boardRes));
        System.out.println(parser.documentToString(winnersRes));
    }




}
