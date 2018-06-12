package tsuro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMPlayer extends MPlayer{
    public RandomMPlayer(String name) {
        super(name);
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        checkState("play-turn");
        List<Tile> legalMoves = findLegalMoves(b, hand, tilesLeft);
        Random rand = new Random();
        return legalMoves.get(rand.nextInt(legalMoves.size()));
    }
}
