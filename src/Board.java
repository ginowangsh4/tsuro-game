import com.sun.deploy.util.ArrayUtil;
import com.sun.tools.javac.util.ArrayUtils;

import java.util.*;

public class Board {
    protected Tile[][] board;
    protected final int SIZE = 6;
    protected List<Token> token_list;

    Board() {
        this.board = new Tile[SIZE][SIZE];
        this.token_list = new ArrayList<>();
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return null;
        }
        return this.board[x][y];
    }

    /**
     * Place the given tile in the given location
     * @param t a tile to be placed
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void placeTile(Tile t, int x, int y) { this.board[x][y] = t;}

    /**
     * Delete the tile in the given location if it currently holds a tile
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void deleteTile(int x, int y) {
        if (getTile(x, y) != null) this.board[x][y] = null;
        else return;
    }

    /**
     * Add a token to the board
     * @param t a token to be added
     */
    public void addToken(Token t){
        this.token_list.add(t);
    }

    /**
     * Remove a token from the board
     * @param inT a token to be removed
     */
    public void removeToken(Token inT) {
        for (int i = 0; i < token_list.size(); i++) {
            if (inT.equals(this.token_list.get(i))) {
                this.token_list.remove(i);
            }
        }
    }

    /**
     * Update the given token
     * @param t a new token
     */
    public void updateToken(Token t) {
        this.token_list.remove(t);
        this.token_list.add(t);
    }
}
