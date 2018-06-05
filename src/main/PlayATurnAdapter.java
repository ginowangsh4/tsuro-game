package tsuro;

import org.w3c.dom.Document;
import tsuro.parser.Parser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayATurnAdapter {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Parser parser = new Parser(db);

        while (true) {
            // ***************************************************
            // Parse input XMLs to game objects
            // ***************************************************
            // input from stdin
            String deckStr = br.readLine();
            if (deckStr == null) break;
            String inPlayerStr = br.readLine();
            String outPlayerStr = br.readLine();
            String boardStr = br.readLine();
            String tileStr = br.readLine();

            // parse board XML
            Board board = parser.boardParser.fromXML(parser.stringToDocument(boardStr));

            // parse inSPlayers XML
            SPlayer dragonOwner = null;
            Pair<List<SPlayer>, SPlayer> inRes = parser.fromSPlayerListXML(inPlayerStr, board);
            List<SPlayer> inSPlayer = inRes.first;
            dragonOwner = inRes.second;

            // parse outSPlayers XML
            Pair<List<SPlayer>, SPlayer> outRes = parser.fromSPlayerListXML(outPlayerStr, board);
            List<SPlayer> outSPlayer = outRes.first;

            // parse tile XML to play this turn
            Tile tileToPlay = parser.tileParser.fromXML(parser.stringToDocument(tileStr));

            // parse deck/draw pile XML
            List<Tile> tileList = parser.fromTileSetXML(parser.stringToDocument(deckStr));
            Deck deck = new Deck(tileList);

            // ***************************************************
            // Now call play a turn
            // ***************************************************
            Server server = Server.getInstance();
            server.setState(board, inSPlayer, outSPlayer, new ArrayList<>(), deck);
            server.giveDragon(dragonOwner);
            List<SPlayer> winners = server.playATurn(tileToPlay);

            // ***************************************************
            // Parse game objects back to output XMLs
            // ***************************************************
            // build deck/draw pile XML
            Document tileRes = parser.buildTileListXML(server.drawPile.getPile());

            // build inSPlayers XML
            Document inPlayerRes = parser.buildSPlayerListXML(server, server.inSPlayers);

            // build outSPlayers XML
            Document outPlayerRes = parser.buildSPlayerListXML(server, server.outSPlayers);

            // build board XML
            Document boardRes = parser.boardParser.buildXML(server.board);

            // build winners XML
            Document winnersRes = parser.buildWinnersXML(server, winners);

            // output to stdout
            printResult(parser, tileRes, inPlayerRes, outPlayerRes, boardRes, winnersRes);
        }
    }

    private static void printResult(Parser parser, Document tileRes, Document inPlayerRes,
                                    Document outPlayerRes, Document boardRes, Document winnersRes) throws Exception {
        System.out.println(parser.documentToString(tileRes));
        System.out.println(parser.documentToString(inPlayerRes));
        System.out.println(parser.documentToString(outPlayerRes));
        System.out.println(parser.documentToString(boardRes));
        System.out.println(parser.documentToString(winnersRes));
    }
}
