package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;

public class BoardParser implements IParser<Board> {
    private DocumentBuilder db;
    private TileParser tileParser;
    private PawnParser pawnParser;

    public BoardParser(DocumentBuilder db){
        this.db = db;
        this.tileParser = new TileParser(db);
        this.pawnParser = new PawnParser(db);
    }

    /**
     * Convert board game object to XML format
     * @param b the board to be converted
     * @return a document with the XML of the board in <board>tiles pawns</board> format as its first child
     */
    public Document buildXML(Board b){
        Document doc = db.newDocument();
        Element board = doc.createElement("board");
        Element map1 = doc.createElement("map");
        Element map2 = doc.createElement("map");

        // Generate list-of-tile XML
        for (int i = 0; i < b.SIZE; i++) {
            for (int j = 0; j < b.SIZE; j++) {
                if (b.getBoard()[i][j] != null) {
                    Node d = tileParser.buildXML(b.getBoard()[i][j]).getFirstChild();
                    Node tile = doc.importNode(d, true);

                    Element ent = generateTileEntry(doc,i,j,tile);
                    map1.appendChild(ent);
                }
            }
        }

        // Generate pawns XML
        for (SPlayer sp : b.getSPlayerList()) {
            Token token = sp.getToken();
            Node t = doc.importNode(pawnParser.buildXML(token).getFirstChild(), true);
            map2.appendChild(t);
        }

        board.appendChild(map1);
        board.appendChild(map2);
        doc.appendChild(board);
        return doc;
    }

    public Element generateTileEntry(Document doc, int i, int j, Node tile){
        Element ent = doc.createElement("ent");
        Element xy = doc.createElement("xy");
        Element x = doc.createElement("x");
        Element y = doc.createElement("y");
        xy.appendChild(x);
        xy.appendChild(y);
        x.appendChild(doc.createTextNode(Integer.toString(i)));
        y.appendChild(doc.createTextNode(Integer.toString(j)));
        ent.appendChild(xy);

        ent.appendChild(tile);

        return ent;
    }

    public Pair<Tile, int[]> parseTileEntryXML(Document doc, Node tileEntry) {
        Node xy = tileEntry.getFirstChild();
        Node tile = xy.getNextSibling();
        Document tileDoc = Parser.fromNodeToDoc(tile, db);
        Tile t = tileParser.fromXML(tileDoc);

        Node x = xy.getFirstChild();
        Node y = x.getNextSibling();
        int xIndex = Integer.parseInt(x.getTextContent());
        int yIndex = Integer.parseInt(y.getTextContent());
        int[] pos = new int[]{xIndex, yIndex};

        return new Pair(t,pos);
    }
    /**
     * Convert board XML to board game object
     * @param doc a document with the XML of the board in <board>tiles pawns</board> format as its first child
     * @return board game object
     */
    public Board fromXML(Document doc) throws Exception {
        Board board = new Board();
        Node pawn = doc.getFirstChild();
        if(!pawn.getNodeName().equals("board")){
            throw new Exception("Trying to parse XML document that is not <board></board>");
        }

        // Parse each tile XML into tile game object and place tile object on board
        Node tiles = pawn.getFirstChild();
        NodeList tileList = tiles.getChildNodes();
        for(int i = 0; i < tileList.getLength(); i++){
            Pair<Tile, int[]> res = parseTileEntryXML(doc, tileList.item(i));
            board.placeTile(res.first, res.second[0], res.second[1]);
        }

        // Parse each pawn XML into token game object given board (with placed tile)
        // and place token object on board
        Node pawns = tiles.getNextSibling();
        NodeList pawnList = pawns.getChildNodes();
        for(int i = 0; i < pawnList.getLength(); i++) {
            Document pawnDoc = Parser.fromNodeToDoc(pawnList.item(i), db);
            Token token = pawnParser.fromXML(pawnDoc, board);
            SPlayer sp = new SPlayer(token, null);
            board.addSPlayer(sp);
        }
        return board;
    }
}
