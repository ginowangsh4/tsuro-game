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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TileParserTest {
    private static TileParser parser;
    private static DocumentBuilder db;

    @BeforeAll
    public static void beforeAll() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new TileParser(db);
    }

    @Test
    public void buildXMLTest() throws Exception {
        String s = "<ent><tile>" +
                        "<connect>" +
                            "<n>0</n>" +
                            "<n>4</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>1</n>" +
                            "<n>7</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>2</n>" +
                            "<n>3</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>5</n>" +
                            "<n>6</n>" +
                        "</connect>" +
                    "</tile></ent>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document actual, expected;
        Tile t = new Tile(new int[][] {{0,4},{1,7},{2,3},{5,6}});
        expected = db.parse(is);
        actual = parser.buildXML(t);
        // System.out.println(Parser.documentToString(actual));
        assertTrue(expected.isEqualNode(actual), "Failed to build XML from tile");
    }

    @Test
    public void fromXMLTest() throws IOException, SAXException {
        String s = "<tile>" +
                        "<connect>" +
                            "<n>0</n>" +
                            "<n>4</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>1</n>" +
                            "<n>7</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>2</n>" +
                            "<n>3</n>" +
                        "</connect>" +
                        "<connect>" +
                            "<n>5</n>" +
                            "<n>6</n>" +
                        "</connect>" +
                    "</tile>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Tile expected = new Tile(new int[][] {{0,4},{1,7},{2,3},{5,6}});
        Document doc = db.parse(is);
        Tile actual = parser.fromXML(doc);
        assertTrue(expected.equals(actual), "Failed to build tile from XML");
    }
}
