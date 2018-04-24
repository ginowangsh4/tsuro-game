import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Tile> pile;

    Deck () {
        // TODO: hardcode and insert all tiles
        pile = new ArrayList<>();
    }

    Deck (List<Tile> pile) {
        this.pile = pile;
    }

    public Tile pop() {
        if (isEmpty()) {
            return null;
        }
        return pile.remove(0);
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
