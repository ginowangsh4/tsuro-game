import java.util.*;

public class Board {
    protected Tile[][] board;
    protected final int SIZE = 6;
    protected HashMap<Player, int[]> playerMap; // [x of location, y of location, index on tile at (x,y)]

    Board() {
        this.board = new Tile[SIZE][SIZE];
        this.playerMap = new HashMap<>();
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return null;
        }
        return this.board[x][y];
    }

    public void placeTile(Tile t, int x, int y) { this.board[x][y] = t;}

    public void deleteTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return;
        }
        this.board[x][y] = null;
    }

    public HashMap<Player, int[]> getPlayerMap() { return this.playerMap; }
}
