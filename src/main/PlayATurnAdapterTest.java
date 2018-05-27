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

        List<SPlayer> inSPlayer = new ArrayList<>();
        List<SPlayer> outSPlayer = new ArrayList<>();
        Document inPlayerDoc = Parser.stringToDocument(db, state.inSPlayerStr);
        Document outPlayerDoc = Parser.stringToDocument(db, state.outSPlayerStr);
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
