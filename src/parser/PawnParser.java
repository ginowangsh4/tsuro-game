package tsuro.parser;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tsuro.*;

public class PawnParser {
    private DocumentBuilder db;
    public PawnParser(DocumentBuilder db){
        this.db = db;
    }

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

    public Token fromXML(Document doc) throws Exception{
        Node pawn = doc.getFirstChild();
        System.out.println(pawn.getNodeName());
        if(!pawn.getNodeName().equals("ent")){
            throw new Exception("trying to parse XML document that is not <ent></ent>");
        }

        Node color = pawn.getFirstChild();
        String colorName = color.getTextContent();

        Node pawnLoc = color.getNextSibling();
        Boolean horizontal = false;
        Node orientation = pawnLoc.getFirstChild();
        if(pawn.getNodeName().equals("h")) {
            horizontal = true;
        }
        Node n1 = orientation.getNextSibling();
        Node n2 = n1.getNextSibling();
        int index1 = Integer.parseInt(n1.getTextContent());
        int index2 = Integer.parseInt(n2.getTextContent());

        int colorIndex = getColorInt(colorName);

        int[] oldPos = getOldPos(index1, index2, horizontal);
        int index = oldPos[2];
        int[] pos= new int[]{oldPos[0], oldPos[1]};
        Token token = new Token(colorIndex, index, pos);

        return token;
    }

    public Element buildPawnElement(Document doc, int[] pos, int index){
        Element pawnLoc = doc.createElement("pawn-loc");

        Boolean horizontal = isHorizontal(index);
        int[] newPos = getNewPos(pos, index);
        Element n1 = doc.createElement("n");
        n1.appendChild(doc.createTextNode(Integer.toString(newPos[0])));
        Element n2 = doc.createElement("n");
        n2.appendChild(doc.createTextNode(Integer.toString(newPos[1])));


        if(horizontal){
            Element h = doc.createElement("h");
            pawnLoc.appendChild(h);
        }
        else{
            Element v = doc.createElement("v");
            pawnLoc.appendChild(v);
        }
        pawnLoc.appendChild(n1);
        pawnLoc.appendChild(n2);
        return pawnLoc;
    }

    public Boolean isHorizontal(int index){
        if(index > 7 || index < 0){
            throw new IllegalArgumentException("index is out of bound");
        }
        if(index == 0 || index == 1 || index == 4 || index ==5)
        {
            return true;
        }
        return false;
    }

    public int[] getNewPos(int[]pos, int index){
        if(index < 0 || index > 7){
            throw new IllegalArgumentException("index is out of bound");
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
                throw new IllegalArgumentException("index should not be horizontal");
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
                throw new IllegalArgumentException("index should not be vertical");
            }
        }
        return newPos;
    }

    public int[] getOldPos(int index1, int index2, Boolean horizontal){
        int[] oldPos = new int[3];
        if(horizontal){
            if(index1 == 0){
                oldPos[1] = -1;
                int temp = index2 % 2;
                if(temp == 1) oldPos[2] = 4;
                else oldPos[2] = 5;
            }
            else if(index1 == 6){
                oldPos[1] = 6;
                oldPos[2] = index2 % 2;
            }
            else{
                throw new IllegalArgumentException("this returned position is not a starting position");
            }
            oldPos[0] = index2 / 2;
        }
        else{
            if(index1 == 0){
                oldPos[0]=-1;
                oldPos[2] = index2 % 2 + 2;
            }
            else if(index1 == 6){
                oldPos[0]=6;
                int temp = index2 % 2;
                if(temp == 1) oldPos[2] = 6;
                else oldPos[2] = 7;
            }
            else{
                throw new IllegalArgumentException("this returned position is not a starting position");
            }
            oldPos[1] = index2 / 2;
        }
        return oldPos;
    }

    public int getColorInt(String colorName){
        for(Integer key : Token.colorMap.keySet()){
            if (Token.colorMap.get(key).equals(colorName)){
                return key;
            }
        }
        throw new IllegalArgumentException("Invalid color name");
    }


}
