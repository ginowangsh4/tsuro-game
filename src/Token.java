public class Token {
    private int color;
    private int indexOnTile; //orientation int, not path int
    private int[] position;
    private Player owner;

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

    public int getIndex() { return indexOnTile; }

    public void setPosition(int[] xy){
        position[0] = xy[0];
        position[1] = xy[1];
    }

    public int[] getPosition() { return position; }

    public Player getOwner() {return owner;}

    public void setOwner(Player p) {this.owner = p;}

    public boolean equals(Token t) {
        if (this.color == t.color) return true;
        return false;
    }

}
