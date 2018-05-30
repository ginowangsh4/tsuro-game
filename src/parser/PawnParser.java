package tsuro.parser;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tsuro.Board;
import tsuro.Tile;
import tsuro.Token;

public class PawnParser {
    private DocumentBuilder db;

    public PawnParser(DocumentBuilder db){
        this.db = db;
    }

    /**
     * Convert token/pawn game object to XML format
     * @param token the token to be converted
     * @return a document with the XML of the token/pawn in <ent>color pawn-loc</ent> format as its first child
     */
    public Document buildXML(Token token){
        Document doc = db.newDocument();
        Element pawn = doc.createElement("ent");

        Element color = doc.createElement("color");
        color.appendChild(doc.createTextNode(token.getColorString()));

        Element pawnLoc = buildPawnElement(doc, token.getPosition(),token.getIndex());

        pawn.appendChild(color);
        pawn.appendChild(pawnLoc);
        doc.appendChild(pawn);
        return doc;
    }

    /**
     * Convert pawn XML to token game object
     * @param doc a document with the XML of the token/pawn in <ent>color pawn-loc</ent> format as its first child
     * @param board a board with tiles placed on it
     * @return token game object
     */
    public Token fromXML(Document doc, Board board) throws Exception{
        Node pawn = doc.getFirstChild();
        if(!pawn.getNodeName().equals("ent")){
            throw new Exception("Trying to parse XML document that is not <ent></ent>");
        }

        Node color = pawn.getFirstChild();
        String colorName = color.getTextContent();
        int colorIndex = Token.getColorInt(colorName);

        Node pawnLoc = color.getNextSibling();
        int[] oldPos = parsePawnLocXML(board, pawnLoc);
        Token token = new Token(colorIndex, oldPos[2], new int[]{oldPos[0], oldPos[1]});

        return token;

    }

    public static int[] parsePawnLocXML(Board board, Node pawnLoc) {
        Boolean horizontal = false;
        Node orientation = pawnLoc.getFirstChild();
        if(orientation.getNodeName().equals("h")) {
            horizontal = true;
        }
        Node n1 = orientation.getNextSibling();
        Node n2 = n1.getNextSibling();
        int index1 = Integer.parseInt(n1.getTextContent());
        int index2 = Integer.parseInt(n2.getTextContent());
        int[] oldPos = getOldPos(index1, index2, horizontal, board);
        return oldPos;
    }


    /**
     * Generate pawn-loc element using doc, pos array and index
     * @param doc a document
     * @param pos token's location on board
     * @param index token's index on tile
     * @return an Element of pawn-loc in <pawn-loc>color pawn-loc</pawn-loc> format
     */
    public Element buildPawnElement(Document doc, int[] pos, int index){
        Element pawnLoc = doc.createElement("pawn-loc");

        Boolean horizontal = isHorizontal(index);
        Element orientation = getOrientationElement(doc, horizontal);

        int[] newPos = getNewPos(pos, index);
        Element n1 = doc.createElement("n");
        n1.appendChild(doc.createTextNode(Integer.toString(newPos[0])));
        Element n2 = doc.createElement("n");
        n2.appendChild(doc.createTextNode(Integer.toString(newPos[1])));

        pawnLoc.appendChild(orientation);
        pawnLoc.appendChild(n1);
        pawnLoc.appendChild(n2);
        return pawnLoc;
    }

    public static Element getOrientationElement(Document doc, Boolean horizontal) {
        if(horizontal) return doc.createElement("h");
        else return doc.createElement("v");
    }

    /**
     * Check whether pawn is on horizontal line or vertical line
     * @param index token's index on tile
     * @return true if pawn is on horizontal line
     */
    public Boolean isHorizontal(int index){
        if(index > 7 || index < 0){
            throw new IllegalArgumentException("Index is out of bound");
        }
        if(index == 0 || index == 1 || index == 4 || index ==5)
        {
            return true;
        }
        return false;
    }

