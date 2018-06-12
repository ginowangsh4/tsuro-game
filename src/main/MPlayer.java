package tsuro;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MPlayer extends APlayer {

    private String name;
    private int color;
    private List<Integer> colors;

    public MPlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void initialize (int color, List<Integer> colors) {
        checkState("initialize");
        validColorAndColors(color, colors);
        this.color = color;
        this.colors = colors;
    }

    public Token placePawn(Board b) {
        checkState("place-pawn");
        // initialize new [x-coordinate, y-coordinate, index on tile]
        int[] pos = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        boolean found = false;
        while (!found) {
            pos = findStartPosition();
            found = true;
            for (SPlayer sp : b.getSPlayerList()) {
                Token t = sp.getToken();
                if (pos[0] == t.getPosition()[0] && pos[1] == t.getPosition()[1] && pos[2] == t.getIndex()) {
                    found = false;
                    break;
                }
            }
        }
        return new Token(color, new int[]{pos[0], pos[1]}, pos[2]);
    }

    public void endGame(Board b, List<Integer> colors) {
        checkState("end-game");
    }

    /**
     * Choose and return a new starting position
     * @return a list of new position as [x-coordinate, y-coordinate, index on tile]
     */
    private int[] findStartPosition() {
        Random rand = new Random();
        int x, y, indexOnTile;
        // choose a random number in {0, 1, 2, 3}
        int side = rand.nextInt(4);
        // choose a random number in {0, 1, 2,..., 11}
        int sideIndex = rand.nextInt(12);
        switch (side) {
            case 0: {
                x = sideIndex / 2; // from 0 to 5
                y = -1;
                indexOnTile = sideIndex % 2 + 4;
                break;
            }
            case 1: {
                x = 6;
                y = sideIndex / 2;
                indexOnTile = sideIndex % 2 + 6;
                break;
            }
            case 2: {
                x = sideIndex / 2;
                y = 6;
                indexOnTile = sideIndex % 2;
                break;
            }
            case 3: {
                x = -1;
                y = sideIndex / 2;
                indexOnTile = sideIndex % 2 + 2;
                break;
            }
            default: {
                throw new IllegalArgumentException("Error: Unable to pick starting position on board");
            }
        }
        return new int[] {x, y, indexOnTile};
    }

    public List<Tile> findLegalMoves(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        List<Tile> legalMoves = new ArrayList<>();
        for (Tile t : hand) {
            Tile copy = t.copyTile();
            for (int i = 0; i < 4; i++) {
                SPlayer tempPlayer = new SPlayer(b.getSPlayer(color).getToken(), hand);
                tempPlayer.linkPlayer(this);
                if (Server.getInstance().legalPlay(tempPlayer, b, copy)) {
                    legalMoves.add(copy.copyTile());
                }
                copy.rotateTile();
            }
        }
        return legalMoves;
    }
}
