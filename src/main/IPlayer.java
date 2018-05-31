package tsuro;
import java.util.*;

public interface IPlayer {
    /**
     * Get the name of the IPlayer
     * @return name of the IPlayer
     */
    String getName() throws Exception;

    /**
     * Called to indicate a game is starting
     * @param color the IPlayer's color
     * @param colors all of the IPlayers' color, in the order that the game will be played
     */
    void initialize(int color, List<Integer> colors) throws Exception;

    /**
     * Called at the first step in a game indicates where the IPlayer wishes to place their token
     * token must be placed along the edge in an unoccupied space
     * @param b the current board state
     * @return a token with the IPlayer's color, its position [x,y] and index on tile
     */
    Token placePawn(Board b) throws Exception;

    /**
     * Called to ask the IPlayer to make a move
     * @param b the current board state
     * @param tilesLeft count of tiles that are not yet handed out to IPlayers
     * @return the tile the IPlayer should place, suitably rotated
     */
    Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception;

    /**
     * Called to inform the IPlayer of the final board state and which IPlayers won the game.
     * @param b the current board game
     * @param colors the list of winner's colors
     */
    void endGame(Board b, List<Integer> colors) throws Exception;
}
