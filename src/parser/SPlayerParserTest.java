package tsuro.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPlayerParserTest {
    private static SPlayerParser parser;
    private static DocumentBuilder db;

    @BeforeAll
    public static void beforeAll() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new SPlayerParser(db);
    }

    @Test
    public void buildXMLTest() throws Exception {
        int[] pos= new int[]{4, 5};
        Token token = new Token(0,1,pos);
        List<Tile> hand = new ArrayList<>();
        Tile tile = new Tile(new int[][] {{0,4},{1,7},{2,3},{5,6}});
        hand.add(tile);
        String name = "blue";
        SPlayer sp = new SPlayer(token, hand);

        String buffer = "<splayer-dragon>"
                        +"<color>blue</color>"
                        +"<set>"
                        +"<tile>"
                        +"<connect><n>0</n><n>4</n></connect>"
                        +"<connect><n>1</n><n>7</n></connect>"
                        +"<connect><n>2</n><n>3</n></connect>"
                        +"<connect><n>5</n><n>6</n></connect>"
                        +"</tile>"
                        +"</set>"
                        +"</splayer-dragon>";

        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = db.parse(is);
        Document doc = parser.buildXML(sp, true);
        assertTrue(expected.isEqualNode(doc),"Parsing splayer does not give the expected XML");
    }

    @Test
    public void fromXMLTest() throws Exception {
        String buffer = "<splayer-dragon>"
                +"<color>blue</color>"
                +"<set>"
                +"<tile>"
                +"<connect><n>0</n><n>4</n></connect>"
                +"<connect><n>1</n><n>7</n></connect>"
                +"<connect><n>2</n><n>3</n></connect>"
                +"<connect><n>5</n><n>6</n></connect>"
                +"</tile>"
                +"</set>"
                +"</splayer-dragon>";

        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc = db.parse(is);

        int[] pos= new int[]{4, 5};
        Token token = new Token(0,1,pos);
        SPlayer sp = new SPlayer(token, null);
        
        Boolean hasDragon = parser.fromXML(doc, sp);

        List<Tile> hand = new ArrayList<>();
        Tile tile = new Tile(new int[][] {{0,4},{1,7},{2,3},{5,6}});
        hand.add(tile);
        String name = "blue";
        SPlayer expected = new SPlayer(token, hand);

        assertTrue(sp.getToken().isSameToken(expected.getToken()),"Generated token is different from expected");
        assertTrue(sp.getHand().get(0).isSameTile(expected.getHand().get(0)),"Generated hand is different from expected");
        assertTrue(hasDragon,"Generated hasDragon is different from expected");
    }
}
