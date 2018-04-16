public class Token {
    private Player player;
    private int color;
    private int endPoint; //orientation int, not path int
    private int[] position = new int[] {0, 0};

    Token (Player player, int color, int endPoint, int[] position) {
        this.player = player;
        this.color = color;
        this.endPoint = endPoint;
        this.position = position;
    }

    Token (int endPoint, int[] position) {
        this.endPoint = endPoint;
        this.position = position;
    }

    public void setEndPoint(int index) { endPoint = index; }

    // Return the orientation int where the token is currently standing upon
    public int getEndPint() { return endPoint; }

    public void setPosition(int x, int y) {
        position[0] = x;
        position[1] = y;
    }

    public int[] getPosition() { return position; }

}
