package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tsuro.*;

import java.io.StringWriter;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Parser {

    // ****************************************************************************************
    // ****************************** Build XML functions *************************************
    // ****************************************************************************************
    public static Document buildGetNameXML(DocumentBuilder db) {
        Document doc = db.newDocument();
        Node getName = doc.createElement("get-name");
        doc.appendChild(getName);
        return doc;
    }

    public static Document buildInitializeXML(DocumentBuilder db, int color, List<Integer> colors) {
        Document doc = db.newDocument();
        Element initialize = doc.createElement("initialize");

        Element ac = doc.createElement("color");
        ac.appendChild(doc.createTextNode(Token.colorMap.get(color)));

        Element cList =  doc.createElement("list");
        for (Integer item : colors) {
            Element c = doc.createElement("color");
            c.appendChild(doc.createTextNode(Token.colorMap.get(item)));
            cList.appendChild(c);
        }

        initialize.appendChild(ac);
        initialize.appendChild(cList);
        doc.appendChild(initialize);

        return doc;
    }

    public static Document buildPlacePawnXML(DocumentBuilder db, Board board) {
        return db.newDocument();
    }

    public static Document buildPlayTurnXML(DocumentBuilder db, Board board, Set<Tile> tiles, int tilesLeft) {
        return db.newDocument();
    }

    public static Document buildEndGameXML(DocumentBuilder db, Board board, Set<Integer> colors) {
        return db.newDocument();
    }

    // ****************************************************************************************
    // **************************** Decompose XML functions ***********************************
    // ****************************************************************************************
    public static String fromGetNameXML(DocumentBuilder db, Document doc) {
        return doc.getFirstChild().getTextContent();
    }

    public static Pair<int[], Integer> fromPlacePawnXML(DocumentBuilder db, Document doc) {
        return new Pair<>(new int[] {0,0}, 1);
    }

    public static Tile fromPlayTurnXML(DocumentBuilder db, Document doc) {
        return new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
    }

    public static String documentToString(Document doc) throws Exception {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }
}
