package tsuro.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    private static DocumentBuilder db;

    @BeforeAll
    public static void beforeAll() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    // ******************************* Network Architecture ***********************************
    //                   ----->              ---||-->        ----->
    //            Server         RemotePlayer   ||     Admin        MPlayer
    //                   <-----              <--||---        <-----
    // ****************************************************************************************

    // ****************************************************************************************
    // *********************** Build XML for Outgoing Inputs to NAdmin ************************
    // ****************************************************************************************
    @Test
    public void buildGetNameXMLTest() throws IOException, SAXException {
        Document actual = Parser.buildGetNameXML(db);
        String s = "<get-name></get-name>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document expected = db.parse(is);
        assertTrue(expected.isEqualNode(actual), "Failed to build getName XML");
    }

    @Test
    public void buildInitializeXMLTest() throws Exception {
        Document actual = Parser.buildInitializeXML(db, 0, Arrays.asList(0,1,2,3));
        String s = "<initialize>" +
                        "<color>blue</color>" +
                        "<list>" +
                            "<color>blue</color>" +
                            "<color>red</color>" +
                            "<color>green</color>" +
                            "<color>orange</color>" +
                        "</list>" +
                   "</initialize>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document expected = db.parse(is);
        // System.out.println(Parser.documentToString(actual));
        // System.out.println(Parser.documentToString(expected));
        assertTrue(expected.isEqualNode(actual), "Failed to build initialize XML");
    }

    @Test
    public void buildPlacePawnXMLTest() throws IOException, SAXException {
        String buffer = "<place-pawn><board>"
                +"<map>"
                +"<ent>"
                +"<xy><x>0</x><y>5</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>1</n></connect>"
                +"<connect><n>2</n><n>3</n></connect>"
                +"<connect><n>4</n><n>5</n></connect>"
                +"<connect><n>6</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"<ent>"
                +"<xy><x>3</x><y>2</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>1</n></connect>"
                +"<connect><n>2</n><n>4</n></connect>"
                +"<connect><n>3</n><n>6</n></connect>"
                +"<connect><n>5</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"<ent>"
                +"<xy><x>4</x><y>1</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>6</n></connect>"
                +"<connect><n>1</n><n>5</n></connect>"
                +"<connect><n>2</n><n>4</n></connect>"
                +"<connect><n>3</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"</map>"
                +"<map>"
                +"<ent>"
                +"<color>blue</color>"
                +"<pawn-loc><v></v><n>1</n><n>10</n></pawn-loc>"
                +"</ent>"
                +"<ent>"
                +"<color>red</color>"
                +"<pawn-loc><h></h><n>2</n><n>8</n></pawn-loc>"
                +"</ent>"
                +"</map>"
                +"</board></place-pawn>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);

        Board board = new Board();
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(t1,0,5);
        board.placeTile(t2,3,2);
        board.placeTile(t3,4,1);
        Token token1 = new Token(0, 2, new int[] {0, 5});
        Token token2 = new Token(1, 5, new int[] {4, 1});
        board.addToken(token1);
        board.addToken(token2);
        Document actual = Parser.buildPlacePawnXML(db, board);

        assertTrue(expected.isEqualNode(actual),"Parsing place-pawn does not give the expected XML");
    }

    @Test
    public void buildPlayTurnXMLTest() throws Exception {
        String buffer = "<play-turn>"
                +"<board>"
                    +"<map>"
                    +"<ent>"
                    +"<xy><x>0</x><y>5</y></xy>"
                    +"<tile>"
                    +"<connect><n>0</n><n>1</n></connect>"
                    +"<connect><n>2</n><n>3</n></connect>"
                    +"<connect><n>4</n><n>5</n></connect>"
                    +"<connect><n>6</n><n>7</n></connect>"
                    +"</tile>"
                    +"</ent>"
                    +"<ent>"
                    +"<xy><x>3</x><y>2</y></xy>"
                    +"<tile>"
                    +"<connect><n>0</n><n>1</n></connect>"
                    +"<connect><n>2</n><n>4</n></connect>"
                    +"<connect><n>3</n><n>6</n></connect>"
                    +"<connect><n>5</n><n>7</n></connect>"
                    +"</tile>"
                    +"</ent>"
                    +"<ent>"
                    +"<xy><x>4</x><y>1</y></xy>"
                    +"<tile>"
                    +"<connect><n>0</n><n>6</n></connect>"
                    +"<connect><n>1</n><n>5</n></connect>"
                    +"<connect><n>2</n><n>4</n></connect>"
                    +"<connect><n>3</n><n>7</n></connect>"
                    +"</tile>"
                    +"</ent>"
                    +"</map>"
                    +"<map>"
                    +"<ent>"
                    +"<color>blue</color>"
                    +"<pawn-loc><v></v><n>1</n><n>10</n></pawn-loc>"
                    +"</ent>"
                    +"<ent>"
                    +"<color>red</color>"
                    +"<pawn-loc><h></h><n>2</n><n>8</n></pawn-loc>"
                    +"</ent>"
                    +"</map>"
                +"</board>"
                +"<set>"
                    +"<tile>"
                    +"<connect><n>0</n><n>6</n></connect>"
                    +"<connect><n>1</n><n>5</n></connect>"
                    +"<connect><n>2</n><n>4</n></connect>"
                    +"<connect><n>3</n><n>7</n></connect>"
                    +"</tile>"
                +"</set>"
                +"<n>20</n>"
                +"</play-turn>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);

        Board board = new Board();
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(t1,0,5);
        board.placeTile(t2,3,2);
        board.placeTile(t3,4,1);
        Token token1 = new Token(0, 2,new int[] {0,5});
        Token token2 = new Token(1, 5,new int[] {4,1});
        board.addToken(token1);
        board.addToken(token2);
        Set<Tile> tileSet = new HashSet<>(Collections.singletonList(t3));

        Document actual = Parser.buildPlayTurnXML(db, board, tileSet, 20);
        assertTrue(expected.isEqualNode(actual),"Parsing play-turn does not give the expected XML");
    }

    @Test
    public void buildEndGameXMLTest() throws Exception {
        String buffer = "<end-game>"
                +"<board>"
                +"<map>"
                +"<ent>"
                +"<xy><x>0</x><y>5</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>1</n></connect>"
                +"<connect><n>2</n><n>3</n></connect>"
                +"<connect><n>4</n><n>5</n></connect>"
                +"<connect><n>6</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"<ent>"
                +"<xy><x>3</x><y>2</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>1</n></connect>"
                +"<connect><n>2</n><n>4</n></connect>"
                +"<connect><n>3</n><n>6</n></connect>"
                +"<connect><n>5</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"<ent>"
                +"<xy><x>4</x><y>1</y></xy>"
                +"<tile>"
                +"<connect><n>0</n><n>6</n></connect>"
                +"<connect><n>1</n><n>5</n></connect>"
                +"<connect><n>2</n><n>4</n></connect>"
                +"<connect><n>3</n><n>7</n></connect>"
                +"</tile>"
                +"</ent>"
                +"</map>"
                +"<map>"
                +"<ent>"
                +"<color>blue</color>"
                +"<pawn-loc><v></v><n>1</n><n>10</n></pawn-loc>"
                +"</ent>"
                +"<ent>"
                +"<color>red</color>"
                +"<pawn-loc><h></h><n>2</n><n>8</n></pawn-loc>"
                +"</ent>"
                +"</map>"
                +"</board>"
                +"<set>"
                +"<color>blue</color>"
                +"<color>red</color>"
                +"</set>"
                +"</end-game>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);

        Board board = new Board();
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(t1,0,5);
        board.placeTile(t2,3,2);
        board.placeTile(t3,4,1);
        Token token1 = new Token(0, 2,new int[] {0,5});
        Token token2 = new Token(1, 5,new int[] {4,1});
        board.addToken(token1);
        board.addToken(token2);
        Set<Integer> colorSet = new HashSet<>(Arrays.asList(0,1));

        Document actual = Parser.buildEndGameXML(db, board, colorSet);
        assertTrue(expected.isEqualNode(actual),"Parsing end-game does not give the expected XML");
    }

    // ****************************************************************************************
    // ******************** Build XML for Outgoing Outputs to AdminSocket ***************************
    // ****************************************************************************************
    @Test
    public void buildVoidXMLTest() throws IOException, SAXException {
        String buffer = "<void></void>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);
        Document actual = Parser.buildVoidXML(db);
        assertTrue(expected.isEqualNode(actual),"Parsing void does not give the expected XML");
    }

    @Test
    public void buildPawnLocXMLTest() throws IOException, SAXException {
        String buffer = "<pawn-loc>" +
                "<h></h>" +
                "<n>1</n>" +
                "<n>1</n>" +
                "</pawn-loc>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);

        int[] pos1 = new int[]{0, 0};
        int index1 = 4;
        int[] pos2 = new int[]{0, 1};
        int index2 = 1;

        Document actual1 = Parser.buildPawnLocXML(db, pos1, index1);
        Document actual2 = Parser.buildPawnLocXML(db, pos2, index2);
        assertTrue(expected.isEqualNode(actual1),"Parsing pawn-loc does not give the expected XML");
        assertTrue(expected.isEqualNode(actual2),"Parsing pawn-loc does not give the expected XML");
    }

    // ****************************************************************************************
    // **************** Decompose XML from Incoming Input from Server *************************
    // ****************************************************************************************
    @Test
    public void fromColorListXML() throws Exception {
        String s = "<list>" +
                "<color>blue</color>" +
                "<color>red</color>" +
                "<color>green</color>" +
                "<color>orange</color>" +
                "<color>sienna</color>" +
                "<color>hotpink</color>" +
                "<color>darkgreen</color>" +
                "<color>purple</color>" +
                "</list>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document doc = db.parse(is);
        System.out.println(Parser.documentToString(doc));
        List<Integer> actual = Parser.fromColorListSetXML(db, doc);

        List<Integer> expected = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7));
        assertTrue(expected.equals(actual), "Failed to build color list from XML");
    }

    // ****************************************************************************************
    // **************** Decompose XML from Incoming Outputs from NAdmin ***********************
    // ****************************************************************************************
    @Test
    public void fromGetNameXMLTest() throws IOException, SAXException {
        String s = "<player-name>blue</player-name>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document doc = db.parse(is);
        String actual = Parser.fromGetNameXML(db, doc);
        assertEquals("blue", actual, "Failed to build string from XML");
    }

    @Test
    public void fromPlacePawnXMLTest() throws IOException, SAXException {
        String buffer = "<pawn-loc>" +
                "<h></h>" +
                "<n>1</n>" +
                "<n>1</n>" +
                "</pawn-loc>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc = db.parse(is);

        Board board = new Board();
        Tile tile = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(tile, 0, 0);
        Token token = new Token(1, 4, new int[] {0, 0});
        board.addToken(token);

        Server server = Server.getInstance();
        server.setState(board, null, null, null);

        Pair<int[], Integer> pair = Parser.fromPlacePawnXML(db, doc);

        assertArrayEquals(new int[]{0,0}, pair.first, "Failed to build position from XML");
        assertEquals(new Integer(4), pair.second, "Failed to build indexOnTile from XML");
    }
}
