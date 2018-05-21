package tsuro;

import org.w3c.dom.Document;
import tsuro.parser.Parser;

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

    public String getName() throws Exception {
        // to socket
        Document inDoc = Parser.buildGetNameXML(db);
        printWriter.println(Parser.documentToString(inDoc));
        // from socket
        Document outDoc = db.parse(bufferedReader.readLine());
        String name = Parser.fromGetNameXML(db, outDoc);
        return name;
    }

    public void initialize(int color, List<Integer> colors) throws Exception {
        // to socket
        Document inDoc = Parser.buildInitializeXML(db, color, colors);
        printWriter.println(Parser.documentToString(inDoc));
    }

    public Token placePawn(Board b) throws Exception {
        // to socket
        Document inDoc = Parser.buildPlacePawnXML(db, b);
        printWriter.println(Parser.documentToString(inDoc));
        // from socket
        Document outDoc = db.parse(bufferedReader.readLine());
        Pair<int[], Integer> pair = Parser.fromPlacePawnXML(db, outDoc);
        Token token = new Token(this.color, pair.second, pair.first);
        return token;
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        Set<Tile> handSet = new HashSet<>();
        handSet.addAll(hand);
        // to socket
        Document inDoc = Parser.buildPlayTurnXML(db, b, handSet, tilesLeft);
        printWriter.println(Parser.documentToString(inDoc));
        // from socket
        Document outDoc = db.parse(bufferedReader.readLine());
        Tile tile = Parser.fromPlayTurnXML(db, outDoc);
        return tile;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {
        Set<Integer> colorsSet = new HashSet<>();
        colorsSet.addAll(colors);
        // to socket
        Document inDoc = Parser.buildEndGameXML(db, b, colorsSet);
        printWriter.println(Parser.documentToString(inDoc));
    }
}
