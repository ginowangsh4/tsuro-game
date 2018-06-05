package tsuro;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerLegalPlayTest {

    static Board b;
    static Token token;
    static SPlayer p;
    static Tile tile;
    static Server server = Server.getInstance();

    // Test 1: original position is legal, and token is simulated to cross one tile
    // Making a legal move where tile is placed in its original position
    @Test
    public void legalPlayTest1() {
        b = new Board();
        token = new Token(0, new int[]{0, 0}, 4);
        tile = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 1");
    }

    // Test 2: original position is legal, and token is simulated to cross two tiles
    // Making a legal move where tile is placed in its original position
    @Test
    public void legalPlayTest2() {
        b = new Board();
        token = new Token(0, new int[] {0,0}, 4);
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        Tile tile2= new Tile(new int[][] {{0,5}, {1,2}, {3,6}, {4,7}});
        b.placeTile(tile1, 0, 0);
        b.placeTile(tile2,1,1);
        tile = new Tile(new int[][] {{0,5}, {1,2}, {3,4}, {6,7}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile),"legalPlay - Expect Legal - Test 2");
    }

    // Test 3: all rotations are legal, tile is rotated once, not placed in its original position
    // Making a legal move where the tile is not placed in its original position
    @Test
    public void legalPlayTest3() {
        b = new Board();
        token = new Token(0, new int[]{0, 1}, 1);
        Tile tile1 = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][]{{0, 5}, {1, 4}, {2, 7}, {3, 6}});
        Tile tile2 = tile.copyTile();
        tile2.rotateTile();
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        assertEquals(true, server.legalPlay(p, b, tile2),"legalPlay - Expect Legal - Test 3");
    }

    // Test 4: original position is illegal, but no other tile is legal, and token is simulated to move to the edge
    // Making a legal move when all possible moves are illegal
    @Test
    public void legalPlayTest4() {
        b = new Board();
        token = new Token(0, new int[] {0,1}, 1);
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,4}, {1,5}, {2,6}, {3,7}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        p.draw(tile2);
        assertEquals(true, server.legalPlay(p, b, tile), "legalPlay - Expect Legal - Test 4");
    }

    // Test 5: tile is not in player's hand
    // Making a illegal move when player doesn't have this tile
    @Test
    public void legalPlayTest5() {
        b = new Board();
        token = new Token(0, new int[] {0,1}, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile1);
        assertEquals(false, server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 5");
    }

    // Test 6: original position is illegal, but other legal position exists for this tile
    // Making an illegal move where the move is an elimination move, but there are non-elimination move
    @Test
    public void legalPlayTest6() {
        b = new Board();
        token = new Token(0, new int[] {0,0}, 4);
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 0, 0);
        tile = new Tile(new int[][] {{0,2}, {1,7}, {3,4}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        assertEquals(false,server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 6");
    }

    // Test 7: all positions are illegal for this tile, but exists legal tiles in hand
    // Making an illegal move where the move is an elimination move, but there are non-elimination move
    @Test
    public void legalPlayTest7() {
        b = new Board();
        token = new Token(0, new int[] {0,1}, 1);
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new SPlayer(token, hand);
        p.draw(tile);
        p.draw(tile2);
        assertEquals(false, server.legalPlay(p, b, tile), "legalPlay - Expect Illegal - Test 7");
    }
}