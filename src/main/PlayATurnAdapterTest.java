package tsuro;

import tsuro.parser.BoardParser;
import tsuro.parser.Parser;
import tsuro.parser.SPlayerParser;
import tsuro.parser.TileParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

import static tsuro.PlayATurnAdapter.parseSPlayersXML;



// Please ignore - this class was used to debug test-play-a-turn
public class PlayATurnAdapterTest {
    public static void main(String[] args) throws Exception {
        String deckStr = "";
        String inPlayerStr = "";
        String outPlayerStr = "";
        String boardStr = "";
        String tileStr = "";

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
    }


}
