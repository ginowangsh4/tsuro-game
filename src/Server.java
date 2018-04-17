import java.util.*;

public class Server {

    private Board board;
    private ArrayList<Tile> drawPile;
    private ArrayList<Player> inPlayer;
    private ArrayList<Player> outPlayer;
//    private ArrayList<Player> winners;
//    private boolean isOver;

//    Server(Board b, ArrayList<Tile> drawPile, ArrayList<Player> inPlayer, ArrayList<Player> outPlayer) {
//        this.board = b;
//        this.drawPile = drawPile;
//        this.inPlayer = inPlayer;
//        this.outPlayer = outPlayer;
//    }

    // Singleton Pattern
    public static Server server = new Server();
    private Server() {}

    /**
     * Return false if
     * 1) the tile is not (a possibly rotated version of) one of the tiles of the player
     * 2) the placement of the tile is an elimination move for the player (unless all of
     * the possible moves are elimination moves
     * @param p         the player that attempts to place a tile
     * @param b         the board before the tile placement
     * @param t         the tile that the player wishes to place on the board
     * @return true if this play is legal
     */
    public boolean legalPlay(Player p, Board b, Tile t) {
        // check condition 1
        for (Tile pt : p.getHand()) {
            if (!t.isSameTile(pt)) {
                return false;
            }
            else break;
        }
        // check condition 2
        Token token = simulateMove(p.getToken(), t, b);
        if (!outOfBoard(token)){
            return true;          // original rotation is legal without considering other rotations
        }
        else {
            Tile newTile = new Tile(t.getPaths());
            for (int i =0; i < 3; i++){
                newTile.rotateTile();
                token = simulateMove(p.getToken(), newTile, b);
                if (!outOfBoard(token)) return false; // original rotation is illegal, as there is another legal rotation
            }
            if (p.getHand().size() > 1){
                for (Tile pt: p.getHand()){
                    if (!t.isSameTile(pt)){
                        newTile = new Tile(pt.getPaths());
                        for (int i = 0; i < 4; i++) {
                            newTile.rotateTile();
                            token = simulateMove(p.getToken(), newTile, b);
                            if (!outOfBoard(token)) return false;   // original rotation is illegal, as there is another legal move
                        }
                    }
                }
            }
            else return true;     // original rotation is legal as 1. only one tile in player's hand 2. all rotations of this tile leads to elimination
        }
        return true;
    }

    public ArrayList playATurn(ArrayList<Tile> drawPile, ArrayList<Player> inPlayer, ArrayList<Player> outPlayer, Board board, Tile t) {
        // place a tile path
        Player currentP = inPlayer.get(0);
        inPlayer.remove(0);
        Token currentT = currentP.getToken();
        int[] location = getAdjacentLocation(currentT);
        b.placeTile(t, location[0], location[1]);

        // move the token
        Token tempT = simulateMove(currentT, null, b);
        currentT.setIndex(tempT.getIndex());
        currentT.setPosition(tempT.getPosition());

        // update player map on board -> physically move the token forward to the final location and index
        currentP.setToken(currentT);
        b.getPlayerMap().put(currentP, new int[] {currentT.getPosition()[0], currentT.getPosition()[1], currentT.getIndex()});
        // eliminate current player & recycle tiles in hand
        if (outOfBoard(currentT)) {
            drawPile.addAll(currentP.getHand());
            currentP.getHand().clear();
            outPlayer.add(currentP);
        }
        // add to tail & draw tile
        else {
            if (drawPile.size() == 0) {
                currentP.getDragon();
            }
            Tile temp = drawPile.get(0);
            currentP.draw(temp);
            drawPile.remove(0);
            inPlayer.add(currentP);
        }

        for (int i = 0; i < inPlayer.size()-1; i ++)
        {
            currentP = inPlayer.get(i);
            currentT = currentP.getToken();
            tempT = simulateMove(currentT, null, b);
            currentT.setPosition(tempT.getPosition());
            currentT.setIndex(tempT.getIndex());
            currentP.setToken(currentT);

            if (outOfBoard(currentT)) {
                drawPile.addAll(currentP.getHand());
                currentP.getHand().clear();
                outPlayer.add(currentP);
            }
        }

        //update the state of the game
        this.board = board;
        this.drawPile = drawPile;
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;

        // determine whether game is over
        if (inPlayer.size() == 0) return null;
        else return inPlayer;
    }

