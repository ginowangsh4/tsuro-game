package tsuro.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tsuro.*;

import javax.xml.parsers.DocumentBuilder;

public class BoardParser {
    private DocumentBuilder db;
    public TileParser tileParser;
    public PawnParser pawnParser;

    public BoardParser(DocumentBuilder db){
        this.db = db;
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

                    Node tile = doc.importNode(tileParser.buildXML(b.board[i][j]).getFirstChild(), true);
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

//    public Board fromXML(Document doc) {
//
//    }
}
