import java.util.*;

public class Player {
    protected Token token;
    protected ArrayList<Tile> hand;
    protected boolean hasDragon = false;

    public Player(Token t, ArrayList<Tile> hand){
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

    /**
     * Get a player's hand
     * @return a list of tiles on player's hand
     */
    public ArrayList<Tile> getHand() { return this.hand; }

    /**
     * The player now holds the dragon tile
     */
    public void getDragon() { this.hasDragon = true; }

    /**
     * The player passes the dragon tile to another player
     * @param p the players to pass on to
     */
    public void passDragon(Player p) {
        this.hasDragon = true;
        p.getDragon();
    }
}
