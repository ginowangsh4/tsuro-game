import java.util.*;

public interface IPlayer {
    /**
     * Get the name of the player
     * @return name of the player
     */
    String getName();

    /**
     * Called to indicate a game is starting.
     * @param color the player's color
     * @param colors all of the players'colors, in the order that the game will be played.
     */
    void initialize(int color, List<Integer> colors);

    /**
     * Called at the first step in a game indicates where the player wishes to place their token
     * token must be placed along the edge in an unoccupied space.
     * @param b the current board state
     * @return a token with the player's color, its position [x,y] and index on tile.
     */
    Token placePawn(Board b);

    /**
     * Called to inform the player of the final board state and which players won the game.
     * @param b the current board game
     * @param colors the list of winner's colors
     */
    void endGame(Board b, List<Integer> colors);

    /**
     * Called to ask the player to make a move.
     * @param b the current board state
     * @param strategy the strategy that player plays
     * @param tilesLeft count of tiles that are not yet handed out to players.
     * @return the tile the player should place, suitably rotated.
     */
    Tile playTurn(Board b, String strategy, List<Tile> hand, int tilesLeft);
}
