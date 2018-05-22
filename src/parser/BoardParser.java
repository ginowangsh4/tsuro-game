package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BoardParser implements IParser<Board> {
    public DocumentBuilder db;
    public TileParser tileParser;
    public PawnParser pawnParser;

    public BoardParser(DocumentBuilder db){
        this.db = db;
        tileParser = new TileParser(db);
        pawnParser = new PawnParser(db);
    }

    public Document buildXML(Board b){
        Document doc = db.newDocument();
        Element board = doc.createElement("board");
        Element map1 = doc.createElement("map");
        Element map2 = doc.createElement("map");

        for (int i = 0; i < b.SIZE; i++) {
            for (int j = 0; j < b.SIZE; j++) {
                if (b.board[i][j] != null) {
                    Element ent = doc.createElement("ent");
                    Element xy = doc.createElement("xy");
                    Element x = doc.createElement("x");
                    Element y = doc.createElement("y");
                    xy.appendChild(x);
                    xy.appendChild(y);
                    x.appendChild(doc.createTextNode(Integer.toString(i)));
                    y.appendChild(doc.createTextNode(Integer.toString(j)));
                    ent.appendChild(xy);
                    Node d = tileParser.buildXML(b.board[i][j]).getFirstChild();

                    Node tile = doc.importNode(d, true);
                    ent.appendChild(tile);

                    map1.appendChild(ent);
                }
            }
        }

        for (Token token : b.tokenList) {
            Node t = doc.importNode(pawnParser.buildXML(token).getFirstChild(), true);
            map2.appendChild(t);
        }

        board.appendChild(map1);
        board.appendChild(map2);
        doc.appendChild(board);
        return doc;
    }

    public Board fromXML(Document doc) throws Exception {
        Board board = new Board();
        Node pawn = doc.getFirstChild();
        if(!pawn.getNodeName().equals("board")){
            throw new Exception("Trying to parse XML document that is not <ent></ent>");
        }

        Node tiles = pawn.getFirstChild();
        NodeList tileList = tiles.getChildNodes();
        for(int i = 0; i < tileList.getLength(); i++){
            Node tileEntry = tileList.item(i);
            Node xy = tileEntry.getFirstChild();
            Node tile = xy.getNextSibling();

            Document tileDoc = db.newDocument();
            Node imported = tileDoc.importNode(tile,true);
            tileDoc.appendChild(imported);
            Tile t = tileParser.fromXML(tileDoc);
            Node x = xy.getFirstChild();
            Node y = x.getNextSibling();
            int xIndex = Integer.parseInt(x.getTextContent());
            int yIndex = Integer.parseInt(y.getTextContent());
            board.placeTile(t, xIndex, yIndex);
        }

        Node pawns = tiles.getNextSibling();
        NodeList pawnList = pawns.getChildNodes();
        for(int i = 0; i < pawnList.getLength(); i++) {
            Node pawnEntry = pawnList.item(i);
            Document pawnDoc = db.newDocument();
            Node imported = pawnDoc.importNode(pawnEntry,true);
            pawnDoc.appendChild(imported);
            Token token = pawnParser.fromXML(pawnDoc, board);
            board.addToken(token);
        }

        return board;
    }

    public static void main(String[] args) throws Exception {
        // generate example board xml for testing commandline play-a-turn
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
    }
}
