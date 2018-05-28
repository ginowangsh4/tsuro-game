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
        String deckStr = "<list></list>";
        String inPlayerStr = "<list><splayer-nodragon><color>red</color><set><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>4</n></connect></tile></set></splayer-nodragon><splayer-nodragon><color>green</color><set><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>4</n></connect><connect><n>3</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>6</n></connect></tile></set></splayer-nodragon><splayer-dragon><color>orange</color><set><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>2</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></set></splayer-dragon><splayer-nodragon><color>blue</color><set><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></set></splayer-nodragon></list>";
        String outPlayerStr = "<list><splayer-nodragon><color>hotpink</color><set></set></splayer-nodragon><splayer-nodragon><color>sienna</color><set></set></splayer-nodragon></list>";
        String boardStr = "<board><map><ent><xy><x>3</x><y>3</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>2</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>1</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>4</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>5</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>5</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>0</x><y>3</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>7</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>5</x><y>1</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>5</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>3</x><y>5</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>4</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>3</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>7</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>2</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>5</n></connect></tile></ent><ent><xy><x>0</x><y>1</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>0</x><y>5</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>3</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>5</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>2</x><y>4</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>1</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>3</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>0</x><y>2</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>4</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>4</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>5</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>3</x><y>4</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>3</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>2</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>1</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>4</x><y>5</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>4</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>2</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>5</n></connect></tile></ent><ent><xy><x>1</x><y>0</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>3</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent></map><map><ent><color>orange</color><pawn-loc><v></v><n>3</n><n>2</n></pawn-loc></ent><ent><color>red</color><pawn-loc><v></v><n>4</n><n>8</n></pawn-loc></ent><ent><color>sienna</color><pawn-loc><v></v><n>0</n><n>3</n></pawn-loc></ent><ent><color>blue</color><pawn-loc><v></v><n>5</n><n>9</n></pawn-loc></ent><ent><color>hotpink</color><pawn-loc><v></v><n>6</n><n>2</n></pawn-loc></ent><ent><color>green</color><pawn-loc><v></v><n>5</n><n>8</n></pawn-loc></ent></map></board>";
        String tileStr = "<tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile>";

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
