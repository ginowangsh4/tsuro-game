public class Token {
    private Player player;
    private int color;
    private int endPoint;
    private int[] position = new int[] {0, 0};

    public void setEndPoint(int index) { endPoint = index; }

    public int getEndPint() { return endPoint; }

    public void setPosition(int x, int y) {
        position[0] = x;
        position[1] = y;
    }

    public int[] getPosition() { return position; }

}
