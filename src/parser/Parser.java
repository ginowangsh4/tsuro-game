package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Parser {
    // ******************************* Network Architecture ***********************************
    //                   ----->              ---||-->        ----->
    //            Server         RemotePlayer   ||     Admin        MPlayer
    //                   <-----              <--||---        <-----
    // ****************************************************************************************

    // ****************************************************************************************
    // *********************** Build XML for Outgoing Inputs to Admin *************************
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
        BoardParser boardParser = new BoardParser(db);

        Document doc = db.newDocument();
        Element placePawn = doc.createElement("place-pawn");
        Node b = doc.importNode(boardParser.buildXML(board).getFirstChild(), true);

        placePawn.appendChild(b);
        doc.appendChild(placePawn);
        return doc;
    }

    public static Document buildPlayTurnXML(DocumentBuilder db, Board board, Set<Tile> tiles, int tilesLeft) {
        BoardParser boardParser = new BoardParser(db);
        TileParser tileParser = new TileParser(db);

        Document doc = db.newDocument();
        Element playTurn = doc.createElement("play-turn");
        Node b = doc.importNode(boardParser.buildXML(board).getFirstChild(), true);

        Element set = doc.createElement("set");
        for (Tile tile : tiles) {
            Node t = doc.importNode(tileParser.buildXML(tile).getFirstChild(), true);
            set.appendChild(t);
        }

        Element n = doc.createElement("n");
        n.appendChild(doc.createTextNode(Integer.toString(tilesLeft)));

        playTurn.appendChild(b);
        playTurn.appendChild(set);
        playTurn.appendChild(n);
        doc.appendChild(playTurn);
        return doc;
    }

    public static Document buildEndGameXML(DocumentBuilder db, Board board, Set<Integer> colors) {
        Document doc = db.newDocument();
        BoardParser boardParser = new BoardParser(db);
        Element endGame = doc.createElement("end-game");

        Node b = doc.importNode(boardParser.buildXML(board).getFirstChild(), true);

        Element cSet =  doc.createElement("set");
        for (Integer item : colors) {
            Element c = doc.createElement("color");
            c.appendChild(doc.createTextNode(Token.colorMap.get(item)));
            cSet.appendChild(c);
        }

        endGame.appendChild(b);
        endGame.appendChild(cSet);
        doc.appendChild(endGame);

        return doc;
    }

    // ****************************************************************************************
    // ******************** Build XML for Outgoing Outputs to Server **************************
    // ****************************************************************************************
    public static Document buildPlayerNameXML(DocumentBuilder db, String s) {
        Document doc = db.newDocument();
        Element playerName = doc.createElement("player-name");
        playerName.appendChild(doc.createTextNode(s));
        doc.appendChild(playerName);
        return doc;
    }

    public static Document buildVoidXML(DocumentBuilder db) {
        Document doc = db.newDocument();
        doc.appendChild(doc.createElement("void"));
        return doc;
    }

    public static Document buildPawnLocXML(DocumentBuilder db, int[] position, int indexOnTile) {
        Document doc = db.newDocument();
        PawnParser pawnParser = new PawnParser(db);
        doc.appendChild(pawnParser.buildPawnElement(doc, position, indexOnTile));
        return doc;
    }
    // ****************************************************************************************
    // **************** Decompose XML from Incoming Input from Server *************************
    // ****************************************************************************************
    public static List<Integer> fromColorListSetXML(DocumentBuilder db, Document doc) {
        Node listSet = doc.getFirstChild();
        NodeList colorsListSet = listSet.getChildNodes();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < colorsListSet.getLength(); i++) {
            Node color = colorsListSet.item(i);
            result.add(Token.getColorInt(color.getTextContent()));
        }
        return result;
    }

    public static List<Tile> fromTileSetXML(DocumentBuilder db, Document doc) {
        TileParser tileParser = new TileParser(db);
        Node list = doc.getFirstChild();
        NodeList tileSet = list.getChildNodes();
        List<Tile> result = new ArrayList<>();
        for (int i = 0; i < tileSet.getLength(); i++) {
            Node tile = tileSet.item(i);
            Document tileDoc = db.newDocument();
            Node imported = tileDoc.importNode(tile, true);
            tileDoc.appendChild(imported);
            result.add(tileParser.fromXML(tileDoc));
        }
        return result;
    }

    // ****************************************************************************************
    // **************** Decompose XML from Incoming Outputs from Admin ************************
    // ****************************************************************************************
    public static String fromGetNameXML(DocumentBuilder db, Document doc) {
        return doc.getFirstChild().getTextContent();
    }

    public static Pair<int[], Integer> fromPlacePawnXML(DocumentBuilder db, Document doc) {
        PawnParser pawnParser = new PawnParser(db);
        Node pawnLoc = doc.getFirstChild();
        if (!pawnLoc.getNodeName().equals("pawn-loc")) {
            throw new IllegalArgumentException("Parse Error: Cannot find <pawn-loc></pawn-loc>");
        }
        Node hv = pawnLoc.getFirstChild();
        String hvs = hv.getNodeName();
        boolean horizontal = hvs.equals("h");
        Node index1 = hv.getNextSibling();
        Node index2 = index1.getNextSibling();

        int[] oldPos = pawnParser.getOldPos(Integer.parseInt(index1.getTextContent()),
                                            Integer.parseInt(index2.getTextContent()),
                                            horizontal,
                                            Server.getInstance().getBoard());

        return new Pair<>(new int[] {oldPos[0], oldPos[1]}, oldPos[2]);
    }

    public static Tile fromPlayTurnXML(DocumentBuilder db, Document doc) {
        TileParser tileParser = new TileParser(db);
        return tileParser.fromXML(doc);
    }

    public static String documentToString(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public static Document stringToDocument(DocumentBuilder db, String string) throws Exception {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        return db.parse(is);
    }
}
