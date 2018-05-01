import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// The following only covers the simplest test cases
// More complicate ones need to be build later to cover different logical aspects.
class PlayerTest {

    static Board b;
    static Tile tile;
    static List<Tile> pile;
    static Deck deck;
    static List<Player> inPlayer;
    static List<Player> outPlayer;
    static Server server = Server.getInstance();

    @Test
    void placePawnTest(){
        Board b = new Board();
        for (int i = 0; i < 8; i++){
            Token t = new Token(i);
            Player p = new Player(t,null );
            p.placePawn(b);
            int[] posn = p.getToken().getPosition();
            System.out.println("The player is currently standing at [" + posn[0] + ", " + posn[1] + "]" +
                    " and he is at index " + p.getToken().getIndex());
            assertTrue(posn[0] == -1 || posn[0] == 6 || posn[1] == -1 || posn[1] == 6,
                    "Error: Placed pawn at wrong position on board");
        }
    }

    @Test
    void reorderPathTest(){
        //This tile has two different ways it might be placed
        int[][] path1 = new int[][] {{0,4}, {1,5}, {2,7}, {3,6}}; // First way
        int[][] path2 = new int[][] {{0,5}, {1,4}, {2,6}, {3,7}}; // First way
        Tile t = new Tile(path1);
        Tile copy = t.copyTile();
        copy.rotateTile(); // Second Way
        SymmetricComparator.reorderPath(copy);
        assertTrue(Arrays.deepEquals(copy.paths, path2), "Error: paths are not in order");

        copy.rotateTile();//has the same pathways as the first way
        SymmetricComparator.reorderPath(copy);
        assertTrue(Arrays.deepEquals(copy.paths, path1), "Error: paths are not in order");

        copy.rotateTile();//has the same pathways as the second way
        SymmetricComparator.reorderPath(copy);
        assertTrue(Arrays.deepEquals(copy.paths, path2), "Error: paths are not in order");

        //This tile is symmetric and only has one way to be placed
        //no matter how it is rotated, the pathways are all the same
        Tile symmetricTile = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile symmetricCopy = symmetricTile.copyTile();
        symmetricCopy.rotateTile();
        SymmetricComparator.reorderPath(symmetricCopy);
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");

        symmetricCopy.rotateTile();
        symmetricCopy.rotateTile();
        SymmetricComparator.reorderPath(symmetricCopy);
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");

        symmetricCopy.rotateTile();
        symmetricCopy.rotateTile();
        SymmetricComparator.reorderPath(symmetricCopy);
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");
    }

    @Test
    void diffPathsTest(){
        // this tile has only one way to be placed
        Tile symmetricTile = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        assertEquals(1, SymmetricComparator.diffPaths(symmetricTile), "Error: a symmetric tile has " +
                " only one way to be placed" );

        // this tile has two ways to be placed
        Tile halfSymmetricTile = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        assertEquals(2, SymmetricComparator.diffPaths(halfSymmetricTile), "Error: a half symmetric tile has" +
                " two ways to be placed" );

        // this tile has four ways to be placed
        Tile asymmetricTile1 = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        assertEquals(4, SymmetricComparator.diffPaths(asymmetricTile1), "Error: a asymmetric tile has" +
                " four ways to be placed" );
        Tile asymmetricTile2 = new Tile(new int[][] {{0, 4}, {1, 6}, {2, 7}, {3, 5}});
        assertEquals(4, SymmetricComparator.diffPaths(asymmetricTile2), "Error: a asymmetric tile has" +
                " four ways to be placed" );
    }

