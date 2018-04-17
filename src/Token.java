public class Token {
    private int color;
    private int indexOnTile; //orientation int, not path int
    private int[] position;

    Token (int color, int indexOnTile, int[] position) {
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
