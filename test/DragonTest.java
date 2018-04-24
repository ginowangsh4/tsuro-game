import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DragonTest {


//    moving where one player has the dragon tile before and no one gets any new tiles
//    moving where the player that has the dragon tile makes a move that causes an elimination (of another player)
//    moving where a player that does not have the dragon tile makes a move and it causes an elimination of the player that has the dragon tile
//    moving where the player that has the dragon tile makes a move that causes themselves to be eliminated

    static Board b;
    static Player p1, p2, p3;
    static Token t1, t2, t3;
    static Deck deck;
    static Tile t;
    static List<Player> inPlayer;
    static List<Player> outPlayer;
    static Server server = Server.getInstance();

    @Test //moving where no player has the dragon tile before or after
    void dragonTest1(){
        b = new Board();
        deck = new Deck();
        t1 = new Token(1, 4, new int[]{0, -1});
        t2 = new Token(2, 2, new int[]{-1, 3});
        t3 = new Token(3, 0, new int[]{6, 3});
        b.addToken(t1);
        b.addToken(t2);
        b.addToken(t3);
        p1 = new Player(t1, new ArrayList<>());
        p2 = new Player(t2, new ArrayList<>());
        p3 = new Player(t3, new ArrayList<>());
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        inPlayer.add(p1);
        inPlayer.add(p2);
        inPlayer.add(p3);
        t = p1.getHand().get(2);
        p1.deal(t);
        server.init(b, inPlayer, outPlayer, deck);
        server.playATurn(t);
        assertEquals(3, p1.getHand().size(),"Error: player 1 doesn't have 3 tiles" );
        assertEquals(25, deck.size(),"Error: more than one player drew" );
    }

}