package tsuro;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    public final int PORT_NUM = 8000;

    public Board board;
    public Deck drawPile;
    public List<SPlayer> inSPlayers;
    public List<SPlayer> outSPlayers;
    public List<SPlayer> winners;
    public List<Integer> colors;
    public SPlayer dragonHolder = null;
    public boolean gameOver = false;

    private static Server server = null;

    private Server() {
        this.board = new Board();
        this.drawPile = new Deck();
        this.inSPlayers = new ArrayList<>();
        this.outSPlayers = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.colors = new ArrayList<>();
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    // both mainly used by unit tests
    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, List<SPlayer> winners, Deck drawPile) {
        this.board = board;
        this.inSPlayers = inSPlayer;
        this.outSPlayers = outSPlayer;
        this.winners = winners;
        this.drawPile = drawPile;
        this.dragonHolder = null;
        this.gameOver = false;
    }

    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, List<SPlayer> winners, List<Integer> colors, Deck drawPile) {
        setState(board, inSPlayer, outSPlayer, winners, drawPile);
        this.colors = colors;
    }

    /**
     * Register a IPlayer with Server; also create corresponding SPlayer
     * @param ip a given player
     */
    public void registerPlayer(IPlayer ip, Token token) throws Exception {
        List<Tile> hand = new ArrayList<>();
        SPlayer sp = new SPlayer(token, hand);
        sp.linkPlayer(ip);
        // check if starting position is legal
        if (!token.isStartingPosition() || board.tokenAtSamePosition(token)) {
            System.err.println("Caught cheating: Player starts the game at an illegal position");
            playerCheatIllegalPawn(sp);
        }
        inSPlayers.add(sp);
        board.addSPlayer(sp);
        for (int i = 0; i < 3; i++){
            sp.draw(drawPile.pop());
        }
    }

    /**
     * Check if a tile is a legal play; return false if
     * 1) the tile is not (a possibly rotated version of) one of the tiles of the SPlayer
     * 2) the placement of the tile is an elimination move for the SPlayer, unless all of
     * the possible moves of all tiles in player's hand are elimination moves,
     * @param sp the player that attempts to place a tile
     * @param b the board before the tile placement
     * @param t the tile that the player wishes to place on the board
     * @return true if this tile is a legal play
     */
    public boolean legalPlay(SPlayer sp, Board b, Tile t) {
        // check condition (1)
        if (!sp.hasTile(t)) {
            return false;
        }
        // check condition (2)
        Token currT = sp.getToken();
        int[] location = Board.getAdjacentLocation(currT);
        b.placeTile(t, location[0], location[1]);
        Token newT = b.simulateMove(currT);
        b.deleteTile(location[0], location[1]);
        if (!newT.isOffBoard()){
            // original rotation is legal, return true
            return true;
        }
        // try placing other tiles and rotations
        for (Tile tile : sp.getHand()) {
            Tile copy = tile.copyTile();
            for (int i = 0; i < 4; i++) {
                copy.rotateTile();
                b.placeTile(copy, location[0], location[1]);
                newT = b.simulateMove(currT);
                b.deleteTile(location[0], location[1]);
                if (!newT.isOffBoard()) {
                    // original rotation is illegal for one of below is true:
                    // 1. one other rotation of this tile is legal
                    // 2. one other tile with some rotation is legal
                    return false;
                }
            }
        }
        // all possible moves lead to elimination, current move is legal
        return true;
    }

    /**
     * Computes the state of the game after the completion of a turn given the state of the game before the turn
     * @param t the tile to be placed on that board
     * @return the list of winner if the game is over; otherwise return null
     *         (drawPile, inSPlayers, outSPlayers are themselves updated and updated in server's fields)
     */
    public List<SPlayer> playATurn(Tile t) throws Exception {
        SPlayer currentP = inSPlayers.get(0);
        // *****************************************
        // ****** Step 1: Contract Validation ******
        // *****************************************
        // check if SPlayer is cheating by playing an illegal move
        currentP.draw(t);
        if (!legalPlay(currentP, board, t)) {
            System.err.println("Caught cheating: Player tried to play an illegal tile while holding at least one other legal tile");
            t = playerCheatIllegalTile(currentP);
        }
        currentP.deal(t);
        // check if this SPlayer's hand is legal
        checkLegalHand(currentP);

        // ***********************************************
        // ****** Step 2: Board & Player Operation *******
        // ***********************************************
        // place tile on the board
        int[] location = Board.getAdjacentLocation(currentP.getToken());
        board.placeTile(t, location[0], location[1]);
        // move all remaining active SPlayers
        List<SPlayer> deadP = new ArrayList<>();
        for (int i = 0; i < inSPlayers.size(); i++) {
            SPlayer player = inSPlayers.get(i);
            Token token = board.simulateMove(player.getToken());
            player.updateToken(token);
            if (token.isOffBoard()) {
                deadP.add(player);
            }
            // current SPlayer draw or get dragon
            if (i == 0 && player.isSameSPlayer(currentP)){
                if (!drawPile.isEmpty()) {
                    player.draw(drawPile.pop());
                }
                else {
                    giveDragon(player);
                }
            }
        }
        // move the current SPlayer
        inSPlayers.remove(0);
        inSPlayers.add(currentP);

        // ****************************************
        // ** Step 3: Update Game Over Condition **
        // ****************************************
        findWinners(deadP);
        // game over, return winners
        if (gameOver) {
            return winners;
        }
        // game not over, eliminate SPlayers
        returnHandToDeck(deadP);
        eliminateSPlayers(deadP);
        drawAndPassDragon();
        outSPlayers.addAll(deadP);
        return null;
    }

    /**
     * Handle three server SPlayer lists at the end of a turn
     * @param deadP SPlayers that are out of board after this turn
     * @throws Exception
     */
    public void findWinners(List<SPlayer> deadP) throws Exception {
        // game over if board is full
        if (board.isFull()) {
            gameOver = true;
            if (inSPlayers.size() == deadP.size()) {
                inSPlayers.clear();
                outSPlayers.addAll(deadP);
                winners.addAll(deadP);
                returnHandToDeck(deadP);
            } else {
                outSPlayers.addAll(deadP);
                returnHandToDeck(deadP);
                eliminateSPlayers(deadP);
                winners.addAll(inSPlayers);
                drawAndPassDragon();
            }
        }
        // game over if all remaining SPlayers are eliminated at this round
        else if (inSPlayers.size() == deadP.size()) {
            gameOver = true;
            inSPlayers.clear();
            outSPlayers.addAll(deadP);
            winners.addAll(deadP);
            returnHandToDeck(deadP);
        }
        // game over if only one SPlayer remains
        else if ((inSPlayers.size() - deadP.size()) == 1) {
            gameOver = true;
            outSPlayers.addAll(deadP);
            returnHandToDeck(deadP);
            eliminateSPlayers(deadP);
            winners.addAll(inSPlayers);
            drawAndPassDragon();
        }
    }

    /**
     * Handle elimination mechanism of a server player
     * @param deadSPlayers the players to eliminate from the game
     */
    private void eliminateSPlayers(List<SPlayer> deadSPlayers) throws Exception {
        for (SPlayer deadP : deadSPlayers) {
            int pi = Integer.MAX_VALUE;
            for (SPlayer inSPlayer : inSPlayers) {
                if (deadP.isSameSPlayer(inSPlayer)) {
                    pi = this.inSPlayers.indexOf(inSPlayer);
                }
            }
            if (pi == Integer.MAX_VALUE) {
                throw new Exception("Cannot eliminate player");
            }
            SPlayer sp = inSPlayers.get(pi);
            // assign dragon holder to be the next player
            if (sp.equals(dragonHolder)) {
                int index = findNextHolder(pi);
                dragonHolder = index == -1 ? null : inSPlayers.get(index);
            }
            inSPlayers.remove(pi);
            // System.out.println("Player " + sp.getName() + " eliminated!");
        }
    }

    /**
     * Return a list of server player's hand back to deck
     * @param deadSPlayers the players to return their hand
     */
    private void returnHandToDeck(List<SPlayer> deadSPlayers) {
        for (SPlayer deadSPlayer : deadSPlayers) {
            drawPile.addAndShuffle(deadSPlayer.getHand());
            deadSPlayer.getHand().clear();
        }
    }

    /**
     * Handle cases when player cheats and server blames player
     * 1) Player chooses an illegal starting position to place pawn
     * 2) Player chooses an illegal tile to play a turn
     * Cheating player is replaced with a MPlayer with Random strategy
     */
    public void playerCheatIllegalPawn(SPlayer p) throws Exception {
        replaceWithMPlayer(p);
        p.updateToken(p.getPlayer().placePawn(board));
    }

    public Tile playerCheatIllegalTile(SPlayer p) throws Exception {
        replaceWithMPlayer(p);
        p.getMPlayer().state = MPlayer.State.PLAY;
        return p.getPlayer().playTurn(board, p.getHand(), drawPile.size());
    }

    public void replaceWithMPlayer(SPlayer p) throws Exception {
        System.out.println("Player " + p.getPlayer().getName() + " cheated and is replaced by a random machine player");
        MPlayer newPlayer = new MPlayer(MPlayer.Strategy.R, p.getPlayer().getName());
        newPlayer.initialize(p.getToken().getColor(), colors);
        p.linkPlayer(newPlayer);
    }

    /**
     * Check whether player's hand is legal against a set of behavior contracts
     * @param p current player
     */
    private void checkLegalHand(SPlayer p) {
        List<Tile> hand = p.getHand();
        if (hand == null ||hand.size() == 0) {
            return;
        }
        // no more than three tiles
        else if (hand.size() > 3) {
            throw new IllegalArgumentException("Player's hand illegal: more than 3 tiles on hand");
        }
        List<Tile> inHands = new ArrayList<>();
        for (SPlayer player : inSPlayers) {
            inHands.addAll(player.getHand());
        }
        for (Tile playerTile : hand) {
            // not already on board
            if (board.containsTile(playerTile)) {
                throw new IllegalArgumentException("Player's hand illegal: tile exists on board");
            }
            // not in the current draw pile
            if (drawPile.containsTile(playerTile)) {
                throw new IllegalArgumentException("Player's hand illegal: tile exists in draw pile");
            }
            // not in other player's hand or the current player's hand does not contain duplicate tiles
            int count = 0;
            for (Tile t : inHands) {
                if (t.isSameTile(playerTile)) {
                    count++;
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
     * Check whether sp has dragon
     * @param sp the SPlayer to be checked
     * @return true if this sp has dragon
     */
    public boolean hasDragon(SPlayer sp) {
        return (dragonHolder != null && dragonHolder.isSameSPlayer(sp));
    }

    /**
     * Assign dragon tile to a player
     * @param sp the player to assign to
     */
    public void giveDragon(SPlayer sp) {
        if (dragonHolder == null) {
            dragonHolder = sp;
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
        int index = inSPlayers.indexOf(dragonHolder);
        while (!drawPile.isEmpty()) {
            dragonHolder.getHand().add(drawPile.pop());
            // if game over, dragon holder tries to draw until full hand
            if (gameOver) {
                while (drawPile.size() > 0 && dragonHolder.getHand().size() < 3) {
                    dragonHolder.getHand().add(drawPile.pop());
                }
            }
            index = findNextHolder(index);
            // cannot find next player with < 3 tiles on his/her hand
            if (index == -1) {
                dragonHolder = null;
                return;
            }
            dragonHolder = inSPlayers.get(index);
        }
    }

    /**
     * Find the index of next player with < 3 tiles on hand
     * @param index index of current dragon holder
     * @return the index of next player with < 3 tiles on hand
     */
    public int findNextHolder(int index) {
        int i = 0;
        while (i < inSPlayers.size() - 1) {
            index = (index + 1) % inSPlayers.size();
            if (inSPlayers.get(index).getHand().size() < 3) {
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
        for (SPlayer sPlayer : inSPlayers) {
            colors.add(sPlayer.getToken().getColor());
        }
        return colors;
    }

    /**
     * Start a tournament over the network with a remote player
     * @throws Exception
     */
    public void startGame() throws Exception {
        ServerSocket socketListener = new ServerSocket(PORT_NUM);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // create players
            // for remote players, initialize a new socket
            IPlayer rP = new RemotePlayer(socketListener.accept(), db);
            IPlayer mP1 = new MPlayer(MPlayer.Strategy.R, "MPlayer 1");
            IPlayer mP2 = new MPlayer(MPlayer.Strategy.LS, "MPlayer 2");
            IPlayer mP3 = new MPlayer(MPlayer.Strategy.MS, "MPlayer 3");

            for (int i = 0; i < 4; i++) {
                colors.add(i);
            }

            mP1.initialize(1, colors);
            mP2.initialize(2, colors);
            mP3.initialize(3, colors);
            rP.initialize(0, colors);

            Token t1 = mP1.placePawn(board);
            server.registerPlayer(mP1, t1);
            Token t2 = mP2.placePawn(board);
            server.registerPlayer(mP2, t2);
            Token t3 = mP3.placePawn(board);
            server.registerPlayer(mP3, t3);
            Token t0 = rP.placePawn(board);
            server.registerPlayer(rP, t0);

            // play game over network
            while(!server.isGameOver()) {
                SPlayer currentP = inSPlayers.get(0);
                System.out.println("Server: current player = " + currentP.getPlayer().getName());
                Tile tileToPlay = currentP.getPlayer().playTurn(board, currentP.getHand(), drawPile.size());
                currentP.deal(tileToPlay);
                server.playATurn(tileToPlay);
            }

            // prints
            List<Integer> winnerColors = server.getCurrentColors();
            for (SPlayer sPlayer : winners) {
                System.out.println("Server: ending game for winners = " + sPlayer.getPlayer().getName());
                sPlayer.getPlayer().endGame(server.board, winnerColors);
            }
            for (SPlayer sPlayer : outSPlayers) {
                System.out.println("Server: ending game for losers = " + sPlayer.getPlayer().getName());
                sPlayer.getPlayer().endGame(server.board, winnerColors);
            }
            System.out.println("Server: game over? = " + server.gameOver);
            for (SPlayer sPlayer : server.winners) {
                System.out.println("Server: winner = " + sPlayer.getPlayer().getName());
            }

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } finally {
            // close connection
            socketListener.close();
        }
    }
}


