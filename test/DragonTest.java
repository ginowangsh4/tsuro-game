import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DragonTest {

//    moving where the player that has the dragon tile makes a move that causes an elimination (of another player)
//    moving where a player that does not have the dragon tile makes a move and it causes an elimination of the player that has the dragon tile
//    moving where the player that has the dragon tile makes a move that causes themselves to be eliminated

    static Board b;
    static Player p1, p2, p3;
    static Token t1, t2, t3;
    static Deck deck;
    static List<Player> inPlayer;
    static List<Player> outPlayer;
    static Server server = Server.getInstance();

    // Moving where no player has the dragon tile before or after
    @Test
    void dragonTest1(){
        b = new Board();
        deck = new Deck();
        t1 = new Token(1, 4, new int[]{0, -1});
        t2 = new Token(2, 2, new int[]{-1, 3});
        t3 = new Token(3, 0, new int[]{3, 6});
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
        Tile t = p1.getHand().get(2);
        p1.deal(t);
        server.init(b, inPlayer, outPlayer, deck);
        server.playATurn(t);
        assertEquals(3, p1.getHand().size(),"Error: player 1 didn't draw new tile");
        assertEquals(25, deck.size(),"Error: more than one player drew in a turn" );
        t = p2.getHand().get(0);
        p2.deal(t);
        server.playATurn(t);
        assertEquals(3, p2.getHand().size(),"Error: player 2 didn't draw new tile");
        assertEquals(24, deck.size(),"Error: more than one player drew in a turn" );
        t = p3.getHand().get(2);
        p3.deal(t);
        server.playATurn(t);
        assertEquals(3, p3.getHand().size(),"Error: player 3 didn't draw new tile");
        assertEquals(23, deck.size(),"Error: more than one player drew in a turn" );
    }

    // Moving where one player has the dragon tile before and no one gets any new tiles
    // This means in Player A plays a card but cannot draw any as Player B has the dragon tile
    // and the deck is empty
    @Test
    void dragonTest2() {
        b = new Board();
        List<Tile> pile = new ArrayList<>();
        Tile tile1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        pile.add(tile1);
        Tile tile2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        pile.add(tile2);
        Tile tile3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        pile.add(tile3);
        Tile tile4 = new Tile(new int[][]{{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        pile.add(tile4);
        Tile tile5 = new Tile(new int[][]{{0, 2}, {1, 4}, {3, 7}, {5, 6}});
        pile.add(tile5);
        Tile tile6 = new Tile(new int[][]{{0, 4}, {1, 7}, {2, 3}, {5, 6}});
        pile.add(tile6);
        Tile tile7 = new Tile(new int[][]{{0, 1}, {2, 6}, {3, 7}, {4, 5}});
        pile.add(tile7);
        Tile tile8 = new Tile(new int[][]{{0, 2}, {1, 6}, {3, 7}, {4, 5}});
        pile.add(tile8);
        Tile tile9 = new Tile(new int[][]{{0, 4}, {1, 5}, {2, 6}, {3, 7}});
        pile.add(tile9);
        deck = new Deck(pile);
        t1 = new Token(1, 4, new int[]{0, -1});
        t2 = new Token(2, 2, new int[]{-1, 3});
        t3 = new Token(3, 0, new int[]{3, 6});
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
        assertEquals(0, deck.size(),"Error: Deck shouldn't be non-empty");
        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        inPlayer.add(p1);
        inPlayer.add(p2);
        inPlayer.add(p3);
        // give Player 2 dragon tile
        server.getDragon(p2);
        Tile t = p1.getHand().get(2);
        p1.deal(t);
        server.init(b, inPlayer, outPlayer, deck);
        // Player 1 tries to draw tile but should fail due to empty deck
        server.playATurn(t);
        assertEquals(2, p1.getHand().size(), "Error: Player 1 should not draw any new tile");
        assertEquals(p2, server.getDragonHolder(),"Error: Dragon tile should not belong to other than Player 2");
    }
}