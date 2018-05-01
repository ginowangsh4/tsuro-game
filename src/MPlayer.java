import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MPlayer implements IPlayer {
    private String name;
    private int color;
    // list of player's colors in the order that the game will be played
    private List<Integer> colors;
    private boolean isWinner;
    public Token token;

    MPlayer (int color, List<Integer> colors) {
        this.color = color;
        this.name = Token.colorMap.get(color);
        this.colors = colors;
    }

    public String getName() {
        return this.name;
    }

    public void initialize (int color, List<Integer> colors) {
        this.color = color;
        this.name = Token.colorMap.get(color);
        this.colors = colors;
    }

    public Token placePawn(Board b) {
        Random rand = new Random();
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int indexOnTile = Integer.MAX_VALUE;
        boolean found = false;
        while (!found) {
            // choose random number in {0,1,2,3}
            int side = rand.nextInt(4);
            // choose random number in {0,1,2,...,11}
            int sideIndex = rand.nextInt(12);
            switch (side) {
                case 0: {
                    x = sideIndex / 2;
                    y = -1;
                    indexOnTile = sideIndex % 2;
                    break;
                }
                case 1: {
                    x = 6;
                    y = sideIndex / 2;
                    indexOnTile = sideIndex % 2 + 2;
                    break;
                }
                case 2: {
                    x = sideIndex / 2;
                    y = 6;
                    indexOnTile = sideIndex % 2 + 4;
                    break;
                }
                case 3: {
                    x = -1;
                    y = sideIndex / 2;
                    indexOnTile = sideIndex % 2 + 6;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Error: Unable to pick starting position on board");
                }
            }
            found = true;
            for (Token t : b.getTokenList()) {
                if (x == t.getPosition()[0] && y == t.getPosition()[1] && indexOnTile == t.getIndex()) {
                    found = false;
                    break;
                }
            }
        }
        if (x == Integer.MAX_VALUE || y == Integer.MAX_VALUE || indexOnTile == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Error: Unable to pick starting position on board");
        }
        Token newToken = new Token(this.color, indexOnTile, new int[]{x, y});
        b.addToken(newToken);
        this.token = newToken;
        return newToken;
    }

    public void endGame(Board b, List<Integer> colors) {
        this.colors = colors;
        if (colors.contains(this.color)) {
            this.isWinner = true;
        }
        this.isWinner = false;
    }

    public Tile playTurn(Board b, String strategy, List<Tile> hand, int tilesLeft) {
        List<Tile> legalMoves = new ArrayList<>();
        List<Tile> legalTiles = new ArrayList<>();
        for (Tile t : hand) {
            Tile copy = t.copyTile();
            for (int i = 0; i < 4; i++) {
                SPlayer tempPlayer = new SPlayer(this.token, hand, "");
                if (Server.getInstance().legalPlay(tempPlayer, b, copy)) {
                    legalMoves.add(copy.copyTile());
                    if (!legalTiles.contains(t)) {
                        legalTiles.add(t);
                    }
                }
                copy.rotateTile();
            }
        }
        switch (strategy) {
            case "R": {
                Random rand = new Random();
                return legalMoves.get(rand.nextInt(legalMoves.size()));
            }

            case "MS": {
                Collections.sort(legalMoves, new SymmetricComparator());
                return legalMoves.get(0);
            }

            case "LS": {
                Collections.sort(legalMoves, new SymmetricComparator());
                return legalMoves.get(legalMoves.size() - 1);
            }

            default: {
                throw new IllegalArgumentException("Input strategy cannot be identified");
            }
        }
    }
}
