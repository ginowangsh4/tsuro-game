import java.util.*;

public class Player {
    protected Token token;
    protected List<Tile> hand;

    Player(Token t, List<Tile> hand){
        this.token = t;
        this.hand = hand;
    }

    /**
     * Get a player's token
     * @return a token
     */
    public Token getToken(){ return this.token; }

    /**
     * Update a player's token
     * @param token new token
     */
    public void updateToken(Token token){ this.token = token; }

    /**
     * Player draws a tile
     * @param t tile to be added to the player's hand
     */
    public void draw(Tile t){ hand.add(t); }

    public boolean deal(Tile t){ return hand.remove(t);}

    /**
     * Get a player's hand
     * @return a list of tiles on player's hand
     */
    public List<Tile> getHand() { return this.hand; }
}
