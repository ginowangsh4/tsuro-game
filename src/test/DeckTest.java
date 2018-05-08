import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    static Deck deck;

    @Test
    void defaultConstructorTest() {
        deck = new Deck();
        assertEquals(35, deck.size(), "Error: Default deck constructor failed");
    }

    @Test
    void customConstructorTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t3 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t4 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        List<Tile> pile = new ArrayList<>();
        pile.add(t1);
        pile.add(t2);
        pile.add(t3);
        pile.add(t4);
        deck = new Deck(pile);
        assertEquals(4, deck.size(), "Error: Custom deck constructor failed");
    }

    @Test
    void popTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t3 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t4 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        List<Tile> pile = new ArrayList<>();
        pile.add(t1);
        pile.add(t2);
        pile.add(t3);
        pile.add(t4);
        deck = new Deck(pile);
        Tile t5 = deck.pop();
        assertEquals(3, deck.size());
        assertTrue(t5.isSameTile(t1), "Error: Pop from deck failed");
        Tile t6 = deck.pop();
        assertEquals(2, deck.size());
        assertTrue(t6.isSameTile(t2), "Error: Pop from deck failed");
        Tile t7 = deck.pop();
        assertEquals(1, deck.size());
        assertTrue(t7.isSameTile(t3), "Error: Pop from deck failed");
        Tile t8 = deck.pop();
        assertEquals(0, deck.size());
        assertTrue(deck.isEmpty());
        assertTrue(t8.isSameTile(t4), "Error: Pop from deck failed");
        assertThrows(NoSuchElementException.class, () -> deck.pop());
    }

    @Test
    void peekTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t3 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t4 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        List<Tile> pile = new ArrayList<>();
        pile.add(t1);
        pile.add(t2);
        pile.add(t3);
        pile.add(t4);
        deck = new Deck(pile);
        Tile t5 = deck.peek();
        assertEquals(4, deck.size());
        assertTrue(t5.isSameTile(t1), "Error: Peek from deck failed to not remove tile");
    }

    @Test
    void addAndShuffleTest() {
        Tile t1 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t3 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t4 = new Tile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        List<Tile> pile = new ArrayList<>();
        pile.add(t1);
        pile.add(t2);
        pile.add(t3);
        pile.add(t4);
        deck = new Deck(pile);
        assertEquals(4, deck.size(), "Error: Custom deck constructor failed");
        deck.addAndShuffle(pile);
        assertEquals(8, deck.size(), "Error: Add all to deck failed");
    }
}