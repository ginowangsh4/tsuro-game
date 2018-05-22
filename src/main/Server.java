package tsuro;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    public Board board;
    public Deck drawPile;
    public List<SPlayer> inSPlayer;
    public List<SPlayer> outSPlayer;
    public List<Integer> colors;
    public SPlayer dragonHolder = null;
    public boolean gameOver = false;

    // singleton pattern
    private static Server server = null;
    private Server() {
        this.board = new Board();
        this.drawPile = new Deck();
        this.inSPlayer = new ArrayList<>();
        this.outSPlayer = new ArrayList<>();
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    // mainly used by unit tests
    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, Deck drawPile) {
        this.board = board;
        this.inSPlayer = inSPlayer;
        this.outSPlayer = outSPlayer;
        this.drawPile = drawPile;
        this.dragonHolder = null;
        this.gameOver = false;
    }

    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, Deck drawPile, List<Integer> colors) {
        this.board = board;
        this.inSPlayer = inSPlayer;
        this.outSPlayer = outSPlayer;
        this.colors = colors;
        this.drawPile = drawPile;
        this.dragonHolder = null;
        this.gameOver = false;
    }

    public void startGame() throws Exception {
        ServerSocket socketListener = new ServerSocket(6666);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            List<Integer> colors = new ArrayList<>();

            RemotePlayer rP = new RemotePlayer(socketListener.accept(), db);
            MPlayer mP1 = new MPlayer(MPlayer.Strategy.R);
            MPlayer mP2 = new MPlayer(MPlayer.Strategy.LS);
            MPlayer mP3 = new MPlayer(MPlayer.Strategy.MS);
            colors.add(0);
            colors.add(1);
            colors.add(2);
            colors.add(3);
            rP.initialize(0, colors);
            mP1.initialize(1, colors);
            mP2.initialize(2, colors);
            mP3.initialize(3, colors);
            Token t0 = rP.placePawn(board);
            server.registerPlayer(rP, t0);
            Token t1 = mP1.placePawn(board);
            server.registerPlayer(mP1, t1);
            Token t2 = mP2.placePawn(board);
            server.registerPlayer(mP2, t2);
            Token t3 = mP3.placePawn(board);
            server.registerPlayer(mP3, t3);

            while(!server.isGameOver()) {
                SPlayer currentP = inSPlayer.get(0);
                System.out.println("Current player = " + currentP.getPlayer().getName());
                Tile tileToPlay = currentP.getPlayer().playTurn(board, currentP.getHand(), drawPile.size());
                currentP.deal(tileToPlay);
                server.playATurn(tileToPlay);
            }

            List<Integer> winners = server.getCurrentColors();
            for (SPlayer sPlayer : inSPlayer) {
                System.out.println("Ending game for winners = " + sPlayer.getPlayer().getName());
                sPlayer.getPlayer().endGame(server.board, winners);
            }
            for (SPlayer sPlayer : outSPlayer) {
                System.out.println("Ending game for losers = " + sPlayer.getPlayer().getName());
                sPlayer.getPlayer().endGame(server.board, winners);
            }

            System.out.println("Game over = " + server.gameOver);
            for (SPlayer sPlayer : server.inSPlayer) {
                System.out.println("Winner = " + sPlayer.getPlayer().getName());
            }

            socketListener.close();

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a MPlayer with Server: create a SPlayer instance based on the given MPlayer
     * @param player a given player
     */
    public void registerPlayer(IPlayer player, Token t) throws Exception {
        List<Tile> hand = new ArrayList<>();
        SPlayer sP = new SPlayer(t, hand, player.getName());
        sP.linkPlayer(player);
        // check if starting position is legal
        if (!t.isStartingPosition()) {
            System.err.println("Caught cheating: Player starts the game at an illegal position");
            playerCheatIllegalPawn(sP);
        }
        colors.add(t.getColor());
        inSPlayer.add(sP);
        board.addToken(sP.getToken());
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
            for (Tile tile : p.getHand()) {
                Tile copy = tile.copyTile();
                for (int i = 0; i < 4; i++) {
                    copy.rotateTile();
                    b.placeTile(copy, location[0], location[1]);
                    newT = simulateMove(currentT, b);
                    b.deleteTile(location[0], location[1]);
                    if (!newT.isOffBoard()) {
                        // original rotation is illegal, as there is at least one
                        // 1. other legal rotation of this tile
                        // 2. other legal tile
                        return false;
                    }
                }
            }
        }
        // all possible moves lead to elimination
        return true;
    }

    public boolean legalPlay(MPlayer mp, Board b, Tile t) {
        for (SPlayer sp : inSPlayer) {
            if (mp.getColor() == sp.getToken().getColor()) {
                return legalPlay(sp, b, t);
            }
        }
        throw new IllegalArgumentException("Caught cheating: Dead player tries to play turn");
    }

    /**
     * Computes the state of the game after the completion of a turn given the state of the game before the turn
     * @param t the tile to be placed on that board
     * @return the list of winner if the game is over; otherwise return null
     *         (drawPile, inSPlayer, outSPlayer are themselves updated and updated in server's status through private fields)
     */
    public List<SPlayer> playATurn(Tile t) throws Exception {
        SPlayer currentP = inSPlayer.get(0);
        // *****************************************
        // ****** Step 1: Contract Validation ******
        // *****************************************
        // check if player is cheating by purposefully playing an illegal move
        currentP.draw(t);
        if (!legalPlay(currentP, board, t)) {
            System.err.println("Caught cheating: Player tried to play an illegal tile while holding at least one other legal tile");
            t = playerCheatIllegalTile(currentP);
        }
        currentP.deal(t);
        // check if this player's hand is legal at the start of this turn
        legalHand(currentP);

        // ***********************************************
        // ****** Step 2: Board & Player Operation *******
        // ***********************************************
        // place tile on the board
        int[] location = getAdjacentLocation(currentP.getToken());
        board.placeTile(t, location[0], location[1]);
        // move all remaining players
        List<SPlayer> deadP = new ArrayList<>();
        int playerCount = inSPlayer.size();
        for (int i = 0; i < playerCount; i++)
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
                // if this player is active player, do something
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
        // check if this player's hand is legal at the end of this turn
        legalHand(currentP);

        // ****************************************
        // ** Step 3: Update Game Over Condition **
        // ****************************************
        // game over if board is full

        System.out.println(deadP.size());
        System.out.println(inSPlayer.size());
        System.out.println(outSPlayer.size());
        System.out.println(board.tokenList.size());
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
        // game over if only one player remains
        else if (inSPlayer.size() == 1) {
            gameOver = true;
            outSPlayer.addAll(deadP);
            return inSPlayer;
        }
        // game over if all remaining players are eliminated at this round
        else if (inSPlayer.size() == 0) {
            gameOver = true;
            inSPlayer.addAll(deadP);
            System.out.println(deadP.size());
            System.out.println(inSPlayer.size());
            System.out.println(outSPlayer.size());
            System.out.println(board.tokenList.size());
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
        int[] newPosition = getAdjacentLocation(token);
        Tile nextTile = board.getTile(newPosition[0], newPosition[1]);
        // base case, return if reached the end of path
        if (nextTile == null) {
            return token;
        }
        // simulate moving token & get new token index
        int pathStart = nextTile.neighborIndex.get(token.getIndex());
        int pathEnd = nextTile.getPathEnd(pathStart);
        // recursion step
        Token nt = new Token(token.getColor(), pathEnd, newPosition);
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
    private void eliminatePlayer(SPlayer p, List<SPlayer> dead) throws Exception {
        int pIndex = inSPlayer.indexOf(p);
        drawPile.addAndShuffle(p.getHand());
        p.getHand().clear();
        //board.removeToken(p.getToken());
        // assign dragon holder to be the next player
        if (p.equals(dragonHolder)) {
            int index = findNextHolder(pIndex);
            dragonHolder = index == -1 ? null : inSPlayer.get(index);
        }
        inSPlayer.remove(pIndex);
        dead.add(p);
        // players draw and pass dragon
        drawAndPassDragon();
        System.out.println("Player " + p.getName() + " eliminated!");
    }

    public void playerCheatIllegalPawn(SPlayer p) throws Exception {
        System.out.println("Player " + p.getName() + " cheated and is replaced by a random machine player");
        MPlayer newPlayer = new MPlayer(MPlayer.Strategy.R);
        newPlayer.initialize(Token.getColorInt(p.getName()), colors);
        p.linkPlayer(newPlayer);
        p.updateToken(p.getPlayer().placePawn(board));
    }

    public Tile playerCheatIllegalTile(SPlayer p) throws Exception {
        System.out.println("Player " + p.getName() + " cheated and is replaced by a random machine player");
        MPlayer newPlayer = new MPlayer(MPlayer.Strategy.R);
        newPlayer.initialize(Token.getColorInt(p.getName()), colors);
        newPlayer.state = MPlayer.State.PLAY;
        p.linkPlayer(newPlayer);
        Tile newTile = p.getPlayer().playTurn(board, p.getHand(), drawPile.size());
        return newTile;
    }

    /**
     * Check whether player's hand is legal against behavior contracts
     * @param p current player
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
            // not in the current draw pile
            if (drawPile.containsTile(playerTile)) {
                throw new IllegalArgumentException("Player's hand illegal: tile exists in draw pile");
            }
            // not in other player's hand or the current player's hand does not contain duplicate tiles
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
        // make sure tile is a valid tile in the original deck
        Deck tempDeck = new Deck();
        for (Tile t : hand) {
            if (!tempDeck.containsTile(t)) {
                throw new IllegalArgumentException("Player's hand illegal: tile is not a legal tile");
            }
        }
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
     * Check whether a game is over
     * @return true if game is over
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * Set status of the game, mainly used by unit tests
     * @param b boolean value to set
     */
    public void setGameOver(boolean b) {
        this.gameOver = b;
    }

    /**
     * Get the board associated with the game
     * @return the board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Get the current colors of the game
     * @return a list of winner colors
     * @throws Exception
     */
    public List<Integer> getCurrentColors() throws Exception {
        List<Integer> colors = new ArrayList<>();
        for (SPlayer sPlayer : inSPlayer) {
            colors.add(Token.getColorInt(sPlayer.getPlayer().getName()));
        }
        return colors;
    }

    /**
     * Get the all colors of the game
     * @return a list of winner colors
     * @throws Exception
     */
    public List<Integer> getAllColors() throws Exception {
        List<Integer> colors = new ArrayList<>();
        for (SPlayer sPlayer : inSPlayer) {
            if (sPlayer.getPlayer() instanceof MPlayer) {
                return ((MPlayer) sPlayer.getPlayer()).getColors();
            }
            colors.add(Token.getColorInt(sPlayer.getPlayer().getName()));
        }
        throw new IllegalArgumentException("Cannot get all colors");
    }
}


