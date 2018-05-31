package tsuro;
import java.util.HashMap;
import java.util.Map;

public class Token {

    private final int color;
    private int indexOnTile;
    private int[] position;

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

    public Token(int color, int indexOnTile, int[] position) {
        if (color < 0 || color > 7) {
            throw new IllegalArgumentException("Invalid token color");
        }
        this.color = color;
        this.indexOnTile = indexOnTile;
        this.position = position;
        if (!isLegalPosition()) {
            throw new IllegalArgumentException("Invalid token position");
        }
    }

    public int getColor() {
        return this.color;
    }

    public int getIndex() {
        return indexOnTile;
    }

    public int[] getPosition() {
        return position;
    }

    public void setIndex(int indexOnTile) { this.indexOnTile = indexOnTile; }

    public void setPosition(int[] position) { this.position = position; }

    /**
     * Check whether two tokens are the same based on color
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean isSameColor(Token t) {
        return color == t.color;
    }

    /**
     * Check whether two tokens are the same based on color
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean isSameToken(Token t) {
        return color == t.color && indexOnTile == t.indexOnTile &&
                position[0] == t.position[0] && position[1] == t.position[1];
    }

    /**
     * Check if a token is placed at a valid position on board
     * @return true if position is valid
     */
    public boolean isLegalPosition() {
        int x = position[0];
        int y = position[1];
        return (x > -1 && x < 6 && y > -1 && y < 6) || isStartingPosition();
    }

    /**
     * Check whether the token is at a starting position at
     * the beginning of a game
     * @return true if at a starting position
     */
    public boolean isStartingPosition() {
        int x = position[0];
        int y = position[1];
        if (x == -1 && y > -1 && y < 6) {
            return indexOnTile == 2 || indexOnTile == 3;
        }
        else if (x == 6 && y > -1 && y < 6) {
            return indexOnTile == 6 || indexOnTile == 7;
        }
        else if (y == -1 && x > -1 && x < 6) {
            return indexOnTile == 4 || indexOnTile == 5;
        }
        else if (y == 6 && x > -1 && x < 6) {
            return indexOnTile == 0 || indexOnTile == 1;
        }
        return false;
    }

    /**
     * Check whether the token is on the edge of the board
     * @return true if on the edge; false if not
     */
    public boolean isOffBoard() {
        return (indexOnTile == 0 || indexOnTile == 1) && position[1] == 0 ||
                (indexOnTile == 2 || indexOnTile == 3) && position[0] == 5 ||
                (indexOnTile == 4 || indexOnTile == 5) && position[1] == 5 ||
                (indexOnTile == 6 || indexOnTile == 7) && position[0] == 0;
    }

    /**
     * find color string associated with color index
     * @return color in string format
     */
    public String getColorString() {
        if (color < 0 || color > 7) {
            throw new IllegalArgumentException("Invalid token color index");
        }
        return colorMap.get(color);
    }

    /**
     * Get the corresponding color integer given a color name
     * @param colorName color name string
     * @return color integer
     */
    public static int getColorInt(String colorName) {
        for(Integer i : colorMap.keySet()){
            if (colorMap.get(i).equals(colorName)){
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid color name");
    }
}
