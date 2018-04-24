public class Token {
    private int color;
    private int indexOnTile; //orientation int, not path int
    private int[] position;
    public boolean isNew;

    Token (int color, int indexOnTile, int[] position) {
        this.color = color;
        this.indexOnTile = indexOnTile;
        this.position = position;
        this.isNew = true;
    }

    Token (int indexOnTile, int[] position) {
        this.indexOnTile = indexOnTile;
        this.position = position;
    }

    /**
     * Set the index of a token
     * @param index a new index
     */
    public void setIndex(int index) { indexOnTile = index; }

    /**
     * Get the index of a token
     * @return an index
     */
    public int getIndex() { return indexOnTile; }

    /**
     * Set the position of a token
     * @param xy a position array [x, y]
     */
    public void setPosition(int[] xy){
        position[0] = xy[0];
        position[1] = xy[1];
    }

    /**
     * Get the position of a token
     * @return a position array
     */
    public int[] getPosition() { return position; }

    /**
     * Check whether two tokens are the same
     * @param t a token to be checked against
     * @return true of the two tokens are the same; false if not
     */
    public boolean equals(Token t) {
        if (this.color == t.color) return true;
        return false;
    }

}
