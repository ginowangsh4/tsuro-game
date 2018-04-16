import java.util.*;

public class Tile {
    protected int[][] paths; //immutable
    protected ArrayDeque<int[]> orientations; //mutable
    private final int[][] default_orientation = new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}};

    Tile(int[][] paths) {
        this.paths = new int[4][2];
        orientations = new ArrayDeque<>();
        for (int i = 0; i < paths.length; i++) {
            this.paths[i][0] = paths[i][0];
            this.paths[i][1] = paths[i][1];
        }
        // top -> (0,1), right -> (2,3), bottom -> (4,5), right -> (6,7)
        // For any side on the bottom, first element marks index on right,
        // second element marks index on left
        // For any side on the right, first element marks index on bottom,
        // second element marks index on top
        orientations.addLast(new int[]{0, 1});
        orientations.addLast(new int[]{2, 3});
        orientations.addLast(new int[]{4, 5});
        orientations.addLast(new int[]{6, 7});
    }

    public boolean isSameTile(Tile tile)
    {
        return (this.paths.equals(tile.paths)) ? true : false;
    }

    // Return which side of the tile a orientation Int lies on
    public String getSide(int orientationInt)
    {
        int i = 0;
        for (int[] array: this.orientations)
        {
            if (orientationInt == array[0] || orientationInt == array[1]) break;
            i++;
        }
        if (i == 0) return "T";
        else if (i == 1) return "R";
        else if (i == 2) return "B";
        else return "L";
    }

    // Map String side to int. T -> 0, R -> 1, B -> 2, L -> 3
    public int mapSide(String side)
    {
        if (side.equals('T')) return 0;
        else if (side.equals('R')) return 1;
        else if (side.equals('B')) return 2;
        else return 3;

    }

    // Return the path index of given a side and which element (first or second) it is
    public int getPathInt(String side, int n)
    {
        int i = 0;
        int orientationInt = 0;
        int sideInt = mapSide(side);
        for (int[] array: this.orientations)
        {
            if (i == sideInt)
            {
                orientationInt = array[n];
                break;
            }
            i ++;
        }

        int rotation = Math.abs(default_orientation[sideInt][n] - orientationInt);
        int pathIndex = sideInt - rotation;

        if (orientationInt%2 == 0) return this.paths[pathIndex][0];
        else return this.paths[pathIndex][1];

    }

    public int getPathEndInt (int startInt)
    {
        int endInt = 0;
        for (int[] array: this.paths)
        {
            if (startInt == array[0])
            {
                endInt = array[1];
                break;
            }
            if (startInt == array[0])
            {
                endInt = array[1];
                break;
            }
        }
        return endInt;
    }


    // Return the orientation int for a given path int
    public int pathToOrient (int pathInt)
    {
        int orientationInt = 0;
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < this.paths.length; i ++) {
            if (pathInt == this.paths[i][0])
            {
                index1 = i;
                index2 = 0;
                break;
            }
            else if (pathInt == this.paths[i][1])
            {
                index1 = i;
                index2 = 1;
                break;
            }
        }

        int i = 0;
        for (int[] array: this.orientations) {
            if (i == index1)
            {
                orientationInt = array[index2];
                break;
            }
        }
        return orientationInt;

    }
}
