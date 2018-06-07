package tsuro;

import org.w3c.dom.Document;
import tsuro.admin.PlacePawnController;
import tsuro.parser.Parser;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HPlayer implements IPlayer {

    private BufferedReader in;
    private PrintWriter out;
    private Parser parser;


    private String name;
    private int color;
    private List<Integer> colors;
    private boolean isWinner;

    public HPlayer(String name) throws Exception {
        ServerSocket socketListener = new ServerSocket(9000);
        Socket socket = socketListener.accept();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.name = name;
        this.parser = new Parser(DocumentBuilderFactory.newInstance().newDocumentBuilder());
    }

    public String getName() throws IOException {
        System.out.println(in.readLine());
        return name;
    }

    public void initialize (int color, List<Integer> colors) {
        this.color = color;
        this.colors = colors;
    }

    public Token placePawn(Board b) throws Exception {
        generateBoardImage(parser.boardParser.buildXML(b), -1, -1);
        System.out.println(in.readLine());
        return null;
    }

    public Tile playTurn(Board b, List<Tile> hand, int tilesLeft) throws Exception {
        generateImages(b, hand);
        return null;
    }

    private Token findMyToken(Board b) {
        for (SPlayer sp : b.getSPlayerList()) {
            if (sp.getToken().getColor() == color) {
                return sp.getToken();
            }
        }
        throw new IllegalArgumentException("Cannot find token on board");
    }

    public void endGame(Board b, List<Integer> colors) throws Exception {

    }

    /**
     * Generate token game object based on pawn location clicked from UI
     * @param colorIndex HPlayer's color index
     * @param side side of location clicked
     * @param index index of location clicked
     * @return token game object
     */
    public static Token generateTokenBySideIndex(int colorIndex, PlacePawnController.Side side, int index) throws Exception {
        if (index < 0 || index > 11) {
            throw new Exception("Index is not valid");
        }
        int indexOnTile, x, y;
        if (side == PlacePawnController.Side.TOP) {
            x = index / 2;
            y = -1;
            indexOnTile = Tile.neighborIndex.get(index % 2);
        }
        else if (side == PlacePawnController.Side.BOTTOM) {
            x = index / 2;
            y = 6;
            indexOnTile = index % 2;
        }
        else if (side == PlacePawnController.Side.LEFT) {
            x = -1;
            y = index / 2;
            indexOnTile = index % 2 + 2;
        }
        else {
            x = 6;
            y = index / 2;
            indexOnTile = Tile.neighborIndex.get(index % 2 + 2);
        }
        int[] pos = new int[]{x,y};
        return new Token(colorIndex, pos, indexOnTile);
    }

    private void generateImages(Board b, List<Tile> hand) throws Exception {
        Document boardXML = parser.boardParser.buildXML(b);
        Token token = findMyToken(b);
        int[] location = Board.getAdjacentLocation(token);
        for (int i = 0; i < hand.size(); i++) {
            Tile copy = hand.get(i).copyTile();
            for (int j = 0; j < 4; j++) {
                Board tempBoard = parser.boardParser.fromXML(boardXML);
                tempBoard.placeTile(copy, location[0], location[1]);
                for (SPlayer sp : b.getSPlayerList()) {
                    sp.updateToken(tempBoard.simulateMove(sp.getToken()));
                }
                generateBoardImage(parser.boardParser.buildXML(tempBoard), i, j);
                copy.rotateTile();
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            generateTileImage(parser.tileParser.buildXML(hand.get(i)), i);
        }
    }

    private void generateBoardImage(Document doc, int tileIndex, int rotationIndex) throws Exception {
        String command = "./visualize -b -i image/board/" + tileIndex + "/" + rotationIndex + ".png";
        if (tileIndex == -1 && rotationIndex == -1) {
            command = "./visualize -b -i image/board/board.png";
        }
        String line;
        Process p = Runtime.getRuntime().exec(command);
        PrintWriter out = new PrintWriter(p.getOutputStream(), true);
        out.println(parser.documentToString(doc));
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    private void generateTileImage(Document doc, int tileIndex) throws Exception {
        String command = "./visualize -t -i image/tile/" + tileIndex + ".png";
        String line;
        Process p = Runtime.getRuntime().exec(command);
        PrintWriter out = new PrintWriter(p.getOutputStream(), true);
        out.println(parser.documentToString(doc));
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    public static void main(String[] args) throws Exception {
        HPlayer p = new HPlayer("Jeff");
        p.getName();
        p.placePawn(new Board());
    }
}
