import com.sun.deploy.util.ArrayUtil;
import com.sun.tools.javac.util.ArrayUtils;

import java.util.*;

public class Board {
    protected Tile[][] board;
    protected final int SIZE = 6;
    protected ArrayList<Token> token_list;

    Board() {
        this.board = new Tile[SIZE][SIZE];
        this.token_list = new ArrayList<>();
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return null;
        }
        return this.board[x][y];
    }

    public void placeTile(Tile t, int x, int y) { this.board[x][y] = t;}

    public void deleteTile(int x, int y) { this.board[x][y] = null;}

    public void addToken(Token t){
        this.token_list.add(t);
    }

    public void removeToken(Token inT) {
        for (int i = 0; i < token_list.size(); i++) {
            if (inT.equals(this.token_list.get(i))) {
                this.token_list.remove(i);
            }
        }
    }

    public void updateToken(Token t){
        this.token_list.remove(t);
        this.token_list.add(t);
    }
}