    @Test
    void sortSymmetricTilesTest() {
        Tile symmetricTile = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile halfSymmetricTile = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile asymmetricTile = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        List<Tile> tileList = new ArrayList<>();
        tileList.add(halfSymmetricTile);
        tileList.add(symmetricTile);
        tileList.add(asymmetricTile);
        Collections.sort(tileList, new SymmetricComparator());
        assertTrue(Arrays.deepEquals(symmetricTile.paths, tileList.get(0).paths), "Error: the symmetric tile is not the first tile in tileList");
        assertTrue(Arrays.deepEquals(halfSymmetricTile.paths, tileList.get(1).paths), "Error: the half symmetric tile is not the second tile in tileList");
        assertTrue(Arrays.deepEquals(asymmetricTile.paths, tileList.get(2).paths), "Error: the asymmetric tile is not the third tile in tileList");

        symmetricTile = new Tile(new int[][] {{3, 2}, {7, 6}, {4, 5}, {1, 0}});
        halfSymmetricTile = new Tile(new int[][] {{2, 7}, {4, 0}, {5, 1}, {3, 6}});
        asymmetricTile = new Tile(new int[][] {{5, 0}, {6, 2}, {1, 3}, {7, 4}});
        tileList = new ArrayList<>();
        tileList.add(halfSymmetricTile);
        tileList.add(symmetricTile);
        tileList.add(asymmetricTile);
        Collections.sort(tileList, new SymmetricComparator());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}}, tileList.get(0).paths), "Error: the symmetric tile is not the first tile in tileList");
        assertTrue(Arrays.deepEquals(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}}, tileList.get(1).paths), "Error: the half symmetric tile is not the second tile in tileList");
        assertTrue(Arrays.deepEquals(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}}, tileList.get(2).paths), "Error: the asymmetric tile is not the third tile in tileList");
    }

    @Test
    void leastSymmetricStrategyTest1() {
        b = new Board();
        Tile tile0 = new Tile(new int[][] {{0, 7}, {1, 4}, {2, 6}, {3, 5}});
        b.placeTile(tile0, 0, 0);
        // starting position
        Token token = new Token(1, 4, new int[] {0, 0});
        List<Tile> hand = new ArrayList<>();
        // expect to play
        Tile tile1 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile tile2 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tile3 = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        hand.add(tile1);
        hand.add(tile2);
        hand.add(tile3);
        Player player = new Player(token, hand);
        b.addToken(token);
        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player);

        server.init(b, inPlayer, outPlayer, deck);
        Tile t = player.playTurn(b, "LS", pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 3}, {1, 4}, {2, 6}, {5, 7}}, t.paths), "Error: Picked wrong tile to play");

        server.playATurn(t);

        assertEquals(1, inPlayer.size(), "check inPlayer list");
        assertEquals(0, outPlayer.size(), "check outPlayer list");
        assertTrue(Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {0, 1}), "check player 1 token position");
        assertEquals(4, inPlayer.get(0).getToken().getIndex(),"check player 1 token index");
    }

    @Test
    void leastSymmetricStrategyTest2() {
        b = new Board();
        Tile tile0 = new Tile(new int[][] {{0, 7}, {1, 4}, {2, 6}, {3, 5}});
        b.placeTile(tile0, 0, 0);
        // starting position
        Token token = new Token(1, 4, new int[] {0, 0});
        List<Tile> hand = new ArrayList<>();
        // expect to play
        Tile tile1 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile tile2 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tile3 = new Tile(new int[][] {{0, 4}, {1, 6}, {2, 7}, {3, 5}});
        hand.add(tile1);
        hand.add(tile2);
        hand.add(tile3);
        Player player = new Player(token, hand);
        b.addToken(token);
        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player);

        server.init(b, inPlayer, outPlayer, deck);
        Tile t = player.playTurn(b, "LS", pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}}, t.paths), "Error: Picked wrong tile to play");

        server.playATurn(t);

        assertEquals(1, inPlayer.size(), "check inPlayer list");
        assertEquals(0, outPlayer.size(), "check outPlayer list");
        assertTrue(Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {0, 1}), "check player 1 token position");
        assertEquals(3, inPlayer.get(0).getToken().getIndex(),"check player 1 token index");
    }

    @Test
    void mostSymmetricStrategyTest() {
        b = new Board();
        Tile tile0 = new Tile(new int[][] {{0, 7}, {1, 4}, {2, 6}, {3, 5}});
        b.placeTile(tile0, 0, 0);
        // starting position
        Token token = new Token(1, 4, new int[] {0, 0});
        List<Tile> hand = new ArrayList<>();
        // expect to play
        Tile tile1 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile tile2 = new Tile(new int[][] {{0, 4}, {1, 5}, {2, 7}, {3, 6}});
        Tile tile3 = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        hand.add(tile1);
        hand.add(tile2);
        hand.add(tile3);
        Player player = new Player(token, hand);
        b.addToken(token);
        inPlayer = new ArrayList<>();
        outPlayer = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inPlayer.add(player);

        server.init(b, inPlayer, outPlayer, deck);
        Tile t = player.playTurn(b, "MS", pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}}, t.paths), "Error: Picked wrong tile to play");

        server.playATurn(t);

        assertEquals(1, inPlayer.size(), "check inPlayer list");
        assertEquals(0, outPlayer.size(), "check outPlayer list");
        assertTrue(Arrays.equals(inPlayer.get(0).getToken().getPosition(), new int[] {0, 0}), "check player 1 token position");
        assertEquals(3, inPlayer.get(0).getToken().getIndex(),"check player 1 token index");
    }
}