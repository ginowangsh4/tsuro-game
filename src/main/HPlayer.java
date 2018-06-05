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

    // TODO: need to check of startIndex as well
    public Token placePawn(Board b) throws Exception {
        if (UISuite.startSide == null) {
            throw new Exception("UISuite.startSide has not been initialized!");
        }
        Token token = generateTokenBySideIndex(color, UISuite.startSide, UISuite.startIndex);
        return token;
    }

    // TODO: need to implement this
    public void endGame(Board b, List<Integer> colors) throws Exception {

    }

    // TODO: need to implement this
    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        return null;
    }


    /**
     * Generate token game object based on pawn location clicked from UI
     * @param colorIndex hplayer's color index
     * @param side side of location clicked
     * @param index index of location clicked
     * @return token game object
     */
    public static Token generateTokenBySideIndex(int colorIndex, UISuite.Side side, int index) throws Exception {
        if (index < 0 || index > 11) {
            throw new Exception("Index is not valid");
        }
        int indexOnTile, x, y;
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
        return new Token(colorIndex, pos, indexOnTile);
    }
}
