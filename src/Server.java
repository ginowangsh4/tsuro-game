import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.*;

public class Server {

    private Board board;

    private List<Tile> drawPile;
    private List<Player> inPlayer;
    private List<Player> outPlayer;

    private boolean gameOver = false;

    // Singleton Pattern
    private static Server server = null;

    private Server() {};

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public void init(Board board, List<Player> inPlayer, List<Player> outPlayer, List<Tile> drawPile) {
        this.board = board;
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
        this.drawPile = drawPile;
    };

    public boolean isGameOver() { return this.gameOver; }

    public void setGameOver(boolean b) {this.gameOver = b; }

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
        // check condition 1
        for (Tile pt : p.getHand()) {
            if (!t.isSameTile(pt)) {
                return false;
            }
            else {
                break;
            }
        }
        // check condition 2
        int[] location = getAdjacentLocation(p.getToken());
        b.placeTile(t, location[0], location[1]);
        Token token = simulateMove(p.getToken(), b);
        b.deleteTile(location[0], location[1]);
        if (!outOfBoard(token)){
            return true;    // original rotation is legal without considering other rotations
        }
        else {
            Tile newTile = t.copyTile();
            for (int i =0; i < 3; i++){
                newTile.rotateTile();
                b.placeTile(newTile, location[0], location[1]);
                token = simulateMove(p.getToken(), b);
                b.deleteTile(location[0], location[1]);
                if (!outOfBoard(token)) {
                    return false;   // original rotation is illegal, as there is another legal rotation
                }
            }
            if (p.getHand().size() > 1){
                for (Tile pt: p.getHand()){
                    if (!t.isSameTile(pt)){
                        newTile = pt.copyTile();
                        for (int i = 0; i < 4; i++) {
                            newTile.rotateTile();
                            b.placeTile(newTile, location[0], location[1]);
                            token = simulateMove(p.getToken(), b);
                            b.deleteTile(location[0], location[1]);
                            if (!outOfBoard(token)) {
                                return false;   // original rotation is illegal, as there is another legal move
                            }
                        }
                    }
                }
            }
            else {
                return true;    // original rotation is legal as 1. only one tile in player's hand 2. all rotations of this tile leads to elimination
            }
        }
        return true;
    }

    /**
     * Vomputes the state of the game after the completion of a turn given the state of the game before the turn
     * @param t          the tile to be placed on that board
     * @return the list of winner if the gmae is over; otherwise return null
     *         (drawPile, inPlayer, outPlayer are themselves updated and updated in server's status through private fields)
     */
    public List<Player> playATurn(Tile t) {
        // place a tile path
        Player currentP = inPlayer.get(0);
        inPlayer.remove(0);
        Token currentT = currentP.getToken();
        int[] location = getAdjacentLocation(currentT);
        board.placeTile(t, location[0], location[1]);
        // move the token
        Token tempT = simulateMove(currentT, board);
        currentT.setIndex(tempT.getIndex());
        currentT.setPosition(tempT.getPosition());
        // update player's and board's copy of the token
        currentP.updateToken(currentT);
        board.updateToken(currentT);
        // eliminate current player & recycle tiles in hand
        if (outOfBoard(currentT)) {
            drawPile.addAll(currentP.getHand());
            currentP.getHand().clear();
            board.removeToken(currentT);
            outPlayer.add(currentP);
        }
        // add to tail & draw tile
        else {
            if (drawPile.size() == 0) {
                currentP.getDragon();
            }
            else {
                Tile temp = drawPile.get(0);
                currentP.draw(temp);
                drawPile.remove(0);
            }
            inPlayer.add(currentP);
        }
        // check if other players can make a move because of the placement of this tile t
        for (int i = 0; i < inPlayer.size(); i++)
        {
            currentP = inPlayer.get(i);
            currentT = currentP.getToken();
            tempT = simulateMove(currentT, board);
            currentT.setPosition(tempT.getPosition());
            currentT.setIndex(tempT.getIndex());
            currentP.updateToken(currentT);
            board.updateToken(currentT);

            if (outOfBoard(currentT)) {
                drawPile.addAll(currentP.getHand());
                board.removeToken(currentT);
                currentP.getHand().clear();
                inPlayer.remove(currentP);
                outPlayer.add(currentP);
            }
        }

        // determine whether game is over
        if (inPlayer.size() == 1) {
            gameOver = true;
            return inPlayer;
        }
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
     * @return a stub (fake) token which only contains the final position on the board and index on the tile after the move
     */
    private Token simulateMove(Token token, Board board) {
        // next location the token can go on
        int[] location = getAdjacentLocation(token);
        Tile nextTile = board.getTile(location[0], location[1]);
        // return if reach the end of path
        if (nextTile == null) {
            return token;
        }
        // simulate moving token & get new token index
        int pathStart = nextTile.neighborIndex.get(token.getIndex());
        int pathEnd = nextTile.getPathEnd(pathStart);
        // recursion
        Token nt = new Token(pathEnd, location);
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
     * Check whether the token is on the edge of the board
     * @param token a token to be checked
     * @return true if on the edge; false if not
     */
    public boolean outOfBoard(Token token) {
        int ti = token.getIndex();
        int[] tl = token.getPosition();
        if ((ti == 0 || ti == 1) && tl[1] == 0 ||
                (ti == 2 || ti == 3) && tl[0] == 5 ||
                (ti == 4 || ti == 5) && tl[1] == 5 ||
                (ti == 6 || ti == 7) && tl[0] == 0) {
            return true;
        }
        return false;
    }
}
