package tsuro;
import java.util.*;

public class SPlayer {
    private Token token;
    private List<Tile> hand;
    private IPlayer player;

    public SPlayer(Token token, List<Tile> hand) {
        this.token = token;
        this.hand = hand;
    }

    /**
     * Get the IPlayer linked with this SPlayer
     * @return an IPlayer
     */
    public IPlayer getPlayer() {
        return this.player;
    }

    // only used if certain that associated IPlayer is MPlayer
    public MPlayer getMPlayer() {
        return (MPlayer) this.player;
    }

    /**
     * Link IPlayer with current SPlayer
     * @param player IPlayer to be linked
     */
    public void linkPlayer(IPlayer player) throws Exception {
        if (getPlayer() != null && !player.getName().equals(this.getPlayer().getName())) {
            throw new IllegalArgumentException("SPlayer and Player mismatch");
        }
        this.player = player;
    }

    /**
     * Check if two SPlayers have the same color
     * @param player SPlayer to be checked against this SPlayer
     * @return true if colors are the same
     */
    public boolean isSamePlayer(SPlayer player) {
        return this.getToken().getColor() == player.getToken().getColor();
    }

    /**
     * Get a player's token
     * @return a token
     */
    public Token getToken() {
        return this.token;
    }

    /**
     * Update a player's token
     * @param token new token
     */
    public void updateToken(Token token) {
        if (!token.isSameColor(this.token))
            throw new IllegalArgumentException("The given token doesn't belong to this player");
        this.token = token;
;    }

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
     * Get a player's hand
     * @return a list of tiles on player's hand
     */
    public List<Tile> getHand() {
        return this.hand;
    }

    /**
     * SPlayer draws a tile
     * @param t tile to be added to the player's hand
     */
    public void draw(Tile t) {
        for (Tile tile: hand) {
            if (t.isSameTile(tile)) {
                throw new IllegalArgumentException("The tile to be drawn is already in player's hand");
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
}

