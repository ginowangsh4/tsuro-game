package tsuro;

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
        return null;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {

    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        return null;
    }
}
