import java.util.*;

public class Tile {
    protected ArrayDeque<int[]> paths;
    protected ArrayDeque<int[]> orientations;

    Tile(int[][] paths) {
        this.paths = new ArrayDeque<>();
        orientations = new ArrayDeque<>();
        for (int i = 0; i < paths.length; i++) {
            this.paths.addLast(new int[]{paths[i][0], paths[i][1]});
        }
        // top (0,1), right -> (2,3), bottom -> (4,5), right -> (6,7)
        // For any side on the bottom, first element marks index on right,
        // second element marks index on left
        // For any side on the right, first element marks index on bottom,
        // second element marks index on top
        orientations.addLast(new int[]{0, 1});
        orientations.addLast(new int[]{2, 3});
        orientations.addLast(new int[]{4, 5});
        orientations.addLast(new int[]{6, 7});
    }

}
