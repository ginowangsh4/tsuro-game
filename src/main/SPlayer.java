import java.util.*;

public class SPlayer {
    private Token token;
    private List<Tile> hand;
    private String name;
    private MPlayer mPlayer;

    SPlayer(Token token, List<Tile> hand, String name) {
        this.token = token;
        this.hand = hand;
        this.name = name;
    }

    public void link(MPlayer mPlayer){
        if (mPlayer.getColor() != this.token.getColor())
            throw new IllegalArgumentException("SPlayer and MPlayer mismatch");
        this.mPlayer = mPlayer;
    }

    public MPlayer getmPlayer() { return this.mPlayer;}
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
    public Token getToken() { return this.token; }

    /**
     * Update a player's token
     *
     * @param token new token
     */
    public void updateToken(Token token) { this.token = token; }

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

