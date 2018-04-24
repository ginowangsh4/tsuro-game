import com.sun.deploy.util.ArrayUtil;
import com.sun.tools.javac.util.ArrayUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    public void placeTile(Tile t, int x, int y) {
        try{
            if (board[x][y] != null) {
                throw new IllegalArgumentException("The location given contains another tile");
            }
            this.board[x][y] = t;
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("The location given is outside of board");
            throw e;
        }
    }

    /**
     * Delete the tile in the given location if it currently holds a tile
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void deleteTile(int x, int y) {
        try{
            if (board[x][y] == null) {
                throw new IllegalArgumentException("The location given doesn't contain a tile");
            }
            this.board[x][y] = null;
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("The location given is outside of board");
            throw e;
        }
    }

    /**
     * Add a token to the board
     * @param t a token to be added
     */
    public void addToken(Token t){
        if (token_list.contains(t)) {
            throw new IllegalArgumentException("The token given already exists on board");
        }
        this.token_list.add(t);
    }

    /**
     * Remove a token from the board
     * @param inT a token to be removed
     */
    public void removeToken(Token inT) {
        if (!token_list.remove(inT)) {
            throw new IllegalArgumentException("The token given doesn't exist on board");
        }
    }

    /**
     * Update the token with a new one
     * @param newT the new token
     */
    public void updateToken(Token newT) {
        Token oldT = null;
        for (Token t: token_list)
        {
            if (t.getColor() == newT.getColor()) {
                oldT = t;
                break;
            }
        }
        if (oldT == null) {
            throw new IllegalArgumentException("The token given can't be updated since it doesn't exist on board");
        }
        token_list.remove(oldT);
        token_list.add(newT);
    }
}
