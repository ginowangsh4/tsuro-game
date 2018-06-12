package tsuro;

import java.util.List;

public class MPlayerMostSym extends MPlayer {
    public MPlayerMostSym(String name) {
        super(name);
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        checkState("play-turn");
        List<Tile> legalMoves = findLegalMoves(b, hand);
        legalMoves.sort(new Tile.SymmetricComparator());
        return legalMoves.get(0);
    }
}
