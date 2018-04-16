import java.util.*;

public class Board {
    protected Tile[][] board;

    Board() {
        this.board = new Tile[6][6];
    }

    public Tile getTile(int x, int y) {
        return this.board[x][y];
    }

}
