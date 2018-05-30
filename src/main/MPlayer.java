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
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public List<Integer> getColors() {
        return this.colors;
    }

    public void initialize (int color, List<Integer> colors) {
        if (color < 0 || color > 7) {
            throw new IllegalArgumentException("Invalid player's color");
        }
        for (int c : colors) {
            if (c < 0 || c > 7) {
                throw new IllegalArgumentException("Player list contains invalid" + "player color");
            }
        }
        if (state != State.DEAD && state != null) {
            throw new IllegalArgumentException("Sequence Contracts: Cannot initialize at this time");
        }
        state = State.BORN;
        this.color = color;
        this.colors = colors;
    }

    public Token placePawn(Board b) {
        if (state != State.BORN) {
            throw new IllegalArgumentException("Sequence Contracts: Cannot place pawn at this time");
        }
        this.state = State.PLAY;

        if (!colors.contains(color)){
            throw new IllegalArgumentException("Player is not authorized to place pawn");
        }

        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int indexOnTile = Integer.MAX_VALUE;
        boolean found = false;
        while (!found) {
            int[] res = findStartPosition();
            x = res[0];
            y = res[1];
            indexOnTile = res[2];
            found = true;
            for (Token t : b.getTokenList()) {
                if (x == t.getPosition()[0] && y == t.getPosition()[1] && indexOnTile == t.getIndex()) {
                    found = false;
                    break;
                }
            }
        }
        return new Token(color, indexOnTile, new int[]{x, y});
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        if (state != State.PLAY) {
            throw new IllegalArgumentException("Sequence Contracts: Cannot play turn at this time");
        }

        if (!colors.contains(this.color)){
            throw new IllegalArgumentException("Player is not authorized to place pawn");
        }

        List<Tile> legalMoves = new ArrayList<>();
        for (Tile t : hand) {
            Tile copy = t.copyTile();
            for (int i = 0; i < 4; i++) {
                SPlayer tempPlayer = new SPlayer(b.getToken(getColor()), hand);
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
        if (state != State.PLAY) {
            throw new IllegalArgumentException("Sequence Contracts: Cannot end game at this time");
        }
        state = State.DEAD;

        this.colors = colors;
        if (colors.contains(this.color)) {
            this.isWinner = true;
        }
        this.isWinner = false;
    }

    public int[] findStartPosition() {
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
        return new int[]{x, y, indexOnTile};
    }
}
