package tsuro;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    static Board board;
    static Deck deck;
    static List<SPlayer> inSPlayer;
    static List<SPlayer> outSPlayer;
    static List<SPlayer> winners;
    static List<Integer> colors = new ArrayList<>();
    static Server server = Server.getInstance();

    // Run a tournament with three players
    // Player 1 uses Random strategy
    // Player 2 uses LeastSymmetric strategy
    // Player 3 uses MostSymmetric Strategy
    @Test
    public void TournamentTest() throws Exception {
        int winR = 0;
        int winLS = 0;
        int winMS = 0;
        int total = 10;

        for(int count = 0; count < total; count++){
            board = new Board();
            inSPlayer = new ArrayList<>();
            outSPlayer = new ArrayList<>();
            winners = new ArrayList<>();
            deck = new Deck();
            colors = new ArrayList<>();
            server.setState(board, inSPlayer, outSPlayer, winners, colors, deck);

            MPlayer mP1 = new MPlayer(MPlayer.Strategy.R, "Player 1");
            MPlayer mP2 = new MPlayer(MPlayer.Strategy.LS, "Player 2");
            MPlayer mP3 = new MPlayer(MPlayer.Strategy.MS, "Player 3");
            colors.add(1);
            colors.add(2);
            colors.add(3);
            mP1.initialize(1, colors);
            mP2.initialize(2, colors);
            mP3.initialize(3, colors);

            Token t1 = mP1.placePawn(board);
            server.registerPlayer(mP1, t1);
            Token t2 = mP2.placePawn(board);
            server.registerPlayer(mP2, t2);
            Token t3 = mP3.placePawn(board);
            server.registerPlayer(mP3, t3);

            List<SPlayer> winners = new ArrayList<>();

            while(!server.isGameOver()) {
                SPlayer currentP = inSPlayer.get(0);
                Tile tileToPlay = currentP.getPlayer().playTurn(board, currentP.getHand(), deck.size());
                currentP.deal(tileToPlay);

                winners = server.playATurn(tileToPlay);
            }

            for (SPlayer p: winners) {
                if (p.getMPlayer().strategy.equals(MPlayer.Strategy.R)) winR ++;
                else if (p.getMPlayer().strategy.equals(MPlayer.Strategy.LS)) winLS ++;
                else winMS++;
            }
        }

        // Most symmetric players is the most consistent in having the highest winning ratio
        // Random comes the second
        // Least symmetric player has the lowest winning ratio
        System.out.println(" *************************************************** ");
        System.out.println(" * Random wins " + (float) winR / total * 100 + "% of all games");
        System.out.println(" * Least Symmetric wins " + (float) winLS / total * 100 + "% of all games");
        System.out.println(" * Most Symmetric wins " + (float) winMS / total * 100 + "% of all games");
        System.out.println(" *************************************************** ");
    }

    @Test   // chooses a position to start that is not a valid phantom position
    public void cheatIllegalStartingPosnTest() throws Exception {
        board = new Board();
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        deck = new Deck();
        colors = new ArrayList<>();

        server.setState(board, inSPlayer, outSPlayer, winners, colors, deck);

        MPlayer mP1 = new MPlayer(MPlayer.Strategy.R, "Player 1");
        MPlayer mP2 = new MPlayer(MPlayer.Strategy.LS, "Player 2");
        MPlayer mP3 = new MPlayer(MPlayer.Strategy.MS, "Player 3");
        colors.add(1);
        colors.add(2);
        colors.add(3);
        mP1.initialize(1, colors);
        mP2.initialize(2, colors);
        mP3.initialize(3, colors);

        Token t1 = new Token(1, 1, new int[]{1, 1});
        server.registerPlayer(mP1, t1);
        Token t2 = new Token(2, 2, new int[]{2, 2});
        server.registerPlayer(mP2, t2);
        Token t3 = new Token(3, 3, new int[]{3, 3});
        server.registerPlayer(mP3, t3);

        while (!server.isGameOver()) {
            SPlayer currentP = inSPlayer.get(0);
            Tile tileToPlay = currentP.getPlayer().playTurn(board, currentP.getHand(), deck.size());
            currentP.deal(tileToPlay);
            server.playATurn(tileToPlay);
        }

        for (SPlayer sp : inSPlayer) {
            assertEquals(MPlayer.Strategy.R, sp.getMPlayer().strategy, "Player's cheating is not caught");
        }
        for (SPlayer sp : outSPlayer) {
            assertEquals(MPlayer.Strategy.R, sp.getMPlayer().strategy, "Player's cheating is not caught");
        }
        assertEquals(true, server.isGameOver());
    }

    @Test   // chooses a position which already has a token
    public void cheatIllegalStartingPosnTest2() throws Exception {
        MPlayer playerOne = new MPlayer(MPlayer.Strategy.LS, "Player 1");
        MPlayer playerTwo = new MPlayer(MPlayer.Strategy.LS, "Player 2");

        board = new Board();
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        deck = new Deck();
        colors = new ArrayList<>(Arrays.asList(0, 1));
        Server server = Server.getInstance();
        server.setState(board, inSPlayer, outSPlayer, winners, colors, deck);

        playerOne.initialize(0, colors);
        Token tokenOne = new Token(0, 0, new int[]{0, 6});
        server.registerPlayer(playerOne, tokenOne);

        playerTwo.initialize(1, colors);
        Token tokenTwo = new Token(1, 0, new int[]{0, 6});
        server.registerPlayer(playerTwo, tokenTwo);

        assertEquals(MPlayer.Strategy.LS, server.inSPlayer.get(0).getMPlayer().strategy, "Player 1's strategy changed");
        assertEquals(MPlayer.Strategy.R, server.inSPlayer.get(1).getMPlayer().strategy, "Player 2's strategy didn't change to R");
        assertFalse(server.inSPlayer.get(1).getToken().getIndex() == 0 && server.inSPlayer.get(1).getToken().getPosition()[0] == 0
                        && server.inSPlayer.get(1).getToken().getPosition()[1] == 6, "Player 2's starting position wasn't updated");
    }

    @Test
    public void cheatIllegalTileTest() throws Exception {
        Tile t1 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][] {{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][] {{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        board = new Board();
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        winners = new ArrayList<>();
        List<Tile> pile = new ArrayList<>();
        pile.addAll(Arrays.asList(t1, t2, t3));
        deck = new Deck(pile);
        colors = new ArrayList<>();

        server.setState(board, inSPlayer, outSPlayer, winners, colors, deck);

        MPlayer mP = new MPlayer(MPlayer.Strategy.LS, "Player 1");
        colors.add(1);
        mP.initialize(1, colors);
        Token token = mP.placePawn(board);
        server.registerPlayer(mP, token);

        SPlayer currentP = inSPlayer.get(0);
        Tile tileToPlay = t1;
        currentP.deal(tileToPlay);
        server.playATurn(tileToPlay);

        assertEquals(MPlayer.Strategy.R, currentP.getMPlayer().strategy, "Player's cheating is not caught");
        assertEquals(true, server.isGameOver());
    }
}
