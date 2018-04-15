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
        for (Tile playerTile : p.hand) {
            if (!checkRotation(playerTile, t)) return false;
        }
        int[] result = new int[2]; // (x,y) of final tile on board
        int finalTileIndex;        // index on final tile
        String finalTileSide;         // side index on final tile
        finalTileIndex = forwardPath(p, t, result);
        finalTileSide = findSide(result, finalTileIndex);
        if (finalTileSide == "TOP" && result[1] == 0 || finalTileSide == "RIGHT" && result[0] == 5 ||
                finalTileSide == "BOTTOM" && result[1] == 5 || finalTileSide == "LEFT" && result[0] == 0) {
            return false;
        }
        return true;
    }

    private boolean checkRotation(Tile real, Tile expected) {
        return (real.paths.equals(expected.paths)) ? true : false;
    }

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

    private int forwardPath(Player p, Tile t, int[] result) {
        int[] current = p.token.getPosition();
        int endPoint = p.token.getEndPint();
        int[] next = new int[2];
        int i = 0;
        for (int[] array: t.orientations)
        {
            if (endPoint == array[0] || endPoint == array[1]) break;
            i++;
        }
        if (i == 0)
        {
            next[0] = current[0];
            next[1] = current[1] - 1;
        }
        else if (i == 1)
        {
            next[0] = current[0] + 1;
            next[1] = current[1];
        }
        else if (i == 2)
        {
            next[0] = current[0];
            next[1] = current[1] + 1;
        }
        else
        {
            next[0] = current[0] - 1;
            next[1] = current[1];
        }

        int startPoint;
    }


}
