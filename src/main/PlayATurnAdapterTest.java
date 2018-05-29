package tsuro;

import org.w3c.dom.Document;
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

import static tsuro.PlayATurnAdapter.parseInSPlayers;
import static tsuro.PlayATurnAdapter.parseOutSPlayers;


// Please ignore - this class was used to debug test-play-a-turn
public class PlayATurnAdapterTest {
    public static void main(String[] args) throws Exception {
        String deckStr = "";
        String inPlayerStr = "";
        String outPlayerStr = "";
        String boardStr = "";
        String tileStr = "";

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
        Board board = boardParser.fromXML(Parser.stringToDocument(db, state.boardStr));


        Document inPlayerDoc = Parser.stringToDocument(db, state.inSPlayerStr);
        Document outPlayerDoc = Parser.stringToDocument(db, state.outSPlayerStr);

        // parse inSPlayer XML
        SPlayer dragonOwner = null;
        Pair<List<SPlayer>, SPlayer> inRes = parseInSPlayers(db, state.inSPlayerStr,
                sPlayerParser, board);
        List<SPlayer> inSPlayer = inRes.first;
        dragonOwner = inRes.second;

        // parse outSPlayer XML
        List<SPlayer> outSPlayer = parseOutSPlayers(db, state.outSPlayerStr,
                sPlayerParser, board);

        Tile tileToPlay = tileParser.fromXML(Parser.stringToDocument(db, state.tileStr));
        List<Tile> tileList = Parser.fromTileSetXML(db, Parser.stringToDocument(db, state.drawPileStr));
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
