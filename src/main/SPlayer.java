import java.util.*;

public class SPlayer {
    private Token token;
    private List<Tile> hand;
    private String name;
    private MPlayer MPlayer;

    SPlayer(Token token, List<Tile> hand, String name) {
        this.token = token;
        this.hand = hand;
        this.name = name;
    }

    /**
     * Link SPlayer to its corresponding MPlayer based on color
     * @param mPlayer
     */
    public void link(MPlayer mPlayer){
        if (mPlayer.getColor() != this.token.getColor())
            throw new IllegalArgumentException("SPlayer and MPlayer mismatch");
        this.MPlayer = mPlayer;
    }

    public MPlayer getMPlayer() { return this.MPlayer; }

    public boolean isSamePlayer(SPlayer player) {
        return this.getToken().getColor() == player.getToken().getColor();
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
     * @return a token
     */
    public Token getToken() { return this.token; }

    /**
     * Update a player's token
     * @param token new token
     */
    public void updateToken(Token token) {
        if (!token.sameColor(this.token))
            throw new IllegalArgumentException("The given token doesn't belong to this player");
        this.token = token;
;    }

    /**
     * SPlayer draws a tile
     *
     * @param t tile to be added to the player's hand
     */
    public void draw(Tile t) {
        for (Tile tile: hand) {
            if (t.isSameTile(tile)) {
                throw new IllegalArgumentException("The tile to be drew is already in player's hand");
            }
        }
        hand.add(t);
    }

    /**
     * Player deals a tile
     * @param t tile to be deal
     */
    public void deal(Tile t) {
        for (Tile tile : hand) {
            if (t.isSameTile(tile)) {
                hand.remove(tile);
                return;
            }
        }
        throw new IllegalArgumentException("The tile to be dealt is not in player's hand");
    }

    /**
     * Get a player's hand
     * @return a list of tiles on player's hand
     */
    public List<Tile> getHand() {
        return this.hand;
    }
}

