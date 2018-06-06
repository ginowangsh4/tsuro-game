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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Parser {
    private DocumentBuilder db;
    public BoardParser boardParser;
    public TileParser tileParser;
    public PawnParser pawnParser;
    public SPlayerParser sPlayerParser;

    public Parser(DocumentBuilder db) {
        this.db = db;
        this.boardParser = new BoardParser(db);
        this.tileParser = new TileParser(db);
        this.pawnParser = new PawnParser(db);
        this.sPlayerParser = new SPlayerParser(db);
    }

    // ******************************* Network Architecture ***********************************
    //                   ----->              ---||-->        ----->
    //            Server         RemotePlayer   ||     Admin        IPlayer
    //                   <-----              <--||---        <-----
    // ****************************************************************************************

    // ****************************************************************************************
    // *********************** Build XML for Outgoing Inputs to Admin *************************
    // ****************************************************************************************

    /**
     * Get XML of get-name
     * @return a document with the XML of the get-name message in <get-name></get-name> format as its first child
     */
    public Document buildGetNameXML() {
        Document doc = db.newDocument();
        Node getName = doc.createElement("get-name");
        doc.appendChild(getName);
        return doc;
    }

    /**
     * Get XML of initialize
     * @param color color assigned to the player on the admin side
     * @param colors colors of all players in the game
     * @return a document with the XML of the initialize message in <initialize>color list-of-color</initialize>
     * format as its first child
     */
    public Document buildInitializeXML( int color, List<Integer> colors) {
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

    /**
     * Get XML of place-pawn
     * @param board current board game object
     * @return a document with the XML of the place-pawn message in <place-pawn>board</place-pawn>
     * format as its first child
     */
    public Document buildPlacePawnXML(Board board) {
        Document doc = db.newDocument();
        Element placePawn = doc.createElement("place-pawn");
        Node b = doc.importNode(boardParser.buildXML(board).getFirstChild(), true);

        placePawn.appendChild(b);
        doc.appendChild(placePawn);
        return doc;
    }

    /**
     * Get XML of play-turn
     * @param board current board game object
     * @param tiles the set of tiles that the player on admin side has
     * @param tilesLeft the number of tiles left in the deck
     * @return a document with the XML of the play-turn message in <play-turn>board set-of-tile n</play-turn>
     * format as its first child
     */
    public Document buildPlayTurnXML(Board board, Set<Tile> tiles, int tilesLeft) {
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

    /**
     * Get XML of end-game
     * @param board current board game object
     * @param colors the colors of winning players
     * @return a document with the XML of the end-game message in <end-game>board set-of-tile</end-game>
     * format as its first child
     */
    public Document buildEndGameXML(Board board, Set<Integer> colors) {
        Document doc = db.newDocument();
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

    /**
     * Get XML of tile-list
     * @param tiles a list of tiles
     * @return a document with the XML of list-of-tile in <list-of-tile>tile...</list-of-tile>
     * format as its first child
     */
    public Document buildTileListXML(List<Tile> tiles) {
        Document doc = db.newDocument();
        Element list = doc.createElement("list");
        for (Tile tile : tiles) {
            Node t = doc.importNode(tileParser.buildXML(tile).getFirstChild(), true);
            list.appendChild(t);
        }
        doc.appendChild(list);
        return doc;
    }

    // ****************************************************************************************
    // ******************** Build XML for Outgoing Outputs to Server **************************
    // ****************************************************************************************

    /**
     * Get XML of player-name
     * @param s player's name in string
     * @return a document with the XML of player-name in <player-name>str</player-name>
     * format as its first child
     */
    public Document buildPlayerNameXML(String s) {
        Document doc = db.newDocument();
        Element playerName = doc.createElement("player-name");
        playerName.appendChild(doc.createTextNode(s));
        doc.appendChild(playerName);
        return doc;
    }

    /**
     * Get XML of void
     * @return a document with the XML of void in <void></void>
     * format as its first child
     */
    public Document buildVoidXML() {
        Document doc = db.newDocument();
        doc.appendChild(doc.createElement("void"));
        return doc;
    }

    /**
     * Get XML of pawn-loc
     * @param position player's position in our board representation
     * @param indexOnTile player's index on tile
     * @return a document with the XML of player-name in <pawn-loc>hv n n</pawn-loc>
     * format as its first child
     */
    public Document buildPawnLocXML(int[] position, int indexOnTile) {
        Document doc = db.newDocument();
        doc.appendChild(pawnParser.buildPawnElement(doc, position, indexOnTile));
        return doc;
    }
    // ****************************************************************************************
    // **************** Decompose XML from Incoming Input from Server *************************
    // ****************************************************************************************

    /**
     * parse XML of list-of-color or set-of-color
     * @param doc a document with the XML of list-of-color in <list-of-color>color...</list-of-color>
     * format as its first child or with the XML of set-of-color in <set-of-color>color...</set-of-color>
     * format as its first child
     * @return list of colorIndex
     */
    public List<Integer> fromColorListSetXML(Document doc) {
        Node listSet = doc.getFirstChild();
        NodeList colorsListSet = listSet.getChildNodes();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < colorsListSet.getLength(); i++) {
            Node color = colorsListSet.item(i);
            result.add(Token.getColorInt(color.getTextContent()));
        }
        return result;
    }

    /**
     * parse XML of list-of-tile or set-of-tile
     * @param doc a document with the XML of list-of-tile in <list-of-tile>tile...</list-of-tile>
     * format as its first child or with the XML of set-of-tile in <set-of-tile>tile...</set-of-tile>
     * format as its first child
     * @return list of tiles
     */
    public List<Tile> fromTileSetXML(Document doc) {
        Node list = doc.getFirstChild();
        NodeList tileSet = list.getChildNodes();
        List<Tile> result = new ArrayList<>();
        for (int i = 0; i < tileSet.getLength(); i++) {
            Document tileDoc = fromNodeToDoc(tileSet.item(i), db);
            result.add(tileParser.fromXML(tileDoc));
        }
        return result;
    }

    // ****************************************************************************************
    // **************** Decompose XML from Incoming Outputs from Admin ************************
    // ****************************************************************************************

    /**
     * parse XML of player-name(response to GetName)
     * @param doc a document with the XML of player-name in <player-name>str</player-name>
     * format as its first child
     * @return str
     */
    public String fromGetNameXML(Document doc) {
        return doc.getFirstChild().getTextContent();
    }

    /**
     * parse XML of pawn-loc(response to PlacePawn)
     * @param doc a document with the XML of pawn-loc in <pawn-loc>hv n n</pawn-loc>
     * format as its first child
     * @return pair with position array as first and index as second
     */
    public Pair<int[], Integer> fromPlacePawnXML(Document doc) {
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
                                            Server.getInstance().board);

        return new Pair<>(new int[] {oldPos[0], oldPos[1]}, oldPos[2]);
    }

    /**
     * parse XML of tile(response to PlayTurn)
     * @param doc a document with the XML of tile in <tile><connect></connect><connect></connect><connect></connect><connect></connect></tile>
     * format as its first child
     * @return pair with position array as first and index as second
     */
    public Tile fromPlayTurnXML(Document doc) {
        return tileParser.fromXML(doc);
    }


    // ****************************************************************************************
    // ************************ Helpers used by playATurnAdapter ******************************
    // ****************************************************************************************

    public Document buildWinnersXML(Server server, List<SPlayer> winners) {
        Document winnersRes = db.newDocument();
        if (winners == null) {
            Element f = winnersRes.createElement("false");
            winnersRes.appendChild(f);
        } else {
            Element l = winnersRes.createElement("list");
            for (SPlayer sp : server.winners) {
                Document spElement = sPlayerParser.buildXML(sp, server.hasDragon(sp));
                l.appendChild(winnersRes.importNode(spElement.getFirstChild(), true));
            }
            winnersRes.appendChild(l);
        }
        return winnersRes;
    }

    public Document buildSPlayerListXML(Server server, List<SPlayer> sPlayers) {
        Document playerRes = db.newDocument();
        Node inList = playerRes.createElement("list");
        for (SPlayer sp : sPlayers) {
            Document spRes = sPlayerParser.buildXML(sp, server.hasDragon(sp));
            inList.appendChild(playerRes.importNode(spRes.getFirstChild(), true));
        }
        playerRes.appendChild(inList);
        return playerRes;
    }

    public Pair<List<SPlayer>, SPlayer> fromSPlayerListXML(String inSPlayerStr, Board board) throws Exception {
        // parse inSPlayers XML
        List<SPlayer> inSPlayer = new ArrayList<>();
        Document inPlayerDoc = stringToDocument(inSPlayerStr);
        NodeList inPlayerList = inPlayerDoc.getFirstChild().getChildNodes();
        SPlayer dragonOwner = null;
        for (int i = 0; i < inPlayerList.getLength(); i++) {
            Node inPlayerNode = inPlayerList.item(i);
            Document doc = fromNodeToDoc(inPlayerNode, db);

            SPlayer sp = board.getSPlayer(Token.getColorInt(inPlayerNode.getFirstChild().getTextContent()));

            Boolean hasDragon = sPlayerParser.fromXML(doc, sp);
            if (hasDragon) {
                dragonOwner = sp;
            }
            inSPlayer.add(sp);
        }
        return new Pair(inSPlayer, dragonOwner);
    }

    // ****************************************************************************************
    // ***************************** Other generic helpers ************************************
    // ****************************************************************************************

    public String documentToString(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public Document stringToDocument(String string) throws Exception {
        InputStream is = new ByteArrayInputStream(string.getBytes());
        return db.parse(is);
    }

    public static Document fromNodeToDoc(Node n, DocumentBuilder db) {
        Document doc = db.newDocument();
        Node imported = doc.importNode(n, true);
        doc.appendChild(imported);
        return doc;
    }
}
