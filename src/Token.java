public class Token {
    private Player player;
    private int color;
    private int indexOnTile; //orientation int, not path int
    private int[] position = new int[] {0, 0};

    Token (Player player, int color, int indexOnTile, int[] position) {
        this.player = player;
        this.color = color;
        this.indexOnTile = indexOnTile;
        this.position = position;
    }

    Token (int indexOnTile, int[] position) {
        this.indexOnTile = indexOnTile;
        this.position = position;
    }

    public void setIndex(int index) { indexOnTile = index; }

    // Return the orientation int where the token is currently standing upon
    public int getIndex() { return indexOnTile; }

    public void setPosition(int x, int y) {
        position[0] = x;
        position[1] = y;
    }

    public int[] getPosition() { return position; }

}
