package tsuro;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class RemotePlayer implements IPlayer {
    int color;
    Socket socket;
    DocumentBuilder db;
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    public RemotePlayer(int color, Socket socket, DocumentBuilder db) throws IOException {
        this.color = color;
        this.socket = socket;
        this.db = db;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getName() {

    }

    public void initialize(int color, List<Integer> colors) {

    }

    public Token placePawn(Board b) {

    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) {
        Set<Tile> handSet = new HashSet<>();
        handSet.addAll(hand);

    }

    public void endGame(Board b, List<Integer> colors) {
        Set<Integer> colorsSet = new HashSet<>();
        colorsSet.addAll(colors);

    }


}
