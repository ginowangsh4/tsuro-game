package tsuro;

import org.w3c.dom.Document;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class RemotePlayer implements IPlayer {

    public int color;
    private Socket socket;
    private DocumentBuilder db;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Parser parser;

    public RemotePlayer(Socket socket, DocumentBuilder db) throws IOException {
        this.socket = socket;
        this.db = db;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.parser = new Parser(db);
    }

    public String getName() throws Exception {
        // to socket
        Document inDoc = parser.buildGetNameXML();
        String s = parser.documentToString(inDoc);
        printWriter.println(s);

        // from socket
        Document outDoc = parser.stringToDocument(bufferedReader.readLine());
        String name = parser.fromGetNameXML(outDoc);

        System.out.println("Remote: getName complete - player name is " + name);
        return name;
    }

    public void initialize(int color, List<Integer> colors) throws Exception {
        // to socket
        Document inDoc = parser.buildInitializeXML(color, colors);
        printWriter.println(parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = parser.stringToDocument(bufferedReader.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        this.color = color;
        System.out.println("Remote: initialize complete");
    }

    public Token placePawn(Board b) throws Exception {
        // to socket
        Document inDoc = parser.buildPlacePawnXML(b);
        printWriter.println(parser.documentToString(inDoc));

        // from socket
        Document outDoc = parser.stringToDocument(bufferedReader.readLine());
        Pair<int[], Integer> pair = parser.fromPlacePawnXML(outDoc);
        Token token = new Token(this.color, pair.second, pair.first);

        System.out.println("Remote: placePawn complete - player starts at [" + token.getPosition()[0] +
                ", " + token.getPosition()[1] + "], index " + token.getIndex());
        return token;
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        Set<Tile> handSet = new HashSet<>();
        handSet.addAll(hand);
        // to socket
        Document inDoc = parser.buildPlayTurnXML(b, handSet, tilesLeft);
        printWriter.println(parser.documentToString(inDoc));

        // from socket
        Document outDoc = parser.stringToDocument(bufferedReader.readLine());
        Tile tile = parser.fromPlayTurnXML(outDoc);

        System.out.print("Remote: playTurn complete - chosen tile is ");
        tile.print();
        return tile;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {
        Set<Integer> colorsSet = new HashSet<>();
        colorsSet.addAll(colors);
        // to socket
        Document inDoc = parser.buildEndGameXML(b, colorsSet);
        printWriter.println(parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = parser.stringToDocument(bufferedReader.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        System.out.println("Remote: endGame complete");
    }
}
