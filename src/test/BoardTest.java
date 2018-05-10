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
    public void addRemoveUpdateTokenTest() {
        b = new Board();
        Token t1 = new Token(1, 3, new int[]{0, 1});
        Token t2 = new Token(2, 7, new int[]{2, 3});
        Token t3 = new Token(2, 3, new int[]{0, 4});
        b.addToken(t1);
        b.addToken(t2);
        ArrayList<Token> myTokenList = new ArrayList<>();
        myTokenList.add(t1);
        myTokenList.add(t2);
        assertEquals(myTokenList, b.getTokenList(), "Error: t1 and t2 not both on board");
        b.removeToken(t1);
        myTokenList.remove(t1);
        assertEquals(myTokenList, b.getTokenList(), "Error: t2 not the only token on board");
        b.updateToken(t3);
        myTokenList.remove(t2);
        myTokenList.add(t3);
        assertEquals(myTokenList, b.getTokenList(), "Error: t2 not updated");
    }

    @Test
    public void addTokenThrowExceptionTest() {
        b = new Board();
        Token t1 = new Token(1, 5, new int[]{3, 5});
        Token t2 = new Token(1, 2, new int[]{0, 4});
        b.addToken(t1);
        assertThrows(IllegalArgumentException.class, () -> b.addToken(t2));
    }

    @Test
    public void removeTokenThrowExceptionTest() {
        b = new Board();
        Token t = new Token(1, 3, new int[]{0, 1});
        assertThrows(IllegalArgumentException.class, () -> b.removeToken(t));
    }

    @Test
    public void updateTokenThrowExceptionTest() {
        b = new Board();
        Token t1 = new Token(1, 3, new int[]{0, 1});
        Token t2 = new Token(2, 7, new int[]{2, 3});
        b.addToken(t1);
        assertThrows(IllegalArgumentException.class, () -> b.updateToken(t2));
    }

}