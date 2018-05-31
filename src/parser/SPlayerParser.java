package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.List;

public class SPlayerParser {
    private DocumentBuilder db;
    private TileParser tileParser;

    public SPlayerParser(DocumentBuilder db){
        this.db = db;
        this.tileParser = new TileParser(db);
    }

    /**
     * Convert splayer game object to XML format
     * @param sp
     * @param hasDragon
     * @return a document with the XML of the splayer in <ent>color pawn-loc</ent> format as its first child
     */
    public Document buildXML(SPlayer sp, Boolean hasDragon){
        Document doc = db.newDocument();
        Element sPlayer = doc.createElement("splayer-nodragon");
        if(hasDragon) sPlayer = doc.createElement("splayer-dragon");


        Element color = doc.createElement("color");
        int colorIndex = sp.getToken().getColor();
        color.appendChild(doc.createTextNode(Token.colorMap.get(colorIndex)));


        Element set = doc.createElement("set");
        for (Tile tile : sp.getHand()) {
            Node t = doc.importNode(tileParser.buildXML(tile).getFirstChild(), true);
            set.appendChild(t);
        }

        sPlayer.appendChild(color);
        sPlayer.appendChild(set);
        doc.appendChild(sPlayer);
        return doc;
    }


    /**
     * Convert XML format to splayer game object
     * @param doc with the XML of the splayer in <ent>color pawn-loc</ent> format as its first child
     * @param sp contains token but with empty hand
     * @return true if this splayer is a dragon holder
     */
    public Boolean fromXML(Document doc, SPlayer sp) throws Exception {
        Node sPlayer = doc.getFirstChild();
        Boolean hasDragon;
        if (sPlayer.getNodeName().equals("splayer-dragon")) {
            hasDragon = true;
        } else if (sPlayer.getNodeName().equals("splayer-nodragon")){
            hasDragon = false;
        } else {
            throw new Exception("Trying to parse XML document that is not <ent></ent>");
        }

        Node color = sPlayer.getFirstChild();
        int colorIndex = Token.getColorInt(color.getTextContent());
        if(colorIndex != sp.getToken().getColor()){
            throw new Exception("XML color and token color mismatch");
        }

        Node setTiles = color.getNextSibling();
        List<Tile> hand = new ArrayList<>();
        NodeList tileList = setTiles.getChildNodes();
        for(int i = 0; i < tileList.getLength(); i++){
            Document tileDoc = Parser.fromNodeToDoc(tileList.item(i), db);
            Tile tile = tileParser.fromXML(tileDoc);
            hand.add(tile);
        }

        sp.setHand(hand);
        return hasDragon;
    }
}
