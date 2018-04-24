import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayATurnTest {

    static Board b;
    static Player p;
    static Tile tile;
    static List<Tile> pile;
    static Deck deck;
    static List<Player> inPlayer;
    static List<Player> outPlayer;
    static Server server = Server.getInstance();

    @Test // playATurn - Expect Game Over - Test 1: Player 1 and Player 2 move off board and both gets eliminated
        // Multiple players get eliminated
    void testPlayATurn1() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        Tile tile2 = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        tile = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile2, 2, 1);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 2,new int[] {1,2});
        token1.isNew = false;
        Token token2 = new Token(1, 5,new int[] {2,1});
        token2.isNew = false;
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, deck);

        assertEquals(null, server.playATurn(tile), "PlayATurn - Expect Game Over - Test 1");
        assertEquals(true, server.isGameOver(), "check game status");
        assertEquals(0, inPlayer.size(), "check inPlayer list");
        assertEquals(2, outPlayer.size(), "check outPlayer list");
        assertEquals(true, Arrays.equals(outPlayer.get(0).getToken().getPosition(), new int[] {2,0}), "check player 1 token position");
        assertEquals(1, outPlayer.get(0).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(outPlayer.get(1).getToken().getPosition(), new int[] {0,2}), "check player 2 token position");
        assertEquals(7, outPlayer.get(1).getToken().getIndex(), "check player 2 token index");
        server.setGameOver(false);
    }

    @Test // playATurn - Expect Game Not Over - Test 2: Player 1 and Player 2 move to a non-edge tile
        // Multiple players move at once and cross multiple tiles
    void testPlayATurn2() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        tile = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        Tile tile3 = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile3, 2, 2);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 1,new int[] {2,2});
        token1.isNew = false;
        Token token2 = new Token(1, 5,new int[] {2,0});
        token2.isNew = false;
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, deck);

        assertEquals(null, server.playATurn(tile), "PlayATurn - Expect Game Not Over - Test 2");
        assertEquals(false, server.isGameOver(), "check game status");
        assertEquals(2, inPlayer.size(),"check inPlayer list");
        assertEquals(0, outPlayer.size(),"check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {2,1}), "check player 1 token position");
        assertEquals(3, inPlayer.get(1).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(inPlayer.get(1).getToken().getPosition(), new int[] {2,1}), "check player 2 token position");
        assertEquals(7, inPlayer.get(0).getToken().getIndex(), "check player 2 token index");
    }

    @Test // playATurn - Expect Game Not Over - Test 3: Player 2 gets eliminated and player 1 wins
        // Multiple players move at once and cross multiple tiles
    void testPlayATurn3() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        Tile tile2 = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        tile = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile2, 2, 1);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 3, new int[] {1,2});
        token1.isNew = false;
        Token token2 = new Token(1, 5, new int[] {2,1});
        token2.isNew = false;
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, deck);

        assertEquals(inPlayer, server.playATurn(tile), "PlayATurn - Expect Game Over - Test 3");
        assertEquals(true, server.isGameOver(), "check game status");
        assertEquals(1, inPlayer.size(), "check inPlayer list");
        assertEquals(1, outPlayer.size(), "check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {2,2}), "check player 1 token position");
        assertEquals(5, inPlayer.get(0).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(outPlayer.get(0).getToken().getPosition(), new int[] {0,2}), "check player 2 token position");
        assertEquals(7, outPlayer.get(0).getToken().getIndex(),"check player 2 token index");
    }

    @Test // playATurn - Expect Game Not Over - Test 4: Player 1 and Player 2 move from an edge
    void testPlayATurn4(){
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Token token1 = new Token(0, 4, new int[] {1,-1});
        Token token2 = new Token(1, 4, new int[] {3,-1});
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, deck);

        assertEquals(null, server.playATurn(tile1), "PlayATurn - Expect Game Not Over - Test 4");
        assertEquals(2, inPlayer.size(), "check inPlayer list");
        assertEquals(0, outPlayer.size(), "check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(1).getToken().getPosition(), new int[] {1,0}), "check player 1 token position");
        assertEquals(4, inPlayer.get(0).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {3,-1}), "check player 2 token position");
        assertEquals(4, inPlayer.get(1).getToken().getIndex(),"check player 2 token index");

        assertEquals(null, server.playATurn(tile2), "PlayATurn - Expect Game Not Over - Test 4");
        assertEquals(2, inPlayer.size(), "check inPlayer list");
        assertEquals(0, outPlayer.size(), "check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {1,0}), "check player 1 token position");
        assertEquals(4, inPlayer.get(0).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(inPlayer.get(1).getToken().getPosition(), new int[] {3,0}), "check player 2 token position");
        assertEquals(4, inPlayer.get(1).getToken().getIndex(),"check player 2 token index");

    }
}