package tsuro;

import tsuro.admin.UISuite;

import java.util.List;

public class HPlayer implements IPlayer {
    private String name;
    private int color;
    private List<Integer> colors;
    private boolean isWinner;

    public HPlayer.Strategy strategy;
    public enum Strategy { R, MS, LS }
    public HPlayer.State state;
    public enum State { INIT, PLACE, PLAY, END };

    public HPlayer(Strategy strategy){this.strategy = strategy;}

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
        for (int aColor : colors) {
            if (aColor < 0 || aColor > 7) {
                throw new IllegalArgumentException("Player list contains invalid" + "player color");
            }
        }
        if (!(state == HPlayer.State.END || state == null)){
            throw new IllegalArgumentException("Sequence Contracts: Cannot initialize at this time");
        }
        state = HPlayer.State.INIT;
        this.color = color;
        this.name = Token.colorMap.get(color);
        this.colors = colors;
    }

    public Token placePawn(Board b) throws Exception {
        Token token = generateTokenBySideIndex(color, UISuite.startSide,UISuite.startIndex);
        return null;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {

    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        return null;
    }

    public static Token generateTokenBySideIndex(int colorIndex, UISuite.Side side, int index) throws Exception {
        if (index < 0 || index > 11) {
            throw new Exception("Index is not valid");
        }
        int indexOnTile;
        int x;
        int y;
        if (side == UISuite.Side.TOP) {
            x = index / 2;
            y = -1;
            indexOnTile = Tile.neighborIndex.get(index % 2);
        }
        else if (side == UISuite.Side.BOTTOM) {
            x = index / 2;
            y = 6;
            indexOnTile = index % 2;
        }
        else if (side == UISuite.Side.LEFT) {
            x = -1;
            y = index / 2;
            indexOnTile = index % 2 + 2;
        }
        else {
            x = 6;
            y = index / 2;
            indexOnTile = Tile.neighborIndex.get(index % 2 + 2);
        }
        int[] pos = new int[]{x,y};
        return new Token(colorIndex, indexOnTile, pos);
    }
}
