import java.util.*;

public class Server {
    // singleton
    public static Server server = new Server();
    private Server() {}

    private Board board;
    private ArrayList<Tile> drawPile;
    private ArrayList<Player> inPlayer;
    private ArrayList<Player> outPlayer;

    Server(Board b, ArrayList<Tile> drawPile, ArrayList<Player> inPlayer, ArrayList<Player> outPlayer) {
        this.board = b;
        this.drawPile = drawPile;
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
    }

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
        for (Tile playerTile : p.hand) {
            if (!t.isSameTile(playerTile)) {
                return false;
            }
        }
        int[] resultPosn = new int[2];  // initializing, update in simulateMove()
        int resultOrientationInt = 0;   // initializing, update in simulateMove()
        simulateMove(p.token, t, resultPosn, resultOrientationInt);
        String side = t.getSide(resultOrientationInt);
        if (side == "T" && resultPosn[1] == 0 || side == "R" && resultPosn[0] == 5 ||
                side == "B" && resultPosn[1] == 5 || side == "L" && resultPosn[0] == 0) {
            return false;
        }
        return true;
    }

    /**
     * Simulate a move by a player
     * @param current        token of the player
     * @param tileToPlace    tile player attempt to place
     * @param position           to be filled with position of final tile
     * @param orientationInt ???
     */
    private void simulateMove(Token current, Tile tileToPlace, int[] position, int orientationInt) {

        if (tileToPlace == null && board.getTile(lookAhead(current)[0], lookAhead(current)[1]) == null) {
            position = position; //stop when no tile to place and no tile in front
            return;
        }

        int[] currPosition = current.getPosition();
        Tile currentTile = board.getTile(currPosition[0], currPosition[1]);

        String aSide = getAdjacentSide(currentTile.getSide(current.getEndPint()));
        int index;
        if (current.getEndPint() % 2 == 0) {
            index = 0;  // even number is the first element
        } else {
            index = 1;  // odd number is the second element
        }
        int startPathInt = tileToPlace.getPathInt(aSide, index);
        int endPathInt = tileToPlace.getPathEndInt(startPathInt);

        orientationInt = tileToPlace.pathToOrient(endPathInt); //new endpoint of the token
        position = lookAhead(current);                         //new position of the token
        // 'fake' token with no player and color assigned
        Token next = new Token(orientationInt, position);
        // see if the path continue
        simulateMove(next, board.getTile(lookAhead(current)[0], lookAhead(current)[1]), position, orientationInt);
    }

    /**
     * Return the side that is adjacent to current tile
     * @param inSide    the side of tile at current board location
     * @return a string indicating the side of adjacent tile
     */
    public String getAdjacentSide (String inSide){

        if (inSide.equals("T")) return "B";
        else if (inSide.equals("R")) return "L";
        else if (inSide.equals("B")) return "T";
        else return "R";
    }

    /**
     * Token looks ahead according to its own position
     * @param token the token of player currently making the move
     * @return array of location [x,y] of the block ahead
     */
    private int[] lookAhead(Token token){

        int[] next = new int[2];
        int currentX = token.getPosition()[0];
        int currentY = token.getPosition()[1];
        Tile currentTile = board.getTile(currentX, currentY);
        String currentSide = currentTile.getSide(token.getEndPint());

        if (currentSide.equals('T')) {
            next[0] = currentX;
            next[1] = currentY - 1;
        }
        else if (currentSide.equals('R')) {
            next[0] = currentX + 1;
            next[1] = currentY;
        }
        else if (currentSide.equals('B')) {
            next[0] = currentX;
            next[1] = currentY + 1;
        }
        else {
            next[0] = currentX - 1;
            next[1] = currentY;
        }
        return next;
    }
}
