package tsuro;

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




// Please ignore - this class was used to debug test-play-a-turn
public class PlayATurnAdapterTest {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Parser parser = new Parser(db);

        // input from stdin
        String deckStr = "";
        String inPlayerStr = "";
        String outPlayerStr = "";
        String boardStr = "";
        String tileStr = "";

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
    }
}
