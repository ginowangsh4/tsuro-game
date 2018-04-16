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

    /*
        Return false if
        1) the tile is not (a possibly rotated version of) one of the tiles of the player
        2) the placement of the tile is an elimination move for the player (unless all of
        the possible moves are elimination moves)
     */
    public boolean legalPlay(Player p, Board b, Tile t) {

        // this.board = b <- do we need this?
        // this.inPlayer.add(p) <- do we need this?
        for (Tile playerTile : p.hand) {
            if (!t.isSameTile(playerTile)) return false;
        }

        int[] resultPosn = new int[2]; // initializing, update in simulateMove()
        int resultOrientationInt = 0; // initializing, update in simulateMove()
        simulateMove(p.token, t, resultPosn, resultOrientationInt);

        String side = t.getSide(resultOrientationInt);
        if (side == "T" && resultPosn[1] == 0 || side == "R" && resultPosn[0] == 5 ||
                side == "B" && resultPosn[1] == 5 || side == "L" && resultPosn[0] == 0) {
            return false;
        }
        return true;
    }


    /* Moved this function to Tile Class, see Tile.getSide()

        private String findSide(int[] result, int index) {
            Tile tile = this.board.getTile(result[0], result[1]);
            int i = 0;
            for (int[] array: tile.orientations)
            {
                if (index == array[0] || index == array[1]) break;
                i++;
            }
            if (i == 0) return "TOP";
            else if (i == 1) return "RIGHT";
            else if (i == 2) return "BOTTOM";
            else return "LEFT";
        }
    */


    // Simulate a move using recursion
    private void simulateMove(Token current, Tile tileToPlace, int[] posn, int orientationInt) {

        if (tileToPlace == null &&
                board.getTile(lookAhead(current)[0], lookAhead(current)[1]) == null) posn = posn; //stop when no tile to place and no tile in front
        else
        {
            Tile currentTile = board.getTile(current.getPosition()[0], current.getPosition()[1]);
            String adjacentSide = getAdjacentSide(currentTile.getSide(current.getEndPint()));
            int index;
            if (current.getEndPint()%2 == 0) index = 0; // even number is the first element
            else index = 1;                             // odd number is the second element
            int startPathInt = tileToPlace.getPathInt(adjacentSide, index);
            int endPathInt = tileToPlace.getPathEndInt(startPathInt);

            orientationInt = tileToPlace.pathToOrient(endPathInt); //new endpoint of the token
            posn = lookAhead(current);                                    //new position of the token

            Token next = new Token(orientationInt, posn);              // 'fake' token with no player and color assigned
            simulateMove(next, board.getTile(lookAhead(current)[0], lookAhead(current)[1]), posn, orientationInt); // see if the path continues
        }
    }

    // Return the side that is adjacent to inSide.
    public String getAdjacentSide (String inSide){

        if (inSide.equals("T")) return "B";
        else if (inSide.equals("R")) return "L";
        else if (inSide.equals("B")) return "T";
        else return "R";
    }

    /*
        Token looks ahead according to its own position.
        Returns the [x,y] position of the block in front of it.
    */

    private int[] lookAhead(Token token){

        int[] next = new int[2];

        int currentX = token.getPosition()[0];
        int currentY = token.getPosition()[1];
        Tile currentTile = board.getTile(currentX, currentY);
        String currentSide = currentTile.getSide(token.getEndPint());

        if (currentSide.equals('T'))
        {
            next[0] = currentX;
            next[1] = currentY - 1;
        }
        else if (currentSide.equals('R'))
        {
            next[0] = currentX + 1;
            next[1] = currentY;
        }
        else if (currentSide.equals('B'))
        {
            next[0] = currentX;
            next[1] = currentY + 1;
        }
        else
        {
            next[0] = currentX - 1;
            next[1] = currentY;
        }

        return next;

    }

}
