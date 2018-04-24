import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.*;

public class Server {

    private Board board;
    private List<Tile> drawPile;
    private List<Player> inPlayer;
    private List<Player> outPlayer;

    private boolean gameOver = false;

    // Singleton Pattern
    private static Server server = new Server();
    private Server() {}
    public static Server getServer() { return server; }
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
            Tile newTile = new Tile(t.getPaths());
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
                        newTile = new Tile(pt.getPaths());
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
     * @param drawPile   the list of tiles on the draw pile
     * @param inPlayer   the list of players still in the game in the order of play
     * @param outPlayer  the list of eliminated players in no particular order
     * @param board      the board before the turn
     * @param t          the tile to be placed on that board
     * @return the list of winner if the gmae is over; otherwise return null
     *         (drawPile, inPlayer, outPlayer are themselves updated and updated in server's status through private fields)
     */
    public List<Player> playATurn(List<Tile> drawPile, List<Player> inPlayer, List<Player> outPlayer, Board board, Tile t) {
        // place a tile path
        Player currentP = inPlayer.get(0);
        inPlayer.remove(0);
        Token currentT = currentP.getToken();
        int[] location = getAdjacentLocation(currentT);
        board.placeTile(t, location[0], location[1]);
        // move the token
        Token tempT = simulateMove(currentT, b);
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
        // update the state of the game
        this.board = board;
        this.drawPile = drawPile;
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
        // determine whether game is over
        if (this.inPlayer.size() == 1) {
            this.gameOver = true;
            return inPlayer;
        }
        else if (this.inPlayer.size() == 0) {
            this.gameOver = true;
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

    /******************************/
    /*********** Tests ************/
    /******************************/
    static Board b;
    static Token token;
    static Player p;
    static Tile tile;
    static List<Tile> pile;
    static List<Player> inPlayerList;
    static List<Player> outPlayerList;

    // legalPlay - Expect Legal - Test 1: place a tile, not tile around it on board
    static public void createExample1() {
        b = new Board();
        token = new Token(0, 4, new int[]{0, 0});
        tile = new Tile(new int[][]{{0, 7}, {1, 4}, {2, 5}, {3, 6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);

    }

    // legalPlay - Expect Legal - Test 2: place a tile, move to some tile not at edge
    static public void createExample2() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        Tile tile2= new Tile(new int[][] {{0,5}, {1,2}, {3,6}, {4,7}});
        b.placeTile(tile1, 0, 0);
        b.placeTile(tile2,1,1);
        tile = new Tile(new int[][] {{0,5}, {1,2}, {3,4}, {6,7}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // legalPlay - Expect Legal - Test 3: place a tile, all rotation leads to elimination
    static public void createExample3() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // legalPlay - Expect Legal - Test 4: all tiles at hand lead to elimination
    static public void createExample4() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,4}, {1,5}, {2,6}, {3,7}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
    }

    // legalPlay - Expect Illegal - Test 5: tile to be placed is not in player's hand
    static public void createExample5() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile1);
    }

    // legalPlay - Expect Illegal - Test 6: this rotation of the tile leads to elimination while others do not
    static public void createExample6() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 0, 0);
        tile = new Tile(new int[][] {{0,2}, {1,7}, {3,4}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // legalPlay - Expect Illegal - Test 7: all rotation of this tile leads to elimination but other tiles do not
    static public void createExample7() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        List<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
    }

    // playATurn - Expect Game Over - Test 1: Player 1 and Player 2 move off board and both gets eliminated
    static public void createExample8() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        Tile tile2 = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        tile = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile2, 2, 1);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 2,new int[] {1,2});
        Token token2 = new Token(1, 5,new int[] {2,1});
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        token1.setOwner(player1);
        token2.setOwner(player2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayerList = new ArrayList<>();
        outPlayerList = new ArrayList<>();
        pile = new ArrayList<>();
        inPlayerList.add(player1);
        inPlayerList.add(player2);
    }

    // playATurn - Expect Game Not Over - Test 2: Player 1 and Player 2 move to a non-edge tile
    static public void createExample9() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        tile = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        Tile tile3 = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile3, 2, 2);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 1,new int[] {2,2});
        Token token2 = new Token(1, 5,new int[] {2,0});
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        token1.setOwner(player1);
        token2.setOwner(player2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayerList = new ArrayList<>();
        outPlayerList = new ArrayList<>();
        pile = new ArrayList<>();
        inPlayerList.add(player1);
        inPlayerList.add(player2);
    }

    // playATurn - Expect Game Not Over - Test 2: Player 1 and Player 2 move to a non-edge tile
    static public void createExample10() {
        b = new Board();
        Tile tile1 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        Tile tile2 = new Tile(new int[][] {{0,7}, {1,5}, {2,6}, {3,4}});
        tile = new Tile(new int[][] {{0,7}, {1,2}, {3,4}, {5,6}});
        Tile tile4 = new Tile(new int[][] {{0,4}, {1,5}, {2,7}, {3,6}});
        Tile tile5 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 2, 0);
        b.placeTile(tile2, 2, 1);
        b.placeTile(tile4, 1, 2);
        b.placeTile(tile5, 0, 2);
        Token token1 = new Token(0, 3,new int[] {1,2});
        Token token2 = new Token(1, 5,new int[] {2,1});
        List<Tile> hand1 = new ArrayList<>();
        List<Tile> hand2 = new ArrayList<>();
        Player player1 = new Player(token1, hand1);
        Player player2 = new Player(token2, hand2);
        token1.setOwner(player1);
        token2.setOwner(player2);
        b.addToken(token1);
        b.addToken(token2);

        inPlayerList = new ArrayList<>();
        outPlayerList = new ArrayList<>();
        pile = new ArrayList<>();
        inPlayerList.add(player1);
        inPlayerList.add(player2);
    }

    public static void main(String argv[]) {
        createExample1();
        Tester.check(server.legalPlay(p, b, tile) == true, "legalPlay - Expect Legal - Test 1");

        createExample2();
        Tester.check(server.legalPlay(p, b, tile) == true, "legalPlay - Expect Legal - Test 2");

        createExample3();
        Tester.check(server.legalPlay(p, b, tile) == true, "legalPlay - Expect Legal - Test 3");

        createExample4();
        Tester.check(server.legalPlay(p, b, tile) == true, "legalPlay - Expect Legal - Test 4");

        createExample5();
        Tester.check(server.legalPlay(p, b, tile) == false, "legalPlay - Expect Illegal - Test 5");

        createExample6();
        Tester.check(server.legalPlay(p, b, tile) == false, "legalPlay - Expect Illegal - Test 6");

        createExample7();
        Tester.check(server.legalPlay(p, b, tile) == false, "legalPlay - Expect Illegal - Test 7");

        createExample8();
        Tester.check(server.playATurn(pile, inPlayerList, outPlayerList, b, tile) == null, "PlayATurn - Expect Game Over - Test 1");
        Tester.check(server.gameOver == true, "check game status");
        Tester.check(server.inPlayer.size() == 0, "check inPlayer list");
        Tester.check(server.outPlayer.size() == 2, "check outPlayer list");
        Tester.check(Arrays.equals(server.outPlayer.get(0).getToken().getPosition(), new int[] {2,0}), "check player 1 token position");
        Tester.check(server.outPlayer.get(0).getToken().getIndex() == 1, "check player 1 token index");
        Tester.check(Arrays.equals(server.outPlayer.get(1).getToken().getPosition(), new int[] {0,2}), "check player 2 token position");
        Tester.check(server.outPlayer.get(1).getToken().getIndex() == 7, "check player 2 token index");
        server.gameOver = false;

        createExample9();
        Tester.check(server.playATurn(pile, inPlayerList, outPlayerList, b, tile) == null, "PlayATurn - Expect Game Not Over - Test 2");
        Tester.check(server.gameOver == false, "check game status");
        Tester.check(server.inPlayer.size() == 2, "check inPlayer list");
        Tester.check(server.outPlayer.size() == 0, "check outPlayer list");
        Tester.check(Arrays.equals(server.inPlayer.get(0).getToken().getPosition(), new int[] {2,1}), "check player 1 token position");
        Tester.check(server.inPlayer.get(1).getToken().getIndex() == 3, "check player 1 token index");
        Tester.check(Arrays.equals(server.inPlayer.get(1).getToken().getPosition(), new int[] {2,1}), "check player 2 token position");
        Tester.check(server.inPlayer.get(0).getToken().getIndex() == 7, "check player 2 token index");

        createExample10();
        Tester.check(server.playATurn(pile, inPlayerList, outPlayerList, b, tile).equals(server.inPlayer), "PlayATurn - Expect Game Over - Test 3");
        Tester.check(server.gameOver == true, "check game status");
        Tester.check(server.inPlayer.size() == 1, "check inPlayer list");
        Tester.check(server.outPlayer.size() == 1, "check outPlayer list");
        Tester.check(Arrays.equals(server.inPlayer.get(0).getToken().getPosition(), new int[] {2,2}), "check player 1 token position");
        Tester.check(server.inPlayer.get(0).getToken().getIndex() == 5, "check player 1 token index");
        Tester.check(Arrays.equals(server.outPlayer.get(0).getToken().getPosition(), new int[] {0,2}), "check player 2 token position");
        Tester.check(server.outPlayer.get(0).getToken().getIndex() == 7, "check player 2 token index");
    }
}