    /**
     * Mapping for specs' representation
     * index      n2      h/v      n1
     * -------------------------------
     *  0        0+2x      h        y
     *  1        1+2x      h        y
     * -------------------------------
     *  4        1+2x      h       y+1
     *  5        0+2x      h       y+1
     * -------------------------------
     *  2        0+2y      v       x+1
     *  3        1+2y      v       x+1
     * -------------------------------
     *  6        1+2y      v        x
     *  7        0+2y      v        x
     * @param pos token's position on board
     * @param index token's index on tile
     * @return int[2] that represents pawn's location on specs' representation of board
     */
    public int[] getNewPos(int[]pos, int index){
        if(index < 0 || index > 7){
            throw new IllegalArgumentException("Index is out of bound");
        }
        int[] newPos = new int[2];

        if(index == 0 || index == 1){
            newPos[0] = pos[1];
        }
        else if(index == 2 || index == 3){
            newPos[0] = pos[0]+1;
        }
        else if(index == 4 || index == 5){
            newPos[0] = pos[1]+1;
        }
        else{
            newPos[0] = pos[0];
        }

        if(isHorizontal(index)){
            if(index == 0 || index == 5){
                newPos[1] = 2 * pos[0];
            }
            else if(index == 1 || index == 4){
                newPos[1] = 2 * pos[0] + 1;
            }
            else{
                throw new IllegalArgumentException("Index should not be horizontal");
            }
        }
        else{
            if(index == 2 || index == 7){
                newPos[1] = 2 * pos[1];
            }
            else if(index == 3 || index == 6){
                newPos[1] = 2 * pos[1] + 1;
            }
            else{
                throw new IllegalArgumentException("Index should not be vertical");
            }
        }
        return newPos;
    }

    /**
     * @param index1 line number
     * @param index2 the index on line
     * @param horizontal whether the pawn is at a horizontal line or vertical line
     * @param b board with tiles placed on it
     * @return {x, y, indexOnTile} as how tokens are represented in the game
     */
    public static int[] getOldPos(int index1, int index2, Boolean horizontal, Board b){
        int[] oldPos = new int[3];
        if(horizontal) {
            int downIndex = index2 % 2;
            int upIndex = Tile.neighborIndex.get(downIndex);
            int upRow = index1 - 1;
            int downRow = index1;
            int column = index2 / 2;
            int[] upPos = new int[]{column,upRow};
            int[] downPos = new int[]{column,downRow};
            if (b.isOffBoard(downPos)){
                // if tile location below this pos is out of bound
                if (b.board[upPos[0]][upPos[1]] != null){
                    // pawn at upTilePos
                    oldPos[1] = upRow;
                    oldPos[2] = upIndex;
                }
                else {
                    // pawn at starting position aka downTilePos
                    oldPos[1] = downRow;
                    oldPos[2] = downIndex;
                }
            }
            else {
                if (b.board[downPos[0]][downPos[1]] == null){
                    oldPos[1] = upRow;
                    oldPos[2] = upIndex;
                }
                else {
                    oldPos[1] = downRow;
                    oldPos[2] = downIndex;
                }
            }
            oldPos[0] = column;
        }
        else {
            int leftIndex = index2 % 2 + 2;
            int rightIndex = Tile.neighborIndex.get(leftIndex);
            int leftColumn = index1 - 1;
            int rightColumn = index1;
            int row = index2 / 2;
            int[] leftPos = new int[]{leftColumn,row};
            int[] rightPos = new int[]{rightColumn,row};

            if (b.isOffBoard(rightPos)){
                if (b.board[leftPos[0]][leftPos[1]] != null){
                    oldPos[0] = leftColumn;
                    oldPos[2] = leftIndex;
                }
                else {
                    oldPos[0] = rightColumn;
                    oldPos[2] = rightIndex;
                }
            }
            else {
                if (b.board[rightPos[0]][rightPos[1]] == null){
                    oldPos[0] = leftColumn;
                    oldPos[2] = leftIndex;
                }
                else {
                    oldPos[0] = rightColumn;
                    oldPos[2] = rightIndex;
                }
            }
            oldPos[1] = row;
        }
        return oldPos;
    }
}
