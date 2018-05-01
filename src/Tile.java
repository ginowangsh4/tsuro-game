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
     * @param tile the tile whose path to be check against
     * @return true if equal; false if not
     */
    public boolean equalPath(Tile tile){
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

    /**
     * Count the number of ways a given tile can be placed
     * @return the number of ways it can be placed
     */
    public int countDiffPaths(){
        // make sure every tile has correct path order before be placed on board
        reorderPath();
        int count = 1;
        Tile copy = this.copyTile();
        for (int i = 0; i < 3; i++ ){
            copy.rotateTile();
            copy.reorderPath();
            if (!this.equalPath(copy)) count++;
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
     */
    public void reorderPath() {
        for (int[] array : this.paths) {
            Arrays.sort(array);
        }
        Arrays.sort(this.paths, new ListFirstElementComparator());
    }
}

/**
 * The following comparator classes are used with Array.sort
 * Sort an array of tile from most symmetric to least symmetric
 * Make sure the order of the path of every tile is properly ordered
 */
class SymmetricComparator implements Comparator<Tile> {
    @Override
    // a < b return -1
    // a > b return 1
    // else return 0
    // order is from most symmetric to least symmetric
    public int compare(Tile a, Tile b){
        if (a.countDiffPaths() == b.countDiffPaths()){
            return 0;
        }
        return a.countDiffPaths() < b.countDiffPaths() ? -1 : 1;
    }
}

/**
 * The following comparator classes are used with Array.sort
 * Sort an array of integer according to the first element of the array
 */
class ListFirstElementComparator implements Comparator<int[]> {
    @Override
    public int compare(int[] a, int[] b) {
        if (a[0] == b[0]) {
            return 0;
        }
        return a[0] < b[0] ? -1 : 1;
    }
}