    private Token simulateMove(Token token, Tile nextTile, Board board) {
        // next location the token can go on
        int[] location = getAdjacentLocation(token);
        // return if reach the end of path
        if (nextTile == null && board.getTile(location[0], location[1]) == null) {
            return token;
        }
        // simulate moving token & get new token index
        int pathStart = nextTile.neighborIndex.get(token.getIndex());
        int pathEnd = nextTile.getPathEnd(pathStart);
        // recursion
        Token nt = new Token(pathEnd, location);
        int[] nl = getAdjacentLocation(nt);
        return simulateMove(nt, board.getTile(nl[0], nl[1]), board);
    }

    /**
     * Check whether a player's token is on tile at the location and the index
     * @param b
     * @param location
     * @param indexOnTile
     * @return whether a player's token is on the tile at input location
     *         and at the index on the tile
     */
    private boolean hasToken(Board b, int[] location, int indexOnTile) {
        for (Player p : b.getPlayerMap().keySet()) {
            int[] temp = b.getPlayerMap().get(p);
            if (location[0] == temp[0] && location[1] == temp[1] && location[2] == indexOnTile) {
                return true;
            }
        }
        return false;
    }

    /**
     * Token looks ahead according to its own position
     * @param token the token of player currently making the move
     * @return array of location [x,y] of the block ahead
     */
    private int[] getAdjacentLocation(Token token){
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
        } else if (indexOnTile == 4 || indexOnTile == 7) {
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
     * so we need to eliminate the player holding that token
     * @param token
     * @return
     */
    public boolean outOfBoard(Token token){
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

    /* Test */
    static Board b;
    static Token token;
    static Player p;
    static Tile tile;

    // Legal 1: place a tile, not tile around it on board
    static public void createExample1() {
        b = new Board();
        token = new Token(0, 4, new int[] {0,0});
        tile = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // Legal 2: place a tile, move to some tile not at edge
    static public void createExample2() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        Tile tile2= new Tile(new int[][] {{0,5}, {1,2}, {3,6}, {4,7}});
        b.placeTile(tile1, 0, 0);
        b.placeTile(tile2,1,1);
        tile = new Tile(new int[][] {{0,5}, {1,2}, {3,4}, {6,7}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // Legal 3: place a tile, all rotation leads to elimination
    static public void createExample3() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // Legal 4: all tiles at hand lead to elimination
    static public void createExample4() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,4}, {1,5}, {2,6}, {3,7}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
    }

    // Illegal 1: tile to be placed is not in player's hand
    static public void createExample5() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile1);
    }

    // Illegal 2: this rotation of the tile leads to elimination while others do not
    static public void createExample6() {
        b = new Board();
        token = new Token(0, 4,new int[] {0,0});
        Tile tile1 = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        b.placeTile(tile1, 0, 0);
        tile = new Tile(new int[][] {{0,2}, {1,7}, {3,4}, {5,6}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
    }

    // Illegal 3: all rotation of this tile leads to elimination but other tiles do not
    static public void createExample7() {
        b = new Board();
        token = new Token(0, 1,new int[] {0,1});
        Tile tile1 = new Tile(new int[][] {{0,7}, {1,4}, {2,5}, {3,6}});
        b.placeTile(tile1, 0, 1);
        tile = new Tile(new int[][] {{0,5}, {1,4}, {2,7}, {3,6}});
        Tile tile2 = new Tile(new int[][] {{0,3}, {1,4}, {2,7}, {5,6}});
        ArrayList<Tile> hand = new ArrayList<>();
        p = new Player(token, hand);
        p.draw(tile);
        p.draw(tile2);
    }

    public static void main(String argv[]) {
        createExample1();
        Tester.check(server.legalPlay(p, b, tile) == true, "Legal 1");
        createExample2();
        Tester.check(server.legalPlay(p, b, tile) == true, "Legal 2");
        createExample3();
        Tester.check(server.legalPlay(p, b, tile) == true, "Legal 3");
        createExample4();
        Tester.check(server.legalPlay(p, b, tile) == true, "Legal 4");
        createExample5();
        Tester.check(server.legalPlay(p, b, tile) == false, "Illegal 2");
        createExample6();
        Tester.check(server.legalPlay(p, b, tile) == false, "Illegal 3");
        createExample7();
        Tester.check(server.legalPlay(p, b, tile) == false, "Illegal 3");
    }
}
