package tsuro.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TileParser implements IParser<Tile> {
    private DocumentBuilder db;

    public TileParser(DocumentBuilder db) {
        this.db = db;
    }

    /**
     * Convert tile game object to XML format
     * @param t the tile to be converted
     * @return a document with the XML of the tile in <tile>connect connect connect connect</tile> format as its first child
     */
    public Document buildXML(Tile t) {
        Document doc = db.newDocument();
        Element tile = doc.createElement("tile");
        int[][] paths = t.paths;
        for (int i = 0; i < 4; i++) {
            Element connect = generateConnectElement(doc, paths[i][0], paths[i][1]);
            tile.appendChild(connect);
        }
        doc.appendChild(tile);
        return doc;
    }

    public Element generateConnectElement(Document doc, int start, int end) {
        Element connect = doc.createElement("connect");
        Element n1 = doc.createElement("n");
        Element n2 = doc.createElement("n");
        n1.appendChild(doc.createTextNode(Integer.toString(start)));
        n2.appendChild(doc.createTextNode(Integer.toString(end)));
        connect.appendChild(n1);
        connect.appendChild(n2);
        return connect;
    }

    public int[] parseConnectNode(Document doc, Node connect) {
        Node n1 = connect.getFirstChild();
        Node n2 = connect.getLastChild();
        int[] path = new int[2];
        path[0] = Integer.parseInt(n1.getTextContent());
        path[1] = Integer.parseInt(n2.getTextContent());
        return path;
    }
    /**
     * Convert tile XML to tile game object
     * @param doc a document with the XML of the tile in <tile>connect connect connect connect</tile> format as its first child
     * @return a tile game object
     */
    public Tile fromXML(Document doc) {
        Node tile = doc.getFirstChild();
        if (!tile.getNodeName().equals("tile")) {
            throw new IllegalArgumentException("Parse Error: Cannot find <tile></tile>");
        }
        int[][] paths = new int[4][2];
        NodeList connects = doc.getElementsByTagName("connect");
        for (int i = 0; i < connects.getLength(); i++) {
            paths[i] = parseConnectNode(doc, connects.item(i));
        }
        return new Tile(paths);
    }

    /**
     * Generate example tile XML for testing commandline play-a-turn
     */
    public static void main(String[] args) throws Exception {
        int[] p1= new int[]{0,7};
        int[] p2= new int[]{1,2};
        int[] p3= new int[]{3,4};
        int[] p4= new int[]{5,6};
        int[][] paths = new int[][]{p1,p2,p3,p4};
        Tile t1 = new Tile(paths);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        TileParser tileParser = new TileParser(db);
        Document doc = tileParser.buildXML(t1);
        System.out.println(Parser.documentToString(doc));
    }
}
