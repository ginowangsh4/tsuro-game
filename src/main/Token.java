package tsuro;
import java.util.HashMap;
import java.util.Map;

public class Token {
    private final int color;
    private int indexOnTile;
    private int[] position;
    public final static Map<Integer, String> colorMap = new HashMap<Integer, String>() {{
            put(0, "BLUE");
            put(1, "RED");
            put(2, "GREEN");
            put(3, "ORANGE");
            put(4, "SIENNA");
            put(5, "HOTPINK");
            put(6, "DARKGREEN");
            put(7, "PURPLE");
    }};

    Token(int color, int indexOnTile, int[] position) {
        if (color < 0 || color > 7) {
            throw new IllegalArgumentException("Invalid token color");
        }
        if (!legalTokenPlacement(indexOnTile, position)) {
            throw new IllegalArgumentException("Invalid token position");
        }
        this.color = color;
        this.indexOnTile = indexOnTile;
        this.position = position;
    }

    public int getColor() {
        return this.color;
    }

    public int getIndex() {
        return indexOnTile;
    }

    public void setIndex(int indexOnTile) { this.indexOnTile = indexOnTile; }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) { this.position = position; }


    /**
     * Check whether two tokens are the same based on color
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean sameColor(Token t) {
        return this.color == t.color;
    }

    /**
     * Check whether two tokens are the same based on color
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean equals(Token t) {
        return this.color == t.color &&
                this.indexOnTile == t.indexOnTile &&
                this.position[0] == t.position[0] &&
                this.position[1] == t.position[1];
    }

    /**
     * Check if index and posn are valid inputs.
     * @return true if legal placement
     */
    public boolean legalTokenPlacement(int index, int[] position) {
        int x = position[0];
        int y = position[1];
        if (x > -1 && x < 6 && y > -1 && y < 6) {
            return true;
        }
        if (x == -1 && y > -1 && y < 6) {
            if (index == 2 || index == 3) {
                return true;
            }
        }
        if (x == 6 && y > -1 && y < 6) {
            if (index == 6 || index == 7) {
                return true;
            }
        }
        if (y == -1 && x > -1 && x < 6) {
            if (index == 4 || index == 5) {
                return true;
            }
        }
        if (y == 6 && x > -1 && x < 6) {
            if (index == 0 || index == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the token is on the edge of the board
     * @return true if on the edge; false if not
     */
    public boolean isOffBoard() {
        int ti = this.getIndex();
        int[] tp = this.getPosition();
        if ((ti == 0 || ti == 1) && tp[1] == 0 ||
                (ti == 2 || ti == 3) && tp[0] == 5 ||
                (ti == 4 || ti == 5) && tp[1] == 5 ||
                (ti == 6 || ti == 7) && tp[0] == 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the token is at a starting position at
     * the beginning of a game (ONLY FOR START OF GAME)
     * @return true if at a starting position
     */
    public boolean isStartingPosition() {
        int index = this.getIndex();
        int x = this.getPosition()[0];
        int y = this.getPosition()[1];
        if (x == -1 && y > -1 && y < 6) {
            if (index == 2 || index == 3) {
                return true;
            }
        }
        if (x == 6 && y > -1 && y < 6) {
            if (index == 6 || index == 7) {
                return true;
            }
        }
        if (y == -1 && x > -1 && x < 6) {
            if (index == 4 || index == 5) {
                return true;
            }
        }
        if (y == 6 && x > -1 && x < 6) {
            if (index == 0 || index == 1) {
                return true;
            }
        }
        return false;
    }
}
