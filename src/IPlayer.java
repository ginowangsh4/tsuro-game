import java.util.List;

public interface IPlayer {
    String getName();
    void initialize(int color, List<Integer> colors);
    Token placePawn(Board b);
}
