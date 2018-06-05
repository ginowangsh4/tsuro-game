package tsuro.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import tsuro.Board;
import tsuro.SPlayer;
import tsuro.Tile;
import tsuro.Token;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardParserTest {
    private static DocumentBuilder db;
    private static BoardParser boardParser;

    @BeforeAll
    public static void beforeAll() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        boardParser = new BoardParser(db);
    }

    // Test buildXML using a board with no tile or pawn
    @Test
    public void buildXMLTest1() throws Exception {
        String buffer = "<board><map></map><map></map></board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected, doc;
        Board board = new Board();
        expected = db.parse(is);
        doc = boardParser.buildXML(board);
        assertTrue(expected.isEqualNode(doc),"Parsing board does not give the expected XML");
    }

    // Test buildXML using a board with 3 tiles and 2 tokens
    @Test
    public void buildXMLTest2() throws Exception {
        String buffer = "<board>"
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
                +"</board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected, doc;
        Board board = new Board();
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(t1,0,5);
        board.placeTile(t2,3,2);
        board.placeTile(t3,4,1);
        Token token1 = new Token(0, new int[] {0,5}, 2);
        Token token2 = new Token(1, new int[] {4,1}, 5);
        SPlayer sp1 = new SPlayer(token1, null);
        SPlayer sp2 = new SPlayer(token2, null);
        board.addSPlayer(sp1);
        board.addSPlayer(sp2);
        expected = db.parse(is);
        doc = boardParser.buildXML(board);
        assertTrue(expected.isEqualNode(doc),"Parsing board does not give the expected XML");
    }

    // Test fromXML using a board with 3 tiles and 2 tokens
    @Test
    public void fromXMLTest() throws Exception{
        String buffer = "<board>"
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
                +"</board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc = db.parse(is);
        Board board = boardParser.fromXML(doc);

        Board expected = new Board();
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        expected.placeTile(t1,0,5);
        expected.placeTile(t2,3,2);
        expected.placeTile(t3,4,1);
        Token token1 = new Token(0, new int[] {0,5}, 2);
        Token token2 = new Token(1, new int[] {4,1}, 5);
        SPlayer sp1 = new SPlayer(token1, null);
        SPlayer sp2 = new SPlayer(token2, null);
        expected.addSPlayer(sp1);
        expected.addSPlayer(sp2);

        for(int i = 0; i < board.SIZE; i++){
            for(int j = 0; j<board.SIZE; j++){
                if(board.getBoard()[i][j] != null && expected.getBoard()[i][j] != null){
                    assertTrue(board.getBoard()[i][j].isSameTile(expected.getBoard()[i][j]),
                             "Generated board is different from expected");
                }
                else if(board.getBoard()[i][j] != null || expected.getBoard()[i][j] != null){
                    assertTrue(false);
                }
            }
        }
        for(int i = 0; i < board.getSPlayerList().size(); i++) {
            assertTrue(board.getSPlayerList().get(i).getToken().isSameToken(expected.getSPlayerList().get(i).getToken()),
                    "Generated board is different from expected");
        }
    }
}
