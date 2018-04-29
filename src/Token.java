import java.util.HashMap;
import java.util.Map;

public class Token {
    // color of a token should be unique
    private final int color;
    private final Map<Integer, String> colorMap = new HashMap<Integer, String>() {{
            put(0, "blue");
            put(1, "red");
            put(2, "green");
            put(3, "orange");
            put(4, "sienna");
            put(5, "hotpink");
            put(6, "darkgreen");
            put(7, "pueple");
    }};
    private int indexOnTile;
    private int[] position;

    Token (int color) {
        this.color = color;
    }

    Token (int color, int indexOnTile, int[] position) {
        this.color = color;
        this.indexOnTile = indexOnTile;
        this.position = position;
    }


    public int getColor(){ return this.color; }

    public String getColorStr(){ return this.colorMap.get(this.color); }

    public void setIndex(int index) { indexOnTile = index; }

    public int getIndex() { return indexOnTile; }

    public void setPosition(int[] xy){
        position[0] = xy[0];
        position[1] = xy[1];
    }

    public int[] getPosition() { return position; }

    /**
     * Check whether two tokens are the same
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean equals(Token t) {
        if (this.color == t.color) return true;
        return false;
    }
}
