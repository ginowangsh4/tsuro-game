package tsuro.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

public class TileParser implements IParser<Tile> {
    private DocumentBuilder db;

    public TileParser(DocumentBuilder db) {
        this.db = db;
    }

    public Document buildXML(Tile t) {
        Document doc = db.newDocument();
//        Element ent = doc.createElement("ent");
        Element tile = doc.createElement("tile");
        int[][] paths = t.paths;
        for (int i = 0; i < 4; i++) {
            Element connect = doc.createElement("connect");
            Element n1 = doc.createElement("n");
            Element n2 = doc.createElement("n");
            n1.appendChild(doc.createTextNode(Integer.toString(paths[i][0])));
            n2.appendChild(doc.createTextNode(Integer.toString(paths[i][1])));
            connect.appendChild(n1);
            connect.appendChild(n2);
            tile.appendChild(connect);
        }
//        ent.appendChild(tile);
        doc.appendChild(tile);
        return doc;
    }

    public Tile fromXML(Document doc) {
//        Node ent = doc.getFirstChild();
//        if (!ent.getNodeName().equals("ent")) {
//            throw new IllegalArgumentException("Parse Error: Cannot find <ent></ent>");
//        }
        Node tile = doc.getFirstChild();
        if (!tile.getNodeName().equals("tile")) {
            throw new IllegalArgumentException("Parse Error: Cannot find <tile></tile>");
        }
        int[][] paths = new int[4][2];
        NodeList connects = doc.getElementsByTagName("connect");
        for (int i = 0; i < connects.getLength(); i++) {
            Node connect = connects.item(i);
            Node n1 = connect.getFirstChild();
            Node n2 = connect.getLastChild();
            paths[i][0] = Integer.parseInt(n1.getTextContent());
            paths[i][1] = Integer.parseInt(n2.getTextContent());
        }
        return new Tile(paths);
    }
}
