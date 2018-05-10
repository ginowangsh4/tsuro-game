package tsuro;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DragonTest {

    static Board b;
    static SPlayer p1, p2, p3;
    static Token t1, t2, t3;
    static Deck deck;
    static List<SPlayer> inSPlayer;
    static List<SPlayer> outSPlayer;
    static Server server = Server.getInstance();

    // Moving where no player has the dragon tile before or after
    @Test
    public void dragonTest1(){
        b = new Board();
        deck = new Deck();
        t1 = new Token(1, 4, new int[]{0, -1});
        t2 = new Token(2, 2, new int[]{-1, 3});
        t3 = new Token(3, 0, new int[]{3, 6});
        b.addToken(t1);
        b.addToken(t2);
        b.addToken(t3);
        p1 = new SPlayer(t1, new ArrayList<>(), "");
        p2 = new SPlayer(t2, new ArrayList<>(), "");
        p3 = new SPlayer(t3, new ArrayList<>(), "");
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        inSPlayer.add(p1);
        inSPlayer.add(p2);
        inSPlayer.add(p3);
        Tile t = p1.getHand().get(2);
        p1.deal(t);
        server.setState(b, inSPlayer, outSPlayer, deck);
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
    // This means in SPlayer A plays a card but cannot draw any as SPlayer B has the dragon tile
    // and the deck is empty
    @Test
    public void dragonTest2() {
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
        p1 = new SPlayer(t1, new ArrayList<>(), "");
        p2 = new SPlayer(t2, new ArrayList<>(), "");
        p3 = new SPlayer(t3, new ArrayList<>(), "");
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p1.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p2.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        p3.draw(deck.pop());
        assertEquals(0, deck.size(),"Error: deck shouldn't be non-empty");
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        inSPlayer.add(p1);
        inSPlayer.add(p2);
        inSPlayer.add(p3);
        server.setState(b, inSPlayer, outSPlayer, deck);
        // give player 2 dragon tile
        server.giveDragon(p2);
        Tile t = p1.getHand().get(2);
        p1.deal(t);
        // SPlayer 1 tries to draw tile but should fail due to empty deck
        server.playATurn(t);
        assertEquals(2, p1.getHand().size(), "Error: player 1 should not draw any new tile");
        assertEquals(p2, server.getDragonHolder(),"Error: dragon tile should not belong to other than SPlayer 2");
    }

    // Moving where the player that has the dragon tile makes a move that causes an elimination (of another player)
    @Test
    public void dragonTest3() {
        b = new Board();
        //Tiles that are already on board
        Tile tb1 = new Tile(new int[][] {{0, 3}, {1, 4}, {2, 7}, {5, 6}});
        Tile tb2 = new Tile(new int[][] {{0, 7}, {1, 5}, {2, 6}, {3, 4}});
        Tile tb3 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tb4 = new Tile(new int[][] {{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        // Tiles in players' hands
        Tile tp1 = new Tile(new int[][] {{0, 7}, {1, 2}, {3, 4}, {5, 6}});
        Tile tp2 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile tp3 = new Tile(new int[][] {{0, 3}, {1, 2}, {4, 6}, {5, 7}});
        // Empty draw pile
        List<Tile> pile = new ArrayList<>();
        deck = new Deck(pile);
        b.placeTile(tb1, 2, 0);
        b.placeTile(tb2, 2, 1);
        b.placeTile(tb3, 1, 2);
        b.placeTile(tb4, 0, 2);
        t1 = new Token(1, 3, new int[]{1, 2});
        t2 = new Token(2, 5, new int[]{2, 1});
        t3 = new Token(3, 6, new int[]{4, 4});
        b.addToken(t1);
        b.addToken(t2);
        b.addToken(t3);
        p1 = new SPlayer(t1, new ArrayList<>(), "");
        p2 = new SPlayer(t2, new ArrayList<>(), "");
        p3 = new SPlayer(t3, new ArrayList<>(), "");
        p1.draw(tp1);
        p2.draw(tp2);
        p3.draw(tp3);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        inSPlayer.add(p1);
        inSPlayer.add(p2);
        inSPlayer.add(p3);
        server.setState(b, inSPlayer, outSPlayer, deck);
        // give player 1 dragon tile
        server.giveDragon(p1);
        Tile t = p1.getHand().get(0);
        p1.deal(t);
        // player 1 makes the move, eliminates player 2, and player 3 stay still
        server.playATurn(t);
        assertEquals(1, p1.getHand().size(), "Error: player 1 should have 1 tile");
        assertEquals(0, p2.getHand().size(), "Error: player 2 should have 0 tile");
        assertEquals(1, p3.getHand().size(), "Error: player 3 should have 1 tile");
        assertEquals(tp2, p1.getHand().get(0), "Error: player 1 drew the wrong tile");
        assertEquals(0, deck.size(), "Error: deck should be empty");
        assertEquals(p3, server.getDragonHolder(), "Error: player 3 should have the dragon tile");
    }

    // Moving where a player that does not have the dragon tile makes a move and it causes an elimination of
    // the player that has the dragon tile
    @Test
    public void dragonTest4() {
        b = new Board();
        //Tiles that are already on board
        Tile tb1 = new Tile(new int[][] {{0, 3}, {1, 4}, {2, 7}, {5, 6}});
        Tile tb2 = new Tile(new int[][] {{0, 7}, {1, 5}, {2, 6}, {3, 4}});
        Tile tb3 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tb4 = new Tile(new int[][] {{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        // Tiles in players' hands
        Tile tp1 = new Tile(new int[][] {{0, 7}, {1, 2}, {3, 4}, {5, 6}});
        Tile tp2 = new Tile(new int[][] {{0, 7}, {1, 6}, {2, 3}, {4, 5}});
        Tile tp3 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        // Empty draw pile
        List<Tile> pile = new ArrayList<>();
        deck = new Deck(pile);
        b.placeTile(tb1, 2, 0);
        b.placeTile(tb2, 2, 1);
        b.placeTile(tb3, 1, 2);
        b.placeTile(tb4, 0, 2);
        t1 = new Token(1, 3, new int[]{1, 2});
        t2 = new Token(2, 5, new int[]{2, 1});
        t3 = new Token(3, 6, new int[]{4, 4});
        b.addToken(t1);
        b.addToken(t2);
        b.addToken(t3);
        p1 = new SPlayer(t1, new ArrayList<>(), "");
        p2 = new SPlayer(t2, new ArrayList<>(), "");
        p3 = new SPlayer(t3, new ArrayList<>(), "");
        p1.draw(tp1);
        p2.draw(tp2);
        p3.draw(tp3);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        inSPlayer.add(p1);
        inSPlayer.add(p2);
        inSPlayer.add(p3);
        server.setState(b, inSPlayer, outSPlayer, deck);
        // give player 2 dragon tile
        server.giveDragon(p2);
        Tile t = p1.getHand().get(0);
        p1.deal(t);
        // player 1 makes the move, eliminates player 2, and player 3 gets dragon, draws and passes to player 1
        server.playATurn(t);
        assertEquals(0, p1.getHand().size(), "Error: player 1 should have 0 tile");
        assertEquals(0, p2.getHand().size(), "Error: player 2 should have 0 tile");
        assertEquals(2, p3.getHand().size(), "Error: player 3 should have 2 tiles");
        assertEquals(tp2, p3.getHand().get(1), "Error: player 3 drew the wrong tile");
        assertEquals(0, deck.size(), "Error: deck should be empty");
        assertEquals(p1, server.getDragonHolder(), "Error: player 1 should have the dragon tile");
    }

    // Moving where the player that has the dragon tile makes a move that causes themselves to be eliminated
    @Test
    public void dragonTest5() {
        b = new Board();
        //Tiles that are already on board
        Tile tb1 = new Tile(new int[][] {{0, 3}, {1, 4}, {2, 7}, {5, 6}});
        Tile tb2 = new Tile(new int[][] {{0, 7}, {1, 5}, {2, 6}, {3, 4}});
        Tile tb3 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tb4 = new Tile(new int[][] {{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        // Tiles in players' hands
        Tile tp1 = new Tile(new int[][] {{0, 7}, {1, 2}, {3, 4}, {5, 6}});
        Tile tp2 = new Tile(new int[][] {{0, 7}, {1, 6}, {2, 3}, {4, 5}});
        Tile tp3 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        // Empty draw pile
        List<Tile> pile = new ArrayList<>();
        deck = new Deck(pile);
        b.placeTile(tb1, 2, 0);
        b.placeTile(tb2, 2, 1);
        b.placeTile(tb3, 1, 2);
        b.placeTile(tb4, 0, 2);
        t1 = new Token(1, 2, new int[]{1, 2});
        t2 = new Token(2, 5, new int[]{2, 1});
        t3 = new Token(3, 6, new int[]{4, 4});
        b.addToken(t1);
        b.addToken(t2);
        b.addToken(t3);
        p1 = new SPlayer(t1, new ArrayList<>(), "");
        p2 = new SPlayer(t2, new ArrayList<>(), "");
        p3 = new SPlayer(t3, new ArrayList<>(), "");
        p1.draw(tp1);
        p2.draw(tp2);
        p3.draw(tp3);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        inSPlayer.add(p1);
        inSPlayer.add(p2);
        inSPlayer.add(p3);
        server.setState(b, inSPlayer, outSPlayer, deck);
        // give player 1 dragon tile
        server.giveDragon(p1);
        Tile t = p1.getHand().get(0);
        p1.deal(t);
        // player 1 makes the move, eliminates player 2 and himself,
        // player 3 gets dragon, draws the tile from player 2, and wins so no dragon holder
        server.playATurn(t);
        assertEquals(0, p1.getHand().size(), "Error: player 1 should have 0 tile");
        assertEquals(0, p2.getHand().size(), "Error: player 2 should have 0 tile");
        assertEquals(2, p3.getHand().size(), "Error: player 3 should have 2 tiles");
        assertEquals(tp2, p3.getHand().get(1), "Error: player 3 drew the wrong tile");
        assertEquals(0, deck.size(), "Error: deck should be empty");
        assertEquals(null, server.getDragonHolder(), "Error: no one should have the dragon tile");
    }
}