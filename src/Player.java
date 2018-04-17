import java.util.*;

public class Player {
    protected Token token;
    protected ArrayList<Tile> hand;
    protected boolean hasDragon = false;

    public Player(Token t, ArrayList<Tile> hand){
        this.token = t;
        this.hand = hand;
    }
    public Token getToken(){ return this.token; }

    public void draw(Tile t){ hand.add(t); }

    public ArrayList<Tile> getHand() { return this.hand; }

    public void getDragon() { this.hasDragon = true; }

    public void passDragon(Player p) {
        this.hasDragon = true;
        p.getDragon();
    }
}
