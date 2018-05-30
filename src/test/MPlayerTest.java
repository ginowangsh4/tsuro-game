package tsuro;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MPlayerTest {

    static Board b;
    static List<Tile> pile;
    static Deck deck;
    static List<SPlayer> inSPlayer;
    static List<SPlayer> outSPlayer;
    static List<SPlayer> winners;
    static List<Integer> colors = new ArrayList<>();
    static Server server = Server.getInstance();

    @Test
    public void placePawnTest(){
        Board b = new Board();
        colors.addAll(Arrays.asList(0,1,2,3,4,5,6));
        for (int i = 0; i < 7; i++){
            MPlayer p = new MPlayer(MPlayer.Strategy.R, "");
            p.initialize(i, colors);
            Token t = p.placePawn(b);
            b.addToken(t);
            assertTrue(t.isStartingPosition(), "Error: illegal token placement");
        }
    }

    @Test
    public void reorderPathTest(){
        //This tile has two different ways it might be placed
        int[][] path1 = new int[][] {{0,4}, {1,5}, {2,7}, {3,6}}; // First way
        int[][] path2 = new int[][] {{0,5}, {1,4}, {2,6}, {3,7}}; // Second way
        Tile t = new Tile(path1);
        Tile copy = t.copyTile();
        copy.rotateTile(); // Second Way
        copy.reorderPath();
        assertTrue(Arrays.deepEquals(copy.paths, path2), "Error: paths are not in order");

        copy.rotateTile();//has the same pathways as the first way
        copy.reorderPath();
        assertTrue(Arrays.deepEquals(copy.paths, path1), "Error: paths are not in order");

        copy.rotateTile();//has the same pathways as the second way
        copy.reorderPath();
        assertTrue(Arrays.deepEquals(copy.paths, path2), "Error: paths are not in order");

        //This tile is symmetric and only has one way to be placed
        //no matter how it is rotated, the pathways are all the same
        Tile symmetricTile = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile symmetricCopy = symmetricTile.copyTile();
        symmetricCopy.rotateTile();
        symmetricCopy.reorderPath();
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");

        symmetricCopy.rotateTile();
        symmetricCopy.rotateTile();
        symmetricCopy.reorderPath();
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");

        symmetricCopy.rotateTile();
        symmetricCopy.rotateTile();
        symmetricCopy.reorderPath();
        assertTrue(Arrays.deepEquals(symmetricTile.paths, symmetricCopy.paths), "Error: paths are not in order");
    }

    @Test
    public void countDiffPathsTest(){
        // this tile has only one way to be placed
        Tile symmetricTile = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        assertEquals(1, symmetricTile.countSymmetricPaths(), "Error: a symmetric tile has " +
                " only one way to be placed" );

        // this tile has two ways to be placed
        Tile halfSymmetricTile = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        assertEquals(2, halfSymmetricTile.countSymmetricPaths(), "Error: a half symmetric tile has" +
                " two ways to be placed" );

        // this tile has four ways to be placed
        Tile asymmetricTile1 = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        assertEquals(4, asymmetricTile1.countSymmetricPaths(), "Error: a asymmetric tile has" +
                " four ways to be placed" );
        Tile asymmetricTile2 = new Tile(new int[][] {{0, 4}, {1, 6}, {2, 7}, {3, 5}});
        assertEquals(4, asymmetricTile2.countSymmetricPaths(), "Error: a asymmetric tile has" +
                " four ways to be placed" );
    }

    @Test
    public void sortSymmetricTilesTest() {
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

    // Three tiles at hand:
    // First tile: one way to be placed, legal
    // Second tile: two ways to be placed, both legal
    // Third tile: four ways to be placed, original rotation legal, rotate once illegal,
    // rotate twice legal and rotate three times legal
    // Expect return the third tile after two rotations
    @Test
    public void leastSymmetricStrategyTest1() throws Exception {
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
        SPlayer SPlayer = new SPlayer(token, hand, "");
        b.addToken(token);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inSPlayer.add(SPlayer);

        server.setState(b, inSPlayer, outSPlayer, winners, deck);

        MPlayer mPlayer = new MPlayer(MPlayer.Strategy.LS, "");
        colors.add(1);
        mPlayer.initialize(1, colors);
        mPlayer.state = MPlayer.State.PLAY;
        Tile t = mPlayer.playTurn(b, hand, pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 3}, {1, 4}, {2, 6}, {5, 7}}, t.paths), "Error: Picked wrong tile to play");
        SPlayer.deal(t);
        server.playATurn(t);

        assertEquals(1, winners.size(), "check winner list");
        assertEquals(0, outSPlayer.size(), "check outSPlayer list");
        assertTrue(Arrays.equals(winners.get(0).getToken().getPosition(), new int[] {0, 1}), "check SPlayer 1 token position");
        assertEquals(4, winners.get(0).getToken().getIndex(),"check SPlayer 1 token index");
    }

    // Three tiles at hand:
    // First tile: one way to be placed, legal
    // Second tile: two ways to be placed, both legal
    // Third tile: four ways to be placed, original rotation illegal, rotate once legal,
    // rotate twice illegal and rotate three times legal
    // Expect return the third tile after three rotations
    @Test
    public void leastSymmetricStrategyTest2() throws Exception {
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
        SPlayer SPlayer = new SPlayer(token, hand, "");
        b.addToken(token);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inSPlayer.add(SPlayer);

        server.setState(b, inSPlayer, outSPlayer, winners, deck);

        MPlayer mPlayer = new MPlayer(MPlayer.Strategy.LS, "");
        colors.add(1);
        mPlayer.initialize(1, colors);
        mPlayer.state = MPlayer.State.PLAY;
        Tile t = mPlayer.playTurn(b, hand, pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}}, t.paths), "Error: Picked wrong tile to play");
        SPlayer.deal(t);
        server.playATurn(t);

        assertEquals(1, winners.size(), "check winner list");
        assertEquals(0, outSPlayer.size(), "check outSPlayer list");
        assertTrue(Arrays.equals(winners.get(0).getToken().getPosition(), new int[] {0, 1}), "check SPlayer 1 token position");
        assertEquals(3, winners.get(0).getToken().getIndex(),"check SPlayer 1 token index");
    }

    // Three tiles at hand:
    // First tile: one way to be placed, legal
    // Second tile: two ways to be placed, both legal
    // Third tile: four ways to be placed, original rotation legal, rotate once illegal,
    // rotate twice legal and rotate three times legal
    // Expect return first tile in original rotation.
    @Test
    public void mostSymmetricStrategyTest() throws Exception {
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
        SPlayer SPlayer = new SPlayer(token, hand, "");
        b.addToken(token);
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        pile = new ArrayList<>();
        deck = new Deck(pile);
        inSPlayer.add(SPlayer);

        server.setState(b, inSPlayer, outSPlayer, winners, deck);

        MPlayer mPlayer = new MPlayer( MPlayer.Strategy.MS, "");
        colors.add(1);
        mPlayer.initialize(1, colors);
        mPlayer.state = MPlayer.State.PLAY;
        Tile t = mPlayer.playTurn(b, hand, pile.size());
        assertTrue(Arrays.deepEquals(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}}, t.paths), "Error: Picked wrong tile to play");
        SPlayer.deal(t);

        server.playATurn(t);

        assertEquals(1, winners.size(), "check winner list");
        assertEquals(0, outSPlayer.size(), "check outSPlayer list");
        assertTrue(Arrays.equals(winners.get(0).getToken().getPosition(), new int[] {0, 0}), "check SPlayer 1 token position");
        assertEquals(3, winners.get(0).getToken().getIndex(),"check SPlayer 1 token index");
    }


}