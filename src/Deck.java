import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Deck {

    private List<Tile> pile;

    Deck () {
        pile = new ArrayList<>();
        int[][][] input = new int[][][] {   {{0, 1}, {2, 3}, {4, 5}, {6, 7}},
                                            {{0, 1}, {2, 4}, {3, 6}, {5, 7}},
                                            {{0, 6}, {1, 5}, {2, 4}, {3, 7}},
                                            {{0, 5}, {1, 4}, {2, 7}, {3, 6}},
                                            {{0, 2}, {1, 4}, {3, 7}, {5, 6}},
                                            {{0, 4}, {1, 7}, {2, 3}, {5, 6}},
                                            {{0, 1}, {2, 6}, {3, 7}, {4, 5}},
                                            {{0, 2}, {1, 6}, {3, 7}, {4, 5}},
                                            {{0, 4}, {1, 5}, {2, 6}, {3, 7}},
                                            {{0, 1}, {2, 7}, {3, 4}, {5, 6}},
                                            {{0, 2}, {1, 7}, {3, 4}, {5, 6}},
                                            {{0, 3}, {1, 5}, {2, 7}, {4, 6}},
                                            {{0, 4}, {1, 3}, {2, 7}, {5 ,6}},
                                            {{0, 3}, {1, 7}, {2, 6}, {4, 5}},
                                            {{0, 1}, {2, 5}, {3, 6}, {4, 7}},
                                            {{0, 3}, {1, 6}, {2, 5}, {4, 7}},
                                            {{0, 1}, {2, 7}, {3, 5}, {4, 6}},
                                            {{0, 7}, {1, 6}, {2, 3}, {4, 5}},
                                            {{0, 7}, {1, 2}, {3, 4}, {5, 6}},
                                            {{0, 2}, {1, 4}, {3, 6}, {5, 7}},
                                            {{0, 7}, {1, 3}, {2, 5}, {4, 6}},
                                            {{0, 7}, {1, 5}, {2, 6}, {3, 4}},
                                            {{0, 4}, {1, 5}, {2, 7}, {3, 6}},
                                            {{0, 1}, {2, 4}, {3, 5}, {6, 7}},
                                            {{0, 2}, {1, 7}, {3, 5}, {4, 6}},
                                            {{0, 7}, {1, 5}, {2, 3}, {4, 6}},
                                            {{0, 4}, {1, 3}, {2, 6}, {5, 7}},
                                            {{0, 6}, {1, 3}, {2, 5}, {4, 7}},
                                            {{0, 1}, {2, 7}, {3, 6}, {4, 5}},
                                            {{0, 3}, {1, 2}, {4, 6}, {5, 7}},
                                            {{0, 3}, {1, 5}, {2, 6}, {4, 7}},
                                            {{0, 7}, {1, 6}, {2, 5}, {3, 4}},
                                            {{0, 2}, {1, 3}, {4, 6}, {5, 7}},
                                            {{0, 5}, {1, 6}, {2, 7}, {3, 4}},
                                            {{0, 5}, {1, 3}, {2, 6}, {4, 7}}    };
        for (int i = 0; i < input.length; i++) {
            Tile t = new Tile(input[i]);
            pile.add(t);
        }
        shuffle();
    }

    Deck (List<Tile> pile) {
        this.pile = pile;
    }

    public Tile pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Error: Can't draw tile from an empty deck");
        }
        return pile.remove(0);
    }

    public Tile peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Error: Can't draw tile from an empty deck");
        }
        return pile.get(0);
    }

    public void shuffle() {
        Collections.shuffle(pile);
    }

    public void addAndShuffle(List<Tile> l) {
        pile.addAll(l);
        shuffle();
    }

    public int size() {
        return pile == null ? 0 : pile.size();
    }

    public Tile get(int index) {
        return pile.get(index);
    }

    public void remove(int index) {
        pile.remove(index);
    }

    public boolean isEmpty() {
        return pile.isEmpty();
    }
}
