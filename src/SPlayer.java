import java.util.*;

// Not complete
// More test cases need to be build to confirm that all things work as expected
public class SPlayer {
    private Token token;
    private List<Tile> hand;
    private String name;

    SPlayer(Token t, List<Tile> hand, String name) {
        this.token = t;
        this.hand = hand;
        this.name = name;
    }

    /**
     * Check if a player has this input tile on hand
     * @param tile to be checked
     * @return true if play has this tile
     */
    public boolean hasTile(Tile tile) {
        for (Tile t : getHand()) {
            if (t.isSameTile(tile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a player's token
     *
     * @return a token
     */
    public Token getToken() {
        return this.token;
    }

    /**
     * Update a player's token
     *
     * @param token new token
     */
    public void updateToken(Token token) {
        this.token = token;
    }

    /**
     * SPlayer draws a tile
     *
     * @param t tile to be added to the player's hand
     */
    public void draw(Tile t) {
        hand.add(t);
    }

    /**
     * Simulate player choosing a tile to place
     *
     * @param t tile to be placed
     */
    public void deal(Tile t) {
        hand.remove(t);
    }

    /**
     * Get a player's hand
     *
     * @return a list of tiles on player's hand
     */
    public List<Tile> getHand() {
        return this.hand;
    }


}

