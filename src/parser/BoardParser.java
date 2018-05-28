package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BoardParser implements IParser<Board> {
    public DocumentBuilder db;
    public TileParser tileParser;
    public PawnParser pawnParser;

    public BoardParser(DocumentBuilder db){
        this.db = db;
        tileParser = new TileParser(db);
        pawnParser = new PawnParser(db);
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
                if (b.board[i][j] != null) {
                    Node d = tileParser.buildXML(b.board[i][j]).getFirstChild();
                    Node tile = doc.importNode(d, true);

                    Element ent = generateTileEntry(doc,i,j,tile);
                    map1.appendChild(ent);
                }
            }
        }

        // Generate pawns XML
        for (Token token : b.tokenList) {
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
        Document tileDoc = Parser.fromNodeToDoc(db, tile);
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
            Document pawnDoc = Parser.fromNodeToDoc(db, pawnList.item(i));
            Token token = pawnParser.fromXML(pawnDoc, board);
            board.addToken(token);
        }

        return board;
    }

    /**
     * Generate example board XML for testing commandline play-a-turn
     */
    public static void main(String[] args) throws Exception {
        Board board = new Board();
        Tile t1 = new Tile(new int[][] {{0,5},{1,2},{3,4},{6,7}});
        Tile t2 = new Tile(new int[][] {{0,3},{1,4},{2,7},{5,6}});
        Tile t3 = new Tile(new int[][] {{0,5},{1,4},{2,7},{3,6}});

        board.placeTile(t1, 1, 0);
        board.placeTile(t2, 1, 5);
        board.placeTile(t3, 5, 2);

        int[] pos1 = new int[]{1, 0};
        Token token1 = new Token(1,2,pos1);
        int[] pos2 = new int[]{1, 5};
        Token token2 = new Token(2,0,pos2);
        int[] pos3 = new int[]{5, 2};
        Token token3 = new Token(3,6,pos3);

        board.addToken(token1);
        board.addToken(token2);
        board.addToken(token3);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        BoardParser boardParser = new BoardParser(db);
        Document doc = boardParser.buildXML(board);
        System.out.println(Parser.documentToString(doc));

        String s = "<board><map><ent><xy><x>3</x><y>3</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>2</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>1</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>5</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>4</x><y>0</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>4</x><y>4</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>3</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>3</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>5</x><y>1</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>4</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>5</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>1</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>3</x><y>5</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>4</n></connect></tile></ent><ent><xy><x>1</x><y>4</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>3</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>4</n></connect><connect><n>3</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>2</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>1</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>5</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>3</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>0</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>4</n></connect></tile></ent><ent><xy><x>2</x><y>4</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>3</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>1</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>3</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>0</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>4</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>3</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>4</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>3</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>2</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>1</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>5</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>4</n></connect></tile></ent><ent><xy><x>0</x><y>4</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>0</x><y>0</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>5</x><y>2</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>0</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>2</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>3</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent></map><map><ent><color>orange</color><pawn-loc><v></v><n>0</n><n>6</n></pawn-loc></ent><ent><color>red</color><pawn-loc><h></h><n>0</n><n>11</n></pawn-loc></ent><ent><color>darkgreen</color><pawn-loc><h></h><n>0</n><n>5</n></pawn-loc></ent><ent><color>sienna</color><pawn-loc><h></h><n>5</n><n>2</n></pawn-loc></ent><ent><color>blue</color><pawn-loc><v></v><n>6</n><n>11</n></pawn-loc></ent><ent><color>hotpink</color><pawn-loc><h></h><n>6</n><n>6</n></pawn-loc></ent><ent><color>green</color><pawn-loc><v></v><n>2</n><n>10</n></pawn-loc></ent></map></board>";
        InputStream is = new ByteArrayInputStream(s.getBytes());
        doc = db.parse(is);
        board = boardParser.fromXML(doc);
    }
}
