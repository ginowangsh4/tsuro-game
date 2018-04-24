import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LegalPlayTest {

    static Board b;
    static Token token;
    static Player p;
    static Tile tile;
    static Server server = Server.getInstance();

    @Test // legalPlay - Expect Legal - Test 1: place a tile, not tile around it on board
    void testLegalPlay1() {
        b = new Board();
        token = new Token(0, 4, new int[]{0, 0});
        tile = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 1");
    }

    @Test // legalPlay - Expect Legal - Test 2: place a tile, move to some tile not at edge
    void testLegalPlay2() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
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

    @Test // legalPlay - Expect Legal - Test 3: only one tile in hand, all rotations lead to elimination
    void testLegalPlay3() {
        b = new Board();
        token = new Token(0, 1, new int[]{0, 1});
        Tile tile1 = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][]{{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 3");
    }

    @Test // legalPlay - Expect Legal - Test 4: all tiles at hand lead to elimination
    void testLegalPlay4() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
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

    @Test // legalPlay - Expect Illegal - Test 5: tile to be placed is not in player's hand
    void testLegalPlay5() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile1);
        assertEquals(false, server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 5");
    }

    @Test // legalPlay - Expect Illegal - Test 6: this rotation of the tile leads to elimination while other rotations do not
          // the move is an elimination move, but there are non-elimination moves available
    void testLegalPlay6() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 0, 0);
        tile = new Tile(new int[][] {{0,2}, {1,7}, {3,4}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        assertEquals(false,server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 6");
    }

    @Test // legalPlay - Expect Illegal - Test 7: all rotation of this tile leads to elimination but other tiles do not
          // the move is an elimination move, but there are non-elimination moves available
    void testLegalPlay7() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
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
}