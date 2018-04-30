import java.util.*;

public class Tile {
    //protected field for the purpose of testing
    protected int[][] paths;
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

    Tile (int[][] paths) {
        if (paths.length != 4 || paths[0].length != 2) {
            throw new IllegalArgumentException("Path is not a 4 x 2 matrix");
        }
        this.paths = new int[4][2];
        for (int i = 0; i < paths.length; i++) {
            this.paths[i][0] = paths[i][0];
            this.paths[i][1] = paths[i][1];
        }
    }

    /**
     * Rotate a given tile clockwise by 90 degrees
     */
    public void rotateTile() {
        for (int side = 0; side < paths.length; side++) {
            paths[side][0] = (paths[side][0] + 2) % 8;
            paths[side][1] = (paths[side][1] + 2) % 8;
        }
    }

    /**
     * Duplicate a given tile
     * @return a copy of a given tile
     */
    public Tile copyTile(){
        int[][] paths = this.paths;
        int[][] temp = new int[4][];
        for (int i = 0; i < 4; i++) {
            temp[i] = Arrays.copyOf(paths[i], paths[i].length);
        }
        return new Tile(temp);
    }

    /**
     * Check whether two tiles are equal
     * @param tile the tile to be checked against
     * @return true if equal; false if not
     */
    public boolean isSameTile(Tile tile) {
        Tile tempTile = tile.copyTile();
        for (int i = 0; i < 4; i++) {
            tempTile.rotateTile();
            if (Arrays.deepEquals(tempTile.paths, this.paths)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether two path arrays are equal
     * @param paths the path array to be check against
     * @return true if equal; false if not
     */
    public boolean hasSamePaths(Tile tile){
        return Arrays.deepEquals(this.paths, tile.paths);
    }

    /**
     * Given the starting index of a path, get the end index on the path
     * @param start starting index of the path
     * @return the end index
     */
    public int getPathEnd (int start) {
        for (int[] array : this.paths) {
            if (start == array[0]) {
                return array[1];
            } else if (start == array[1]) {
                return array[0];
            }
        }
        throw new IllegalArgumentException("Error: Invalid path start index");
    }
}
