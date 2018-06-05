package tsuro;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    static Board b;

    @Test
    public void constructorTest() {
        b = new Board();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                assertEquals(null, b.getTile(i, j), "Error: Board not empty");
            }
        }
    }

    @Test
    public void placeDeleteTest() {
        b = new Board();
        Tile tile1 = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        Tile tile2 = new Tile(new int[][]{{0, 5}, {1, 2}, {3, 6}, {4, 7}});
        b.placeTile(tile1, 3, 5);
        assertEquals(tile1, b.getTile(3, 5), "Error: Tile1 not at [3,5]");
        b.deleteTile(3, 5);
        b.placeTile(tile2, 3, 5);
        assertEquals(tile2, b.getTile(3, 5), "Error: Tile2 not at [3, 5]");
    }

    @Test
    public void placeTileThrowExceptionTest() {
        b = new Board();
        Tile tile1 = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        Tile tile2 = new Tile(new int[][]{{0, 5}, {1, 2}, {3, 6}, {4, 7}});
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> b.placeTile(tile1, 9, 3));
        b.placeTile(tile1, 3, 5);
        assertThrows(IllegalArgumentException.class, () -> b.placeTile(tile2, 3, 5));
    }

    @Test
    public void deleteTileThrowExceptionTest() {
        b = new Board();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> b.deleteTile(9, 3));
        assertThrows(IllegalArgumentException.class, () -> b.deleteTile(3, 5));
    }

    @Test
    public void addRemoveUpdateSPlayerTest() {
        b = new Board();
        Token t1 = new Token(1, new int[]{0, 1}, 3);
        Token t2 = new Token(2, new int[]{2, 3}, 7);
        Token t3 = new Token(2, new int[]{0, 4}, 3);
        SPlayer sp1 = new SPlayer(t1, null);
        SPlayer sp2 = new SPlayer(t2, null);
        SPlayer sp3 = new SPlayer(t3, null);
        b.addSPlayer(sp1);
        b.addSPlayer(sp2);
        ArrayList<SPlayer> sPlayerList = new ArrayList<>();
        sPlayerList.add(sp1);
        sPlayerList.add(sp2);
        assertEquals(sPlayerList, b.getSPlayerList(), "Error: t1 and t2 not both on board");
        b.removeSPlayer(sp1);
        sPlayerList.remove(sp1);
        assertEquals(sPlayerList, b.getSPlayerList(), "Error: t2 not the only token on board");
    }

    @Test
    public void addSPlayerThrowExceptionTest() {
        b = new Board();
        Token t1 = new Token(1, new int[]{3, 5}, 5);
        Token t2 = new Token(1, new int[]{0, 4}, 2);
        SPlayer sp1 = new SPlayer(t1, null);
        b.addSPlayer(sp1);
        SPlayer sp2 = new SPlayer(t2, null);
        assertThrows(IllegalArgumentException.class, () -> b.addSPlayer(sp2));
    }

    @Test
    public void removeSPlayerThrowExceptionTest() {
        b = new Board();
        Token t = new Token(1, new int[]{0, 1}, 3);
        SPlayer sp = new SPlayer(t, null);
        assertThrows(IllegalArgumentException.class, () -> b.removeSPlayer(sp));
    }
}