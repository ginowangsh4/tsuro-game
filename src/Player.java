import java.util.*;

// Not complete
// More test cases need to be build to confirm that all things work as expected
public class Player {
    private Token token;
    private List<Tile> hand;
    private String name;
    // list of player's colors in the order that the game will be played
    private List<Integer> colors;
    private boolean isWinner;

    Player(Token t, List<Tile> hand) {
        this.token = t;
        this.hand = hand;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Called to indicate a game is starting.
     * @param color the player's color
     * @param colors all of the players'colors, in the order that the game will be played.
     */
    public void initialize(int color, List<Integer> colors) {
        this.token = new Token(color);
        this.colors = colors;
    }


    /**
     * Check if a player has this input tile on hand
     * @param tile to be checked
     * @return true if play has this tile
     */
    public boolean hasTile(Tile tile) {
        for (Tile t : getHand()) {
            if (t.isSameTile(tile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a player's token
     *
     * @return a token
     */
    public Token getToken() {
        return this.token;
    }

    /**
     * Update a player's token
     *
     * @param token new token
     */
    public void updateToken(Token token) {
        this.token = token;
    }

    /**
     * Player draws a tile
     *
     * @param t tile to be added to the player's hand
     */
    public void draw(Tile t) {
        hand.add(t);
    }

    /**
     * Simulate player choosing a tile to place
     *
     * @param t tile to be placed
     */
    public void deal(Tile t) {
        hand.remove(t);
    }

    /**
     * Get a player's hand
     *
     * @return a list of tiles on player's hand
     */
    public List<Tile> getHand() {
        return this.hand;
    }

    /**
     * Called at the first step in a game indicates where the player wishes to place their token
     * token must be placed along the edge in an unoccupied space.
     * @param b the current board state
     * @return a token with the player's color, its position [x,y] and index on tile.
     */
    public Token placePawn(Board b) {
        Random rand = new Random();
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int indexOnTile = Integer.MAX_VALUE;
        boolean found = false;
        while (!found) {
            // choose random number in {0,1,2,3}
            int side = rand.nextInt(4);
            // choose random number in {0,1,2,...,11}
            int sideIndex = rand.nextInt(12);
            switch (side) {
                case 0: {
                    x = sideIndex / 2;
                    y = -1;
                    indexOnTile = sideIndex % 2;
                    break;
                }
                case 1: {
                    x = 6;
                    y = sideIndex / 2;
                    indexOnTile = sideIndex % 2 + 2;
                    break;
                }
                case 2: {
                    x = sideIndex / 2;
                    y = 6;
                    indexOnTile = sideIndex % 2 + 4;
                    break;
                }
                case 3: {
                    x = -1;
                    y = sideIndex / 2;
                    indexOnTile = sideIndex % 2 + 6;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Error: Unable to pick starting position on board");
                }
            }
            found = true;
            for (Token t : b.getTokenList()) {
                if (x == t.getPosition()[0] && y == t.getPosition()[1] && indexOnTile == t.getIndex()) {
                    found = false;
                    break;
                }
            }
        }
        if (x == Integer.MAX_VALUE || y == Integer.MAX_VALUE || indexOnTile == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Error: Unable to pick starting position on board");
        }
        updateToken(new Token(this.token.getColor(), indexOnTile, new int[]{x, y}));
        b.addToken(this.token);
        return this.token;
    }

    /**
     * Called to inform the player of the final board state and which players won the game.
     * @param b the current board game
     * @param colors the list of winner's colors
     */
    public void endGame(Board b, List<Integer> colors) {
        this.colors = colors;
        if (b.getTokenList().contains(this.token)) {
            this.isWinner = true;
        }
        this.isWinner = false;
    }


    /**
     * Called to ask the player to make a move.
     * @param b the current board state
     * @param strategy the strategy that player plays
     * @param tilesLeft count of tiles that are not yet handed out to players.
     * @return the tile the player should place, suitably rotated.
     */
    public Tile playTurn(Board b, String strategy, int tilesLeft) {
        List<Tile> legalMoves = new ArrayList<>();
        List<Tile> legalTiles = new ArrayList<>();
        for (Tile t : getHand()) {
            Tile copy = t.copyTile();
            for (int i = 0; i < 4; i++) {
                if (Server.getInstance().legalPlay(this, b, copy)) {
                    legalMoves.add(copy.copyTile());
                    if (!legalTiles.contains(t)) {
                        legalTiles.add(t);
                    }
                }
                copy.rotateTile();
            }
        }
        switch (strategy) {
            case "R": {
                Random rand = new Random();
                return legalMoves.get(rand.nextInt(legalMoves.size()));
            }

            case "MS": {
                Collections.sort(legalMoves, new SymmetricComparator());
                return legalMoves.get(0);
            }

            case "LS": {
                Collections.sort(legalMoves, new SymmetricComparator());
                return legalMoves.get(legalMoves.size() - 1);
            }

            default: {
                throw new IllegalArgumentException("Input strategy cannot' be identified");
            }
        }
    }

    private int findFirstMove(List<Tile> moves, Tile tile) {
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).isSameTile(tile)) {
                return i;
            }
        }
        return moves.size();
    }

}

/**
 * The following classes are used with Array.sort
 */
class SymmetricComparator implements Comparator<Tile> {
    @Override
    // a < b return -1
    // a > b return 1
    // else return 0
    // order is from most symmetric to least symmetric
    public int compare(Tile a, Tile b){
        int countA = diffPaths(a);
        int countB = diffPaths(b);
        if (countA == countB){
            return 0;
        }
        return countA < countB ? -1 : 1;
    }

    /**
     * Count the number of ways a given tile can be placed
     * @param t a given tile
     * @return the number of ways it can be placed
     */
    public static int diffPaths(Tile t){
        // make sure every tile has correct path order before be placed on board
        reorderPath(t);
        int count = 1;
        Tile copy = t.copyTile();
        for (int i = 0; i < 3; i++ ){
            copy.rotateTile();
            Tile temp = copy.copyTile();
            reorderPath(temp);
            if (!t.hasSamePaths(temp)) count++;
        }
        // if we have count = 3, it means we have two pathways that are different from the original
        // but these two pathways must be the same
        // so we need to subtract count by 1 to get the number of ways it can be placed.
        // count can only be 1, 2, or 4
        return count == 3 ? count - 1 : count;
    }

    /**
     * Reorder the path arrays of a given tile; path indices ordered from smallest to largest
     * Doesn't modify the actual path
     * i.e. {{2,6}, {3, 7}, {4, 1}, {5, 0}} becomes {{0,5}, {1, 4}, {2, 6}, {3, 7}}
     * @param t a given tile
     */
    public static void reorderPath(Tile t) {
        for (int[] array : t.paths) {
            Arrays.sort(array);
        }
        Arrays.sort(t.paths, new ListFirstElementComparator());
    }
}

class ListFirstElementComparator implements Comparator<int[]> {
    @Override
    public int compare(int[] a, int[] b) {
        if (a[0] == b[0]) {
            return 0;
        }
        return a[0] < b[0] ? -1 : 1;
    }
}