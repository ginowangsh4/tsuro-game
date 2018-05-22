package tsuro.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import tsuro.Board;
import tsuro.Tile;
import tsuro.Token;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PawnParserTest {
    private static PawnParser parser;
    private static DocumentBuilder db;

    @BeforeAll
    public static void beforeAll() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new PawnParser(db);
    }

    // Test buildXML using a token with color 1 and location 4,5,1
    @Test
    public void buildXMLTest() throws Exception {
        String buffer = "<ent><color>red</color><pawn-loc><h></h><n>5</n><n>9</n></pawn-loc></ent>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected, doc;
        int[] pos= new int[]{4, 5};
        Token token = new Token(1,1,pos);
        expected = db.parse(is);
        doc = parser.buildXML(token);
        assertTrue(expected.isEqualNode(doc),"Parsing token does not give the expected XML");
    }

    // Test fromXML using a pawn with color red and location v,0,4
    @Test
    public void fromXMLTest() {
        String buffer = "<ent><color>red</color><pawn-loc><v></v><n>0</n><n>4</n></pawn-loc></ent>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Token token = null;
        try {
            doc = db.parse(is);
            token = parser.fromXML(doc, new Board());
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Token expected = new Token(1,2,new int[]{-1,2});
        assertTrue(token.equals(expected),"Generated token is different from expected");
    }

    // Test isHorizontal(int index) using 2 horizontal lines and 2 vertical lines
    @Test
    public void isHorizontalTest() {
        assertTrue(parser.isHorizontal(0));
        assertTrue(parser.isHorizontal(5));
        assertFalse(parser.isHorizontal(2));
        assertFalse(parser.isHorizontal(7));
    }

    // Test getNewPos using 4 board locations in our representation
    @Test
    public void getNewPosTest() {
        int[] pos = new int[]{-1,2};
        int[] expected = new int[]{0,4};
        int[] result = parser.getNewPos(pos,2);
        assertArrayEquals(result,expected);

        pos = new int[]{6,4};
        expected = new int[]{6,8};
        result = parser.getNewPos(pos,7);
        assertArrayEquals(result,expected);

        pos = new int[]{2,-1};
        expected = new int[]{0,5};
        result = parser.getNewPos(pos,4);
        assertArrayEquals(result,expected);

        pos = new int[]{2,6};
        expected = new int[]{6,4};
        result = parser.getNewPos(pos,0);
        assertArrayEquals(result,expected);
    }

    // Test getOldPos using 4 locations in specs' representation of board and the board
    @Test
    public void getOldPosTest() {
        Board board =  new Board();
        int[] expected = new int[]{-1,2,2};
        int[] actual = parser.getOldPos(0,4,false, board);
        assertArrayEquals(actual,expected,"Computed game position is not correct");

        expected = new int[]{6,4,7};
        actual = parser.getOldPos(6,8,false, board);
        assertArrayEquals(actual,expected,"Computed game position is not correct");

        expected = new int[]{2,-1,4};
        actual = parser.getOldPos(0,5,true, board);
        assertArrayEquals(actual,expected,"Computed game position is not correct");

        expected = new int[]{2,6,0};
        actual = parser.getOldPos(6,4,true, board);
        assertArrayEquals(actual,expected,"Computed game position is not correct");

        Tile tile = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board.placeTile(tile, 0, 0);
        expected = new int[]{0,0,4};
        actual = parser.getOldPos(1,1,true, board);
        assertArrayEquals(actual,expected,"Computed game position is not correct");
    }
}