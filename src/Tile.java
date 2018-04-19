import java.util.*;

public class Tile {
    protected int[][] paths; // mutable
    public final Map<Integer, Integer> neighborIndex = new HashMap<Integer, Integer>() {{
        put(0, 5);
        put(1, 4);
        put(2, 7);
        put(3, 6);
        put(4, 1);
        put(5, 0);
        put(6, 3);
        put(7, 2);
    }};

    Tile(int[][] paths) {
        this.paths = new int[4][2];
        for (int i = 0; i < paths.length; i++) {
            this.paths[i][0] = paths[i][0];
            this.paths[i][1] = paths[i][1];
        }
    }

    /**
     * Get all the engraved paths on a tile
     * @return a 2d array of paths
     */
    public int[][] getPaths() {
        return this.paths;
    }

    public void rotateTile() {
        for (int side = 0; side < paths.length; side++) {
            paths[side][0] = (paths[side][0] + 2) % 8;
            paths[side][1] = (paths[side][1] + 2) % 8;
        }
    }

    /**
     * Check whether two tiles are equal
     * @param tile the tile to be checked against
     * @return true if equal; false if not
     */
    public boolean isSameTile(Tile tile) {
        int[][] paths = tile.getPaths();
        int[][] temp = new int[4][];
        for (int i = 0; i < 4; i++) {
            temp[i] = Arrays.copyOf(paths[i], paths[i].length);
        }
        Tile tempTile = new Tile(temp);
        for (int i = 0; i < 4; i++) {
//            for (int side = 0; side < temp.length; side++) {
//                temp[side][0] = (temp[side][0] + 2) % 8;
//                temp[side][1] = (temp[side][1] + 2) % 8;
//            }
            tempTile.rotateTile();
            if (Arrays.deepEquals(tempTile.getPaths(), this.paths)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given the starting index of a path, get the end index on the path
     * @param startInt starting index of the path
     * @return the end index
     */
    public int getPathEnd (int startInt) {
        int endInt = 0;
        for (int[] array : this.paths) {
            if (startInt == array[0]) {
                endInt = array[1];
                break;
            } else if (startInt == array[1]) {
                endInt = array[0];
                break;
            }
        }
        return endInt;
    }
}
