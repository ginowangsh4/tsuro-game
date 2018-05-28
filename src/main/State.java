package tsuro;

import java.util.List;

public class State {
    public List<SPlayer> inSPlayer;
    public List<SPlayer> outSPlayer;
    public List<SPlayer> winners;
    public Board board;
    public Deck drawPile;

    public String inSPlayerStr;
    public String outSPlayerStr;
    public String boardStr;
    public String drawPileStr;
    public String tileStr;

    public State() {}

    public void setState(Board board, List<SPlayer> inSPlayer, List<SPlayer> outSPlayer, List<SPlayer> winners, Deck drawPile) {
        this.board = board;
        this.inSPlayer = inSPlayer;
        this.outSPlayer = outSPlayer;
        this.winners = winners;
        this.drawPile = drawPile;
    }

    public void setPlayATurnState(String drawPileStr, String inSPlayerStr, String outSPlayerStr, String boardStr, String tileStr) {
        this.drawPileStr = drawPileStr;
        this.inSPlayerStr = inSPlayerStr;
        this.outSPlayerStr = outSPlayerStr;
        this.boardStr = boardStr;
        this.tileStr = tileStr;
    }
}
