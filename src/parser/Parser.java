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
    // ******************************* Network Architecture ***********************************
    //                   ----->              ---||-->        ----->
    //              Admin        RemotePlayer   ||     NAdmin        MPlayer
    //                   <-----              <--||---        <-----
    // ****************************************************************************************

    // ****************************************************************************************
    // *********************** Build XML for Outgoing Inputs to NAdmin ************************
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
    // ******************** Build XML for Outgoing Outputs to Admin ***************************
    // ****************************************************************************************
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
    // **************** Decompose XML from Incoming Input from Admin **************************
    // ****************************************************************************************


    // ****************************************************************************************
    // **************** Decompose XML from Incoming Outputs from NAdmin ***********************
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

        int[] oldPos = pawnParser.getOldPos(Integer.parseInt(index1.getNodeName()),
                                            Integer.parseInt(index2.getNodeName()),
                                            horizontal,
                                            Server.getInstance().getBoard());

        return new Pair<>(new int[] {oldPos[0], oldPos[1]}, oldPos[2]);
    }

    public static Tile fromPlayTurnXML(DocumentBuilder db, Document doc) {
        TileParser tileParser = new TileParser(db);
        return tileParser.fromXML(doc);
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
