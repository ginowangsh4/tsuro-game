import java.util.*;
import java.util.List;

public class Board {
    private Tile[][] board;
    private final int SIZE = 6;
    private List<Token> tokenList;
    private List<Tile> tileList;

    Board() {
        this.board = new Tile[SIZE][SIZE];
        this.tokenList = new ArrayList<>();
        this.tileList = new ArrayList<>();
    }

    /**
     * Get the tile on a given location on board
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return tile on a given location; null is not on board or indices are invalid
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return null;
        }
        return this.board[x][y];
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    /**
     * Return the token with the given color
     *
     * @param color color of the token
     * @return
     */
    public Token getToken(int color) {
        for (Token t : this.tokenList) {
            if (t.getColor() == color) return t;
        }
        throw new IllegalArgumentException("Token with this color does not exist on board");
    }

    /**
     * Place the given tile in the given location
     *
     * @param t a tile to be placed
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void placeTile(Tile t, int x, int y) {
        try {
            if (board[x][y] != null) {
                throw new IllegalArgumentException("The location given contains another tile");
            }
            this.board[x][y] = t;
            tileList.add(t);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The location given is outside of board");
            throw e;
        }
    }

    /**
     * Delete the tile in the given location if it currently has a tile
     * Should only be used in Server.legalPlay()
     *
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void deleteTile(int x, int y) {
        try {
            if (board[x][y] == null) {
                throw new IllegalArgumentException("The location given doesn't contain a tile");
            }
            for (Tile t: tileList){
                if (t.isSameTile(board[x][y])){
                    tileList.remove(t);
                    break;
                }
            }
            this.board[x][y] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The location given is outside of board");
            throw e;
        }
    }

    /**
     * Return if the board contains the given tile
     *
     * @param t a tile to be checked
     * @return
     */
    public boolean containsTile(Tile t) {
        t.reorderPath();
        for (Tile tileOnBoard : tileList)
            if (tileOnBoard.isSameTile(t)) {
                return true;
            }
        return false;
    }

    /**
     * Return if a given token exists on board
     * @param inT a token to be checked
     */
    public boolean containsToken(Token inT){
        for (Token token: this.tokenList){
            if (token.sameColor(inT)) return true;
        }
        return false;
    }

    /**
     * Add a token to the board
     * @param inT a token to be added
     */
    public void addToken(Token inT){
        if (!inT.legalTokenPlacement(inT.getIndex(), inT.getPosition())){
            throw new IllegalArgumentException("The token's position and index are not legal");
        }
        if(containsToken(inT)){
            throw new IllegalArgumentException("The token given already exists on board");
        }
        this.tokenList.add(inT);
    }

    /**
     * Remove a token from the board
     * @param inT a token to be removed
     */
    public void removeToken(Token inT) {
        if (!containsToken(inT))
            throw new IllegalArgumentException("The token given doesn't exist on board");
        this.tokenList.remove(inT);
    }

    /**
     * Update the token with a new one
     * @param newT the new token
     */
    public void updateToken(Token newT) {
        Token oldT = null;
        for (Token t: tokenList)
        {
            if (t.sameColor(newT)) {
                oldT = t;
                break;
            }
        }
        if (oldT == null) {
            throw new IllegalArgumentException("The token given can't be updated since it doesn't exist on board");
        }
        tokenList.remove(oldT);
        tokenList.add(newT);
    }

    public boolean isFull(){
        int count = 0;
        for (int x = 0; x < 6; x++){
            for (int y = 0; y < 6; y++)
                if (getTile(x,y) != null) count++;
        }
        return count == 35? true:false;
    }
}
