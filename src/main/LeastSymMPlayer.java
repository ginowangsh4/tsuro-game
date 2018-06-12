package tsuro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeastSymMPlayer extends MPlayer {
    public LeastSymMPlayer(String name) {
        super(name);
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        checkState("play-turn");
        List<Tile> legalMoves = findLegalMoves(b, hand, tilesLeft);
        legalMoves.sort(new Tile.SymmetricComparator());
        return legalMoves.get(legalMoves.size() - 1);
    }
}
