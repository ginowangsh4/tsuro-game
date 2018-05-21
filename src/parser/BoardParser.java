package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;

public class BoardParser {
    private DocumentBuilder db;
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

    public Board fromXML(Document doc) throws Exception{
        Board board = new Board();
        Node pawn = doc.getFirstChild();
        System.out.println(pawn.getNodeName());
        if(!pawn.getNodeName().equals("board")){
            throw new Exception("trying to parse XML document that is not <ent></ent>");
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
}
