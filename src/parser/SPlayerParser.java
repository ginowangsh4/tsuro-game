package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class SPlayerParser {
    private DocumentBuilder db;
    public TileParser tileParser;
    public SPlayerParser(DocumentBuilder db){
        this.db = db;
        tileParser = new TileParser(db);
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
     * @param token contains location of the splayer
     * @return
     */
    public Pair<SPlayer,Boolean> fromXML(Document doc, Token token) throws Exception {
        Node sPlayer = doc.getFirstChild();
        Boolean hasDragon;
        if (sPlayer.getNodeName().equals("splayer-dragon")) {
            hasDragon = true;
        } else if(sPlayer.getNodeName().equals("splayer-nodragon")){
            hasDragon = false;
        } else{
            throw new Exception("Trying to parse XML document that is not <ent></ent>");
        }

        Node color = sPlayer.getFirstChild();
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

    /**
     * Generate example splayer XML for testing commandline play-a-turn
     */
    public static void main(String[] args) throws Exception {
        int[] pos1 = new int[]{1, 0};
        Token token1 = new Token(1,2,pos1);
        List<Tile> hand1 = new ArrayList<>();
        SPlayer sp1 = new SPlayer(token1, hand1, "red");

        int[] pos2 = new int[]{1, 5};
        Token token2 = new Token(2,0,pos2);
        List<Tile> hand2 = new ArrayList<>();
        SPlayer sp2 = new SPlayer(token2, hand2, "green");

        int[] pos3 = new int[]{1, 0};
        Token token3 = new Token(3,2,pos3);
        List<Tile> hand3 = new ArrayList<>();
        SPlayer sp3 = new SPlayer(token3, hand3, "orange");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        SPlayerParser splayerParser = new SPlayerParser(db);
        Document doc1 = splayerParser.buildXML(sp1,false);
        System.out.println(Parser.documentToString(doc1));
        Document doc2 = splayerParser.buildXML(sp2,false);
        System.out.println(Parser.documentToString(doc2));
        Document doc3 = splayerParser.buildXML(sp3,false);
        System.out.println(Parser.documentToString(doc3));
    }
}
