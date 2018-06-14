package tsuro;

import java.util.List;

// our own player
public class MPlayerKA extends MPlayer {
    public MPlayerKA(String name) {
        super(name);
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        checkState("play-turn");
        List<Tile> legalMoves = findLegalMoves(b, hand);
        return null;
    }
}
