package tsuro;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    static Board b;

    @Test
    public void constructorTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        assertTrue(Arrays.deepEquals(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}}, t1.getPaths()), "Error: Tile constructor failed");
    }

    @Test
    public void constructorThrowExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}, {8, 9}}));
        assertThrows(IllegalArgumentException.class, () -> new Tile(new int[][]{{0, 1, 2}, {2, 3}, {4, 5}, {6, 7}, {8, 9}}));
    }

    @Test
    public void rotateTileTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        t1.rotateTile();
        assertTrue(Arrays.deepEquals(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}}, t1.getPaths()), "Error: Rotate tile once failed");

        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        t2.rotateTile();
        t2.rotateTile();
        assertTrue(Arrays.deepEquals(new int[][]{{0, 6}, {1, 3}, {2, 7}, {4, 5}}, t2.getPaths()), "Error: Rotate tile twice failed");

        Tile t3 = new Tile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        t3.rotateTile();
        t3.rotateTile();
        t3.rotateTile();
        assertTrue(Arrays.deepEquals(new int[][]{{0, 2}, {1, 5}, {3, 7}, {4, 6}}, t3.getPaths()), "Error: Rotate tile three times failed");
        t3.rotateTile();
        assertTrue(Arrays.deepEquals(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}}, t3.getPaths()), "Error: Rotate tile four times failed");
    }

    @Test
    public void isSameTileTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        assertTrue(t1.isSameTile(t2), "Error: Same tile check failed");
    }

    @Test
    public void copyTileTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = t1.copyTile();
        assertTrue(t1.isSameTile(t2), "Error: Copy tile failed");
    }

    @Test
    public void getTilePathEndTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        assertEquals(0, t1.getPathEnd(1));
        assertEquals(1, t1.getPathEnd(0));
        assertEquals(2, t1.getPathEnd(3));
        assertEquals(3, t1.getPathEnd(2));
        assertEquals(4, t1.getPathEnd(5));
        assertEquals(5, t1.getPathEnd(4));
        assertEquals(6, t1.getPathEnd(7));
        assertEquals(7, t1.getPathEnd(6));
        assertThrows(IllegalArgumentException.class, () -> t1.getPathEnd(-1));
        assertThrows(IllegalArgumentException.class, () -> t1.getPathEnd(8));
        assertThrows(IllegalArgumentException.class, () -> t1.getPathEnd(Integer.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> t1.getPathEnd(Integer.MAX_VALUE));
    }
}