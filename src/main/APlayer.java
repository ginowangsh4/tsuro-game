package tsuro;
import java.util.*;

public abstract class APlayer {

    public State state;
    public enum State { BORN, PLAY, DEAD }

    /**
     * Get the name of the APlayer
     * @return name of the APlayer
     */
    public abstract String getName() throws Exception;

    /**
     * Called to indicate a game is starting
     * @param color the APlayer's color
     * @param colors all of the APlayers' color, in the order that the game will be played
     */
    public abstract void initialize(int color, List<Integer> colors) throws Exception;

    /**
     * Called at the first step in a game indicates where the APlayer wishes to place their token
     * token must be placed along the edge in an unoccupied space
     * @param b the current board state
     * @return a token with the APlayer's color, its position [x,y] and index on tile
     */
    public abstract Token placePawn(Board b) throws Exception;

    /**
     * Called to ask the APlayer to make a move
     * @param b the current board state
     * @param tilesLeft count of tiles that are not yet handed out to APlayers
     * @return the tile the APlayer should place, suitably rotated
     */
    public abstract Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception;

    /**
     * Called to inform the APlayer of the final board state and which APlayers won the game.
     * @param b the current board game
     * @param colors the list of winner's colors
     */
    public abstract void endGame(Board b, List<Integer> colors) throws Exception;

    /**
     * Check APlayer's state against sequential contract
     * @param method string name of caller method
     */
    public void checkState(String method) {
        switch (method) {
            case "initialize":
                if (state != MPlayer.State.DEAD && state != null) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot initialize at this time");
                }
                state = MPlayer.State.BORN;
                break;
            case "place-pawn":
                if (state != MPlayer.State.BORN) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot place pawn at this time");
                }
                state = MPlayer.State.PLAY;
                break;
            case "play-turn":
                if (state != MPlayer.State.PLAY) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot play turn at this time");
                }
                break;
            case "end-game":
                if (state != MPlayer.State.PLAY) {
                    throw new IllegalArgumentException("Sequential Contracts: Cannot end game at this time");
                }
                state = MPlayer.State.DEAD;
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
    public void validColorAndColors(int color, List<Integer> colors) {
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
}
