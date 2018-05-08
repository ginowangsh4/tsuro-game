import java.util.*;

public class Server {

    private Board board;
    private Deck drawPile;
    private List<SPlayer> inSPlayer;
    private List<SPlayer> outSPlayer;
    private SPlayer dragonHolder = null;
    private boolean gameOver = false;

    // singleton pattern
    private static Server server = null;
    private Server() {}

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }


    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, Deck drawPile) {
        this.board = board;
        this.inSPlayer = inSPlayer;
        this.outSPlayer = outSPlayer;
        this.drawPile = drawPile;
        this.dragonHolder = null;
        this.gameOver = false;
    }

    /**
     * Register a MPlayer with Server: create a SPlayer instance based on the given MPlayer
     * @param mP a given MPlayer
     */
    public void registerPlayer(MPlayer mP) {
        Token t = mP.placePawn(board);
        board.addToken(t);
        List<Tile> hand = new ArrayList<>();
        SPlayer sP = new SPlayer(t, hand, mP.getName());
        sP.link(mP);
        inSPlayer.add(sP);
        for (int i = 0; i < 3; i++){
            sP.draw(drawPile.pop());
        }
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
    public boolean legalPlay(SPlayer p, Board b, Tile t) {
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
     *         (drawPile, inSPlayer, outSPlayer are themselves updated and updated in server's status through private fields)
     */
    public List<SPlayer> playATurn(Tile t) {

        SPlayer currentP = inSPlayer.get(0);
        //Check if this player's hand is legal at the start of this turn
        legalHand(currentP);

        int[] location = getAdjacentLocation(currentP.getToken());
        board.placeTile(t, location[0], location[1]);

        List<SPlayer> deadP = new ArrayList<>();
        int playerCount = inSPlayer.size();
        for(int i = 0; i < playerCount; i++)
        {
            SPlayer player = inSPlayer.get(i);
            Token token = simulateMove(player.getToken(), board);
            player.updateToken(token);
            board.updateToken(token);
            if (token.isOffBoard()) {
                eliminatePlayer(player, deadP);
                i--;
                playerCount --;
            }
            else {
                if (i == 0 && player.isSamePlayer(currentP)){
                    inSPlayer.remove(0);
                    inSPlayer.add(player);
                    if (!drawPile.isEmpty()) {
                        player.draw(drawPile.pop());
                    }
                    else {
                        giveDragon(player);
                    }
                    i--;
                    playerCount--;
                }
            }
        }
        //Check if this player's hand is legal at the end of this turn
        legalHand(currentP);

        // determine whether game is over
        if (board.isFull()) {
            gameOver = true;
            if (inSPlayer.size() == 0) {
                inSPlayer.addAll(deadP);
                return inSPlayer;
            } else {
                outSPlayer.addAll(deadP);
                return inSPlayer;
            }
        }
        else if (inSPlayer.size() == 1) {
            gameOver = true;
            outSPlayer.addAll(deadP);
            return inSPlayer;
        }
        // everyone is eliminated at this round
        else if (inSPlayer.size() == 0) {
            gameOver = true;
            inSPlayer.addAll(deadP);
            return inSPlayer;
        }
        outSPlayer.addAll(deadP);
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
     * Handle elimination mechanism of a server player
     * @param p the player to eliminate from the game
     */
    private void eliminatePlayer(SPlayer p, List<SPlayer> dead){
        int pIndex = inSPlayer.indexOf(p);
        drawPile.addAndShuffle(p.getHand());
        p.getHand().clear();
        board.removeToken(p.getToken());
        // assign dragon holder to be the next player
        if (p.equals(dragonHolder)) {
            int index = findNextHolder(pIndex);
            dragonHolder = index == -1 ? null : inSPlayer.get(index);
        }
        inSPlayer.remove(pIndex);
        dead.add(p);
        // players draw and pass dragon
        drawAndPassDragon();
        // System.out.println("Eliminated player " + p.getMPlayer().getName());
    }

    /**
     * Get the current dragon holder
     * @return the player with da dragon
     */
    public SPlayer getDragonHolder() {
        return dragonHolder;
    }

    /**
     * Assign dragon tile to a player
     * @param p the player to assign to
     */
    public void giveDragon(SPlayer p) {
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
        int index = inSPlayer.indexOf(dragonHolder);
        while (!drawPile.isEmpty()) {
            dragonHolder.getHand().add(drawPile.pop());
            index = findNextHolder(index);
            // cannot find next player with < 3 tiles on his/her hand
            if (index == -1) {
                dragonHolder = null;
                return;
            }
            dragonHolder = inSPlayer.get(index);
        }
    }

    /**
     * Find the index of next player with < 3 tiles on hand
     * @param index index of current dragon holder
     * @return the index of next player with < 3 tiles on hand
     */
    public int findNextHolder(int index) {
        int i = 0;
        while (i < inSPlayer.size() - 1) {
            index = (index + 1) % inSPlayer.size();
            if (inSPlayer.get(index).getHand().size() < 3) {
                return index;
            }
            i++;
        }
        return -1;
    }

    /**
     *
     * @param p
     * @return
     */
    private void legalHand(SPlayer p) {
        List<Tile> hand = p.getHand();
        if (hand.size() == 0 || hand == null) {
            return;
        }
        // no more than three tiles
        else if (hand.size() > 3) {
            throw new IllegalArgumentException("Player's hand illegal: more than 3 tiles on hand");
        }

        List<Tile> pile = drawPile.getPile();
        List<Tile> onBoard = board.getTileList();
        List<Tile> inHands = new ArrayList<>();
        for (SPlayer player : inSPlayer) {
            inHands.addAll(player.getHand());
        }

        for (Tile playerTile : hand) {
            // not already on board
            for (Tile t : onBoard) {
                if (t.isSameTile(playerTile)) {
                    throw new IllegalArgumentException("Player's hand illegal: tile exists on board");
                }
            }
            // not in the draw pile
            for (Tile t : pile) {
                if (t.isSameTile(playerTile)) {
                    throw new IllegalArgumentException("Player's hand illegal: tile exists in draw pile");
                }
            }

            int count = 0;
            for (Tile t : inHands) {
                if (t.isSameTile(playerTile)) {
                    count ++;
                    if (count > 1) {
                        throw new IllegalArgumentException("Player's hand illegal: tile exists in other player's hand");
                    }
                }
            }

        }
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
