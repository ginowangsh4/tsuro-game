package tsuro.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
    public void buildPlacePawnXMLTest() {

    }

    @Test
    public void buildPlayTurnXMLTest() {

    }

    @Test
    public void buildEndGameXMLTest() {

    }

    @Test
    public void fromGetNameXMLTest() throws IOException, SAXException {
        String s = "<player-name>blue</player-name>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        Document doc = db.parse(is);
        String actual = Parser.fromGetNameXML(db, doc);
        assertEquals("blue", actual, "Failed to build string from XML");
    }

    @Test
    public void fromPlacePawnXMLTest() {

    }

    @Test
    public void fromPlayTurnXMLTest() {

    }

}
