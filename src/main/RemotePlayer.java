package tsuro;

import org.w3c.dom.Document;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class RemotePlayer implements IPlayer {

    public int color;
    private BufferedReader in;
    private PrintWriter out;
    private Parser parser;

    public RemotePlayer(Socket socket, DocumentBuilder db) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.parser = new Parser(db);
    }

    public String getName() throws Exception {
        // to socket
        Document inDoc = parser.buildGetNameXML();
        String s = parser.documentToString(inDoc);
        out.println(s);

        // from socket
        Document outDoc = parser.stringToDocument(in.readLine());
        String name = parser.fromGetNameXML(outDoc);

        System.out.println("Remote: getName complete - player name is " + name);
        return name;
    }

    public void initialize(int color, List<Integer> colors) throws Exception {
        // to socket
        Document inDoc = parser.buildInitializeXML(color, colors);
        out.println(parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = parser.stringToDocument(in.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        this.color = color;
        System.out.println("Remote: initialize complete");
    }

    public Token placePawn(Board b) throws Exception {
        // to socket
        Document inDoc = parser.buildPlacePawnXML(b);
        out.println(parser.documentToString(inDoc));

        // from socket
        Document outDoc = parser.stringToDocument(in.readLine());
        Pair<int[], Integer> pair = parser.fromPlacePawnXML(outDoc);
        Token token = new Token(color, pair.first, pair.second);

        System.out.println("Remote: placePawn complete - player starts at [" + token.getPosition()[0] +
                ", " + token.getPosition()[1] + "], index " + token.getIndex());
        return token;
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        Set<Tile> handSet = new HashSet<>(hand);
        // to socket
        Document inDoc = parser.buildPlayTurnXML(b, handSet, tilesLeft);
        out.println(parser.documentToString(inDoc));

        // from socket
        Document outDoc = parser.stringToDocument(in.readLine());
        Tile tile = parser.fromPlayTurnXML(outDoc);

        System.out.print("Remote: playTurn complete - chosen tile is ");
        tile.print();
        return tile;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {
        Set<Integer> colorsSet = new HashSet<>(colors);
        // to socket
        Document inDoc = parser.buildEndGameXML(b, colorsSet);
        out.println(parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = parser.stringToDocument(in.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        System.out.println("Remote: endGame complete");
    }
}
