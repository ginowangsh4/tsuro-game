package tsuro;

import org.w3c.dom.Document;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class RemotePlayer implements IPlayer {
    public int color;
    Socket socket;
    DocumentBuilder db;
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    public RemotePlayer(Socket socket, DocumentBuilder db) throws IOException {
        this.socket = socket;
        this.db = db;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getName() throws Exception {
        // to socket
        Document inDoc = Parser.buildGetNameXML(db);
        String s = Parser.documentToString(inDoc);
        printWriter.println(s);
        // from socket
        Document outDoc = Parser.stringToDocument(db, bufferedReader.readLine());
        String name = Parser.fromGetNameXML(db, outDoc);

        System.out.println("Remote: getName complete - player name is " + name);
        return name;
    }

    public void initialize(int color, List<Integer> colors) throws Exception {
        // to socket
        Document inDoc = Parser.buildInitializeXML(db, color, colors);
        printWriter.println(Parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = Parser.stringToDocument(db, bufferedReader.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        System.out.println("Remote: initialize complete");
    }

    public Token placePawn(Board b) throws Exception {
        // to socket
        Document inDoc = Parser.buildPlacePawnXML(db, b);
        printWriter.println(Parser.documentToString(inDoc));

        // from socket
        Document outDoc = Parser.stringToDocument(db, bufferedReader.readLine());
        //System.out.println(Parser.documentToString(outDoc));
        Pair<int[], Integer> pair = Parser.fromPlacePawnXML(db, outDoc);
        Token token = new Token(this.color, pair.second, pair.first);

        System.out.println("Remote: placePawn complete - player starts at [" + token.getPosition()[0] + ", " + token.getPosition()[1] + "], index " + token.getIndex());
        return token;
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        Set<Tile> handSet = new HashSet<>();
        handSet.addAll(hand);
        // to socket
        Document inDoc = Parser.buildPlayTurnXML(db, b, handSet, tilesLeft);
        printWriter.println(Parser.documentToString(inDoc));

        // from socket
        Document outDoc = Parser.stringToDocument(db, bufferedReader.readLine());
        Tile tile = Parser.fromPlayTurnXML(db, outDoc);

        System.out.print("Remote: playTurn complete - chosen tile is ");
        tile.print();
        return tile;
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {
        Set<Integer> colorsSet = new HashSet<>();
        colorsSet.addAll(colors);
        // to socket
        Document inDoc = Parser.buildEndGameXML(db, b, colorsSet);
        printWriter.println(Parser.documentToString(inDoc));

        // from socket, must be void
        Document outDoc = Parser.stringToDocument(db, bufferedReader.readLine());
        if (!outDoc.getFirstChild().getNodeName().equals("void")) {
            throw new IllegalArgumentException("Response is not void!");
        }

        System.out.println("Remote: endGame complete");
    }
}
