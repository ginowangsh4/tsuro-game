import java.util.*;

public class Board {
    protected Tile[][] board;
    protected final int SIZE = 6;
    Board() {
        this.board = new Tile[SIZE][SIZE];
    }

    public Tile getTile(int x, int y) {
        return this.board[x][y];
    }

    public void placeTile(Tile t, int x, int y) { this.board[x][y] = t;}

    public void deleteTile(int x, int y) {
        this.board[x][y] = null;
    } //illegal actually
}
