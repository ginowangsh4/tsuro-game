import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

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
        assertEquals(1,SymmetricComparator.diffPaths(symmetricTile), "Error: a symmetric tile has " +
                " only one way to be placed" );

        // this tile has two ways to be placed
        Tile halfSymmetricTile = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        assertEquals(2,SymmetricComparator.diffPaths(halfSymmetricTile), "Error: a half symmetric tile has" +
                " two ways to be placed" );

        // this tile has four ways to be placed
        Tile asymmetricTile = new Tile(new int[][] {{0, 5}, {1, 3}, {2, 6}, {4, 7}});
        assertEquals(4,SymmetricComparator.diffPaths(asymmetricTile), "Error: a asymmetric tile has" +
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
        assertEquals(symmetricTile, tileList.get(0), "Error: the symmetric tile is not the first tile in tileList");
        assertEquals(halfSymmetricTile, tileList.get(1), "Error: the half symmetric tile is not the first tile in tileList");
        assertEquals(asymmetricTile, tileList.get(2), "Error: the asymmetric tile is not the first tile in tileList");
    }

}