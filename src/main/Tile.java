package tsuro;
import java.util.*;

public class Tile {

    public int[][] paths;

    public static final Map<Integer, Integer> neighborIndex = new HashMap<Integer, Integer>() {{
        put(0, 5);
        put(1, 4);
        put(2, 7);
        put(3, 6);
        put(4, 1);
        put(5, 0);
        put(6, 3);
        put(7, 2);
    }};

    public Tile(int[][] paths) {
        if (paths.length != 4 || paths[0].length != 2) {
            throw new IllegalArgumentException("Paths is not a 4 x 2 matrix");
        }
        if (!legalPaths(paths)) {
            throw new IllegalArgumentException("Paths invalid");
        }
        this.paths = new int[4][2];
        for (int i = 0; i < paths.length; i++) {
            this.paths[i][0] = paths[i][0];
            this.paths[i][1] = paths[i][1];
        }
    }

    /**
     * Check if an input path vector is a valid tile paths
     * @param paths input path matrix
     * @return true if the path is valid
     */
    private boolean legalPaths(int[][] paths) {
        HashSet<Integer> count = new HashSet<>();
        for (int[] path : paths){
            if (path[0] < 0 || path[0] > 7 || path[1] < 0 || path[1] > 7) {
                return false;
            }
            count.add(path[0]);
            count.add(path[1]);
        }
        return count.size() == 8;
    }

    /**
     * Rotate a given tile clockwise by 90 degrees
     * Mutate the path of this tile
     */
    public void rotateTile() {
        for (int i = 0; i < paths.length; i++) {
            paths[i][0] = (paths[i][0] + 2) % 8;
            paths[i][1] = (paths[i][1] + 2) % 8;
        }
        reorderPath();
    }

    /**
     * Reorder the path arrays of a given tile; only called once by rotateTile()
     * e.g. {{2, 6}, {3, 7}, {4, 1}, {5, 0}} -> {{0, 5}, {1, 4}, {2, 6}, {3, 7}}
     */
    private void reorderPath() {
        for (int[] path : this.paths) {
            Arrays.sort(path);
        }
        Arrays.sort(this.paths, new ListFirstElementComparator());
    }

    /**
     * Duplicate a given tile which remains unchanged after being copied
     * @return a copy of a given tile
     */
    public Tile copyTile(){
        int[][] paths = this.paths;
        int[][] newPaths = new int[paths.length][paths[0].length];
        for (int i = 0; i < paths.length; i++) {
            newPaths[i] = Arrays.copyOf(paths[i], paths[i].length);
        }
        return new Tile(newPaths);
    }

    /**
     * Check whether two paths matrices are equal
     * @param tile the tile whose path to be check against
     * @return true if equal; false if not
     */
    public boolean isSamePaths(Tile tile){
        return Arrays.deepEquals(this.paths, tile.paths);
    }

    /**
     * Check whether two tiles are equal
     * Both tiles' paths is unchanged / not reordered
     * @param tile the tile to be checked against
     * @return true if equal; false if not
     */
    public boolean isSameTile(Tile tile) {
        Tile inputCopy = tile.copyTile();
        for (int i = 0; i < paths.length; i++) {
            inputCopy.rotateTile();
            if (Arrays.deepEquals(inputCopy.paths, this.paths)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given the starting index of a path, get the end index on the path
     * @param start starting index of the path
     * @return the end index
     */
    public int getPathEnd(int start) {
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
    private int countSymmetricPaths(){
        int count = 1;
        Tile copy = this.copyTile();
        for (int i = 0; i < 3; i++ ){
            copy.rotateTile();
            if (!isSamePaths(copy)) {
                count++;
            }
        }
        // if we have count = 3, it means we have two pathways that are different from the original
        // but these two pathways must be the same
        // so we need to subtract count by 1 to get the number of ways it can be placed.
        // count can only be 1, 2, or 4
        return count == 3 ? count - 1 : count;
    }

    /**
     * Print the tile representation on console
     */
    public void print() {
        System.out.print("{ ");
        for (int i = 0; i < paths.length; i++) {
            System.out.print("{" + paths[i][0] + ", " + paths[i][1] + "}");
            if (i != 3) {
                System.out.print(" , ");
            }
        }
        System.out.println(" }");
    }

    /**
     * The following comparator classes are used with Array.sort
     * Sort an array of tile from most symmetric to least symmetric
     * Make sure the order of the path of every tile is properly ordered
     */
    static class SymmetricComparator implements Comparator<Tile> {
        @Override
        // a < b return -1
        // a > b return 1
        // else return 0
        // order is from most symmetric to least symmetric
        public int compare(Tile a, Tile b){
            if (a.countSymmetricPaths() == b.countSymmetricPaths()){
                return 0;
            }
            return a.countSymmetricPaths() < b.countSymmetricPaths() ? -1 : 1;
        }
    }

    /**
     * The following comparator classes are used with Array.sort
     * Sort an array of integer according to the first element of the array
     */
    static class ListFirstElementComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] a, int[] b) {
            if (a[0] == b[0]) {
                return 0;
            }
            return a[0] < b[0] ? -1 : 1;
        }
    }
}


