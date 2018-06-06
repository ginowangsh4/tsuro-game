package tsuro;

import java.util.*;
import java.util.List;

public class Board {

    public final int SIZE = 6;
    private Tile[][] board;
    private List<SPlayer> sPlayerList;

    public Board() {
        this.board = new Tile[SIZE][SIZE];
        this.sPlayerList = new ArrayList<>();
    }

    public Tile[][] getBoard() {
        return board;
    }

    public List<SPlayer> getSPlayerList() {
        return sPlayerList;
    }

//
//    public void setBoard(Tile[][] board) {
//        this.board = board;
//    }
//
//    public void setsPlayerList(List<SPlayer> sPlayerList) {
//        this.sPlayerList = sPlayerList;
//    }
//
//    public Board copyBoard() {
//        Tile[][] newBoard = new Tile[SIZE][SIZE];
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (board[i][j] != null) {
//                    newBoard[i][j] = new Tile(board[i][j].getPaths());
//                }
//            }
//        }
//        Board newB = new Board();
//        newB.setBoard(newBoard);
//        newB.setsPlayerList();
//    }
//

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
        return board[x][y];
    }

    /**
     * Return if the board contains the given tile
     * @param tile a tile to be checked
     * @return
     */
    public boolean containsTile(Tile tile) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (getTile(i, j) != null && getTile(i, j).isSameTile(tile)) {
                    return true;
                }
            }
        }
        return false;
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
     * Return the SPlayer with token of the given color
     * @param color color of the token
     * @return
     */
    public SPlayer getSPlayer(int color) {
        for (SPlayer sp : sPlayerList) {
            if (sp.getToken().getColor() == color) {
                return sp;
            }
        }
        throw new IllegalArgumentException("Token with this color does not exist on board");
    }

    /**
     * Return board contains a SPlayer with the given color
     * @param sPlayer a token to be checked
     */
    public boolean containsSPlayer(SPlayer sPlayer) {
        for (SPlayer sp : sPlayerList) {
            if (sp.isSameSPlayer(sPlayer)) return true;
        }
        return false;
    }

    /**
     * Add a SPlayer to the board
     * @param sPlayer SPlayer to be added
     */
    public void addSPlayer(SPlayer sPlayer) {
        if (!sPlayer.getToken().isLegalPosition()){
            throw new IllegalArgumentException("SPlayer's token position and index are not legal");
        }
        if(containsSPlayer(sPlayer)){
            throw new IllegalArgumentException("SPlayer's token given already exists on board");
        }
        sPlayerList.add(sPlayer);
    }

    /**
     * Remove a SPlayer from the board
     * @param sPlayer a SPlayer to be removed
     */
    public void removeSPlayer(SPlayer sPlayer) {
        if (!containsSPlayer(sPlayer))
            throw new IllegalArgumentException("SPlayer's token doesn't exist on board");
        sPlayerList.remove(sPlayer);
    }

    /**
     * Check whether another token is already on the same starting position
     * @param token input token to be checked against
     * @return true if there is another token on the same starting position
     */
    public boolean tokenAtSamePosition(Token token) {
        for (SPlayer sp : sPlayerList) {
            Token t = sp.getToken();
            if (t.getPosition()[0] == token.getPosition()[0] && t.getPosition()[1] == token.getPosition()[1]
                    && t.getIndex() == token.getIndex()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the board is full
     * @return true if the board is full
     */
    public boolean isFull() {
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
    public static boolean posOffBoard(int[] position) {
        return position[0] < 0 || position[0] > 5 || position[1] < 0 || position[1] > 5;
    }

    /**
     * Simulate the path taken by a token given a board
     * @param token token that attempts making the move
     * @return a copy of the original token with new position and index
     */
    public Token simulateMove(Token token) {
        // next location the token can go on
        int[] newPosition = getAdjacentLocation(token);
        Tile nextTile = getTile(newPosition[0], newPosition[1]);
        // base case, return if reached the end of path
        if (nextTile == null) {
            return token;
        }
        // simulate moving token & get new token index
        int pathStart = Tile.neighborIndex.get(token.getIndex());
        int pathEnd = nextTile.getPathEnd(pathStart);
        // recursion step
        Token nt = new Token(token.getColor(), newPosition, pathEnd);
        return simulateMove(nt);
    }

    /**
     * Find the adjacent position on board given a token
     * @param token the token of player currently making the move
     * @return an array of location [x,y] of the adjacent tile
     */
    public static int[] getAdjacentLocation(Token token) {
        int[] next = new int[2];
        int x = token.getPosition()[0];
        int y = token.getPosition()[1];
        int indexOnTile = token.getIndex();
        if (indexOnTile == 0 || indexOnTile == 1) {
            next[0] = x;
            next[1] = y - 1;
        } else if (indexOnTile == 2 || indexOnTile == 3) {
            next[0] = x + 1;
            next[1] = y;
        } else if (indexOnTile == 4 || indexOnTile == 5) {
            next[0] = x;
            next[1] = y + 1;
        } else {
            next[0] = x - 1;
            next[1] = y;
        }
        return next;
    }
}
