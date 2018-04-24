import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    static Board b;
    static Token token;
    static Player p;
    static Tile tile;
    static List<Tile> pile;
    static List<Player> inPlayer;
    static List<Player> outPlayer;
    static Server server = Server.getInstance();

    @Test
    void testLegalPlay1() {
        b = new Board();
        token = new Token(0, 4, new int[]{0, 0});
        token.isNew = false;
        tile = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 1");
    }

    @Test
    void testLegalPlay2() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        token.isNew = false;
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        Tile tile2= new Tile(new int[][] {{0,5}, {1,2}, {3,6}, {4,7}});
        b.placeTile(tile1, 0, 0);
        b.placeTile(tile2,1,1);
        tile = new Tile(new int[][] {{0,5}, {1,2}, {3,4}, {6,7}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 2");
    }

    @Test
    void testLegalPlay3() {
        b = new Board();
        token = new Token(0, 1, new int[]{0, 1});
        token.isNew = false;
        Tile tile1 = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][]{{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 3");
    }

    @Test
    void testLegalPlay4() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        token.isNew = false;
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,4}, {1,5}, {2,6}, {3,7}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
        assertEquals(true, server.legalPlay(p, b, tile), "legalPlay - Expect Legal - Test 4");
    }

    @Test
    void testLegalPlay5() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        token.isNew = false;
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile1);
        assertEquals(false, server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 5");
    }

    @Test
    void testLegalPlay6() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        token.isNew = false;
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 0, 0);
        tile = new Tile(new int[][] {{0,2}, {1,7}, {3,4}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(false,server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 6");
    }

    @Test
    void testLegalPlay7() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        token.isNew = false;
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
        assertEquals(false, server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 7");
    }

    @Test
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
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, pile);

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

    @Test
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
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, pile);

        assertEquals(null, server.playATurn(tile), "PlayATurn - Expect Game Over - Test 2");
        assertEquals(false, server.isGameOver(), "check game status");
        assertEquals(2, inPlayer.size(),"check inPlayer list");
        assertEquals(0, outPlayer.size(),"check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {2,1}), "check player 1 token position");
        assertEquals(3, inPlayer.get(1).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(inPlayer.get(1).getToken().getPosition(), new int[] {2,1}), "check player 2 token position");
        assertEquals(7, inPlayer.get(0).getToken().getIndex(), "check player 2 token index");
    }

    @Test
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
        inPlayer.add(player1);
        inPlayer.add(player2);

        server.init(b, inPlayer, outPlayer, pile);

        assertEquals(inPlayer, server.playATurn(tile), "PlayATurn - Expect Game Over - Test 3");
        assertEquals(true, server.isGameOver(), "check game status");
        assertEquals(1, inPlayer.size(), "check inPlayer list");
        assertEquals(1, outPlayer.size(), "check outPlayer list");
        assertEquals(true, Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {2,2}), "check player 1 token position");
        assertEquals(5, inPlayer.get(0).getToken().getIndex(), "check player 1 token index");
        assertEquals(true, Arrays.equals(outPlayer.get(0).getToken().getPosition(), new int[] {0,2}), "check player 2 token position");
        assertEquals(7, outPlayer.get(0).getToken().getIndex(),"check player 2 token index");
    }
}