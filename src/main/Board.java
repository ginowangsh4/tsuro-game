package tsuro;

import java.util.*;
import java.util.List;

public class Board {
    public Tile[][] board;
    public final int SIZE = 6;
    public List<Token> tokenList;
    public List<Tile> tileList;

    public Board() {
        this.board = new Tile[SIZE][SIZE];
        this.tokenList = new ArrayList<>();
        this.tileList = new ArrayList<>();
    }

    /**
     * Get the tile on a given location on board
     * @param x x-coordinate
     * @param y y-coordinate
     * @return tile on a given location; null is not on board
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return null;
        }
        return this.board[x][y];
    }

    /**
     * Place the given tile in the given location
     * @param tile a tile to be placed
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void placeTile(Tile tile, int x, int y) throws ArrayIndexOutOfBoundsException {
        if (board[x][y] != null) {
            throw new IllegalArgumentException("This location has another tile");
        }
        board[x][y] = tile;
    }

    /**
     * Delete the tile in the given location if it currently has a tile
     * Should only be used in Server.legalPlay()
     * @param x the x-coordinate of the given location
     * @param y the y-coordinate of the given location
     */
    public void deleteTile(int x, int y) {
        if (board[x][y] == null) {
            throw new IllegalArgumentException("The location given doesn't contain a tile");
        }
        board[x][y] = null;
    }

    /**
     * Return if the board contains the given tile
     * @param tile a tile to be checked
     * @return
     */
    public boolean containsTile(Tile tile) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (getTile(i, j) != null && getTile(i, j ).isSameTile(tile)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    /**
     * Return the token with the given color
     * @param color color of the token
     * @return
     */
    public Token getToken(int color) {
        for (Token t : tokenList) {
            if (t.getColor() == color) {
                return t;
            }
        }
        throw new IllegalArgumentException("Token with this color does not exist on board");
    }

    /**
     * Return board contains a token with the given color
     * @param token a token to be checked
     */
    public boolean containsToken(Token token){
        for (Token t: tokenList){
            if (t.isSameColor(token)) return true;
        }
        return false;
    }

    /**
     * Add a token to the board
     * @param token a token to be added
     */
    public void addToken(Token token){
        if (!token.isLegalPosition()){
            throw new IllegalArgumentException("The token's position and index are not legal");
        }
        if(containsToken(token)){
            throw new IllegalArgumentException("The token given already exists on board");
        }
        tokenList.add(token);
    }

    /**
     * Remove a token from the board
     * @param token a token to be removed
     */
    public void removeToken(Token token) {
        if (!containsToken(token))
            throw new IllegalArgumentException("The token given doesn't exist on board");
        tokenList.remove(token);
    }

    /**
     * Update the token with a new one
     * @param newToken the new token
     */
    public void updateToken(Token newToken) {
        if (!containsToken(newToken)) {
            throw new IllegalArgumentException("The token given can't be updated since it doesn't exist on board");
        }
        Token oldToken = null;
        for (Token t: tokenList) {
            if (t.isSameColor(newToken)) {
                oldToken = t;
                break;
            }
        }
        tokenList.remove(oldToken);
        tokenList.add(newToken);
    }

    /**
     * Check whether the board is full
     * @return true if the board is full
     */
    public boolean isFull(){
        int count = 0;
        for (int x = 0; x < 6; x++){
            for (int y = 0; y < 6; y++) {
                if (getTile(x, y) != null) {
                    count++;
                }
            }
        }
        return count == 35;
    }

    /**
     * check if a position is a valid board position
     * @param position a board position
     * @return true if position is not a valid board position
     */
    public static boolean isOffBoard(int[] position){
        if(position.length != 2){
            throw new IllegalArgumentException("Not a valid position length");
        }
        if(position[0] < 0 || position[0] > 5 || position[1] < 0 || position[1] > 5){
            return true;
        }
        return false;
    }
}
