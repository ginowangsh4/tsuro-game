package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.Pair;
import tsuro.SPlayer;
import tsuro.Tile;
import tsuro.Token;

import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.List;

public class SPlayerParser {
    private DocumentBuilder db;
    public TileParser tileParser;
    public SPlayerParser(DocumentBuilder db){
        this.db = db;
        tileParser = new TileParser(db);
    }

    public Document buildXML(SPlayer sp, Boolean hasDragon){
        Document doc = db.newDocument();
        Element splayer = doc.createElement("splayer-nodragon");
        if(hasDragon) splayer = doc.createElement("splayer-dragon");


        Element color = doc.createElement("color");
        int colorIndex = sp.getToken().getColor();
        color.appendChild(doc.createTextNode(Token.colorMap.get(colorIndex)));


        Element set = doc.createElement("set");
        for (Tile tile : sp.getHand()) {
            Node t = doc.importNode(tileParser.buildXML(tile).getFirstChild(), true);
            set.appendChild(t);
        }

        splayer.appendChild(color);
        splayer.appendChild(set);
        doc.appendChild(splayer);
        return doc;
    }

    public Pair<SPlayer,Boolean> fromXML(Document doc, Token token) throws Exception {
        Node splayer = doc.getFirstChild();
        Boolean hasDragon = false;
        if (splayer.getNodeName().equals("splayer-dragon")) {
            hasDragon = true;
        } else if(splayer.getNodeName().equals("splayer-nodragon")){
            hasDragon = false;
        } else{
            throw new Exception("Trying to parse XML document that is not <ent></ent>");
        }

        Node color = splayer.getFirstChild();
        int colorIndex = Token.getColorInt(color.getTextContent());
        if(colorIndex != token.getColor()){
            throw new Exception("XML color and token color mismatch");
        }

        Node setTiles = color.getNextSibling();

        List<Tile> hand = new ArrayList<>();
        NodeList tileList = setTiles.getChildNodes();
        for(int i = 0; i < tileList.getLength(); i++){
            Node tileEntry = tileList.item(i);
            Document tileDoc = db.newDocument();
            Node imported = tileDoc.importNode(tileEntry, true);
            tileDoc.appendChild(imported);
            Tile tile = tileParser.fromXML(tileDoc);
            hand.add(tile);
        }

        SPlayer sp = new SPlayer(token, hand, Token.colorMap.get(colorIndex));
        return new Pair<>(sp, hasDragon);
    }
}
