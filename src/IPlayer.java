import java.util.*;

public interface IPlayer {
    String getName();
    void initialize(int color, List<Integer> colors);
    Token placePawn(Board b);
}
