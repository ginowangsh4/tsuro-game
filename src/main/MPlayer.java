package tsuro;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MPlayer implements IPlayer {

    private String name;
    private int color;
    private List<Integer> colors;
    private boolean isWinner;

    public Strategy strategy;
    public enum Strategy { R, MS, LS }
    public State state;
    public enum State { BORN, PLAY, DEAD }

    public MPlayer(Strategy strategy, String name) {
        this.name = name;
        this.strategy = strategy;
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

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        checkState("play-turn");
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
        Tile tileToPlay = null;
        switch (strategy) {
            case R: {
                Random rand = new Random();
                tileToPlay = legalMoves.get(rand.nextInt(legalMoves.size()));
                break;
            }
            case MS: {
                legalMoves.sort(new Tile.SymmetricComparator());
                tileToPlay = legalMoves.get(0);
                break;
            }
            case LS: {
                legalMoves.sort(new Tile.SymmetricComparator());
                tileToPlay = legalMoves.get(legalMoves.size() - 1);
                break;
            }
        }
        return tileToPlay;
    }

    public void endGame(Board b, List<Integer> colors) {
        checkState("end-game");
        this.colors = colors;
        if (colors.contains(this.color)) {
            isWinner = true;
        }
        isWinner = false;
    }

    /**
     * Check IPlayer's state against sequential contract
     * @param method string name of caller method
     */
    private void checkState(String method) {
        switch (method) {
            case "initialize":
                if (state != State.DEAD && state != null) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot initialize at this time");
                }
                state = State.BORN;
                break;
            case "place-pawn":
                if (state != State.BORN) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot place pawn at this time");
                }
                state = State.PLAY;
                break;
            case "play-turn":
                if (state != State.PLAY) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot play turn at this time");
                }
                break;
            case "end-game":
                if (state != State.PLAY) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot end game at this time");
                }
                state = State.DEAD;
                break;
            default:
                throw new IllegalArgumentException("Sequential Contract: Invalid caller method");
        }
    }

    /**
     * Check a color and a list of colors against certain constraints
     * @param color a color
     * @param colors a list of colors
     */
    private void validColorAndColors(int color, List<Integer> colors) {
        if (!colors.contains(color)){
            throw new IllegalArgumentException("Player is not authorized to be initialized");
        }
        if (color < 0 || color > 7) {
            throw new IllegalArgumentException("Invalid player's color");
        }
        for (int c : colors) {
            if (c < 0 || c > 7) {
                throw new IllegalArgumentException("Player list contains invalid" + "player color");
            }
        }
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
}
