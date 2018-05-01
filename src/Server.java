import java.util.*;

public class Server {

    private Board board;
    private Deck drawPile;
    public List<Player> inPlayer;
    public List<Player> outPlayer;
    private Player dragonHolder = null;
    private boolean gameOver = false;

    // Singleton Pattern
    private static Server server = null;
    private Server() {}

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public void init(Board board, List<Player> inPlayer, List<Player> outPlayer, Deck drawPile) {
        this.board = board;
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
        this.drawPile = drawPile;
        this.dragonHolder = null;
        this.gameOver = false;
    }

    /**
     * Return false if
     * 1) the tile is not (a possibly rotated version of) one of the tiles of the player
     * 2) the placement of the tile is an elimination move for the player, unless all of
     * the possible moves of all tiles in player's hand are elimination moves,
     * @param p the player that attempts to place a tile
     * @param b the board before the tile placement
     * @param t the tile that the player wishes to place on the board
     * @return true if this play is legal
     */
    @SuppressWarnings("Duplicates")
    public boolean legalPlay(Player p, Board b, Tile t) {
        // check condition (1) above
        if (!p.hasTile(t)) {
            return false;
        }
        // check condition (2) above
        Token currentT = p.getToken();
        int[] location = getAdjacentLocation(currentT);
        b.placeTile(t, location[0], location[1]);
        Token newT = simulateMove(currentT, b);
        b.deleteTile(location[0], location[1]);
        if (!newT.isOffBoard()){
            // original rotation is legal without considering other rotations
            return true;
        }
        else {
            Tile copy = t.copyTile();
            for (int i =0; i < 3; i++){
                copy.rotateTile();
                b.placeTile(copy, location[0], location[1]);
                newT = simulateMove(currentT, b);
                b.deleteTile(location[0], location[1]);
                if (!newT.isOffBoard()) {
                    // original rotation is illegal, as there is another legal rotation
                    return false;
                }
            }
            if (p.getHand().size() <= 1) {
                // original rotation is legal as
                // 1. only one tile in player's hand
                // 2. all rotations of this tile leads to elimination
                return true;
            }
            else {
                for (Tile pt: p.getHand()){
                    if (!t.isSameTile(pt)){
                        copy = pt.copyTile();
                        for (int i = 0; i < 4; i++) {
                            copy.rotateTile();
                            b.placeTile(copy, location[0], location[1]);
                            newT = simulateMove(currentT, b);
                            b.deleteTile(location[0], location[1]);
                            if (!newT.isOffBoard()) {
                                // original rotation is illegal, as there is another legal move
                                return false;
                            }
                        }
                    }
                }
            }
        }
        // all possible moves lead to elimination, return true
        return true;
    }

