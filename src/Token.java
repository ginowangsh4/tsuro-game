import java.util.HashMap;
import java.util.Map;

public class Token {
    // color of a token should be unique
    private final int color;
    public final static Map<Integer, String> colorMap = new HashMap<Integer, String>() {{
            put(0, "blue");
            put(1, "red");
            put(2, "green");
            put(3, "orange");
            put(4, "sienna");
            put(5, "hotpink");
            put(6, "darkgreen");
            put(7, "purple");
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

    /**
     * Check whether the token is on the edge of the board
     * @return true if on the edge; false if not
     */
    public boolean isOffBoard() {
        int ti = this.getIndex();
        int[] tl = this.getPosition();
        if ((ti == 0 || ti == 1) && tl[1] == 0 ||
                (ti == 2 || ti == 3) && tl[0] == 5 ||
                (ti == 4 || ti == 5) && tl[1] == 5 ||
                (ti == 6 || ti == 7) && tl[0] == 0) {
            return true;
        }
        return false;
    }
}