    /**
     * Computes the state of the game after the completion of a turn given the state of the game before the turn
     * @param t the tile to be placed on that board
     * @return the list of winner if the game is over; otherwise return null
     *         (drawPile, inPlayer, outPlayer are themselves updated and updated in server's status through private fields)
     */
    public List<Player> playATurn(Tile t) {
        // place a tile path
        Player currentP = inPlayer.get(0);
        Token currentT = currentP.getToken();
        int[] location = getAdjacentLocation(currentT);
        board.placeTile(t, location[0], location[1]);
        // move the token
        currentT = simulateMove(currentT, board);
        // update player's and board's copy of the token
        currentP.updateToken(currentT);
        board.updateToken(currentT);

        // eliminate current player & recycle tiles in hand
        if (currentT.isOffBoard()) {
            drawPile.addAndShuffle(currentP.getHand());
            currentP.getHand().clear();
            board.removeToken(currentT);
            // assign dragon holder to be the next player
            if (currentP.equals(dragonHolder)) {
                int index = findNextHolder(0);
                dragonHolder = index == -1 ? null : inPlayer.get(index);
            }
            inPlayer.remove(0);
            outPlayer.add(currentP);
            // players draw and pass dragon
            drawAndPassDragon();
        }
        // add to tail & draw tile
        else {
            if (drawPile.isEmpty()) {
                // player gets dragon if it is not dealt to another player
                giveDragon(currentP);
            }
            else {
                currentP.draw(drawPile.pop());
            }
            inPlayer.remove(0);
            inPlayer.add(currentP);
        }

        // check if other players can make a move because of the placement of this tile t
        for (int i = 0; i < inPlayer.size(); i++)
        {
            currentP = inPlayer.get(i);
            currentT = currentP.getToken();
            // At the start of the game, new players stand outside the board
            // Make sure don't eliminate them
            if (currentT.getPosition()[0] < 0 || currentT.getPosition()[0] > 5 ||
                    currentT.getPosition()[1] < 0 || currentT.getPosition()[1] > 5) {
                continue;
            }
            currentT = simulateMove(currentT, board);
            currentP.updateToken(currentT);
            board.updateToken(currentT);
            if (currentT.isOffBoard()) {
                drawPile.addAndShuffle(currentP.getHand());
                currentP.getHand().clear();
                board.removeToken(currentT);
                if (currentP.equals(dragonHolder)) {
                    int index = findNextHolder(inPlayer.indexOf(currentP));
                    dragonHolder = index == -1 ? null : inPlayer.get(index);
                }
                inPlayer.remove(currentP);
                outPlayer.add(currentP);
            }
            // players draw and pass dragon
            drawAndPassDragon();
        }

        // determine whether game is over
        if (inPlayer.size() == 1) {
            gameOver = true;
            return inPlayer;
        }
        //no one is the winner
        else if (inPlayer.size() == 0) {
            gameOver = true;
            return null;
        }
        return null;
    }

    /**
     * Simulate the path taken by a token given a board
     * @param token token that attempts making the move
     * @param board a board at a given state
     * @return a copy of the original token with new position and index
     */
    private Token simulateMove(Token token, Board board) {
        // next location the token can go on
        int[] location = getAdjacentLocation(token);
        Tile nextTile = board.getTile(location[0], location[1]);
        // base case, return if reached the end of path
        if (nextTile == null) {
            return token;
        }
        // simulate moving token & get new token index
        int pathStart = nextTile.neighborIndex.get(token.getIndex());
        int pathEnd = nextTile.getPathEnd(pathStart);
        // recursion step
        Token nt = new Token(token.getColor(), pathEnd, location);
        return simulateMove(nt, board);
    }

    /**
     * Find the adjacent position on board given a token
     * @param token the token of player currently making the move
     * @return an array of location [x,y] of the adjacent tile
     */
    private int[] getAdjacentLocation(Token token) {
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

    /**
     * Get the current dragon holder
     * @return the player with da dragon
     */
    public Player getDragonHolder() {
        return dragonHolder;
    }

    /**
     * Assign dragon tile to a player
     * @param p the player to assign to
     */
    public void giveDragon(Player p) {
        if (dragonHolder == null) {
            dragonHolder = p;
        }
    }

    /**
     * Pass dragon to the next player who has less than three tile on hand,
     * set dragon holder to be nobody if cannot find any or there is a winner
     */
    public void drawAndPassDragon() {
        if (dragonHolder == null) {
            return;
        }
        int index = inPlayer.indexOf(dragonHolder);
        while (!drawPile.isEmpty()) {
            dragonHolder.getHand().add(drawPile.pop());
            index = findNextHolder(index);
            // cannot find next player with < 3 tiles on his/her hand
            if (index == -1) {
                dragonHolder = null;
                return;
            }
            dragonHolder = inPlayer.get(index);
        }
    }

    /**
     * Find the index of next player with < 3 tiles on hand
     * @param index index of current dragon holder
     * @return the index of next player with < 3 tiles on hand
     */
    public int findNextHolder(int index) {
        int i = 0;
        while (i < inPlayer.size() - 1) {
            index = (index + 1) % inPlayer.size();
            if (inPlayer.get(index).getHand().size() < 3) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Check whether a game is over
     * @return true if game is over
     */
    public boolean isGameOver() { return this.gameOver; }

    /**
     * Set status of the game, mainly used by unit tests
     * @param b boolean value to set
     */
    public void setGameOver(boolean b) {this.gameOver = b; }
}
