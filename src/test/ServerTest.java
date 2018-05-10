package tsuro;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("Duplicates")
public class ServerTest {
    static Board b;
    static Deck deck;
    static List<SPlayer> inSPlayer;
    static List<SPlayer> outSPlayer;
    static List<Integer> colors = new ArrayList<>();
    static Server server = Server.getInstance();

    // Run a tournament with three players
    // Player 1 uses Random strategy
    // Player 2 uses LeastSymmetric strategy
    // Player 3 uses MostSymmetric Strategy
    @Test
    public void TournamentTest(){
        int winR = 0;
        int winLS = 0;
        int winMS = 0;
        int total = 100;

        for(int count = 0; count < total; count++){
            b = new Board();
            inSPlayer = new ArrayList<>();
            outSPlayer = new ArrayList<>();
            deck = new Deck();
            server.setState(b, inSPlayer, outSPlayer, deck);

            MPlayer mP1 = new MPlayer("R");
            MPlayer mP2 = new MPlayer("LS");
            MPlayer mP3 = new MPlayer("MS");
            colors.add(1);
            colors.add(2);
            colors.add(3);
            mP1.initialize(1, colors);
            mP2.initialize(2, colors);
            mP3.initialize(3, colors);

            Token t1 = mP1.placePawn(b);
            server.registerPlayer(mP1, t1);
            Token t2 = mP2.placePawn(b);
            server.registerPlayer(mP2, t2);
            Token t3 = mP3.placePawn(b);
            server.registerPlayer(mP3, t3);

            List<SPlayer> winners = new ArrayList<>();

            while(!server.isGameOver()) {
                SPlayer currentP = inSPlayer.get(0);
                Tile tileToPlay = currentP.getMPlayer().playTurn(b, currentP.getHand(), deck.size());
                currentP.deal(tileToPlay);
                winners = server.playATurn(tileToPlay);
            }

            for (SPlayer p: winners){
                if (p.getMPlayer().Strategy == "R") winR ++;
                else if (p.getMPlayer().Strategy == "LS") winLS ++;
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

    @Test
    public void cheatIllegalStartingPosnTest() {
        int total = 10;
        for(int count = 0; count < total; count++) {
            b = new Board();
            inSPlayer = new ArrayList<>();
            outSPlayer = new ArrayList<>();
            deck = new Deck();
            server.setState(b, inSPlayer, outSPlayer, deck);

            MPlayer mP1 = new MPlayer("R");
            MPlayer mP2 = new MPlayer("LS");
            MPlayer mP3 = new MPlayer("MS");
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
                Tile tileToPlay = currentP.getMPlayer().playTurn(b, currentP.getHand(), deck.size());
                currentP.deal(tileToPlay);
                server.playATurn(tileToPlay);
            }

            assertEquals("R", mP1.Strategy, "Error: Player 1's cheating is not caught");
            assertEquals("R", mP2.Strategy, "Error: Player 2's cheating is not caught");
            assertEquals("R", mP3.Strategy, "Error: Player 3's cheating is not caught");
            assertEquals(true, server.isGameOver());
        }
    }

    @Test
    public void cheatIllegalTileTest() {
        Tile t1 = new Tile(new int[][] {{0, 1}, {2, 3}, {4, 5}, {6, 7}});
        Tile t2 = new Tile(new int[][] {{0, 1}, {2, 4}, {3, 6}, {5, 7}});
        Tile t3 = new Tile(new int[][] {{0, 6}, {1, 5}, {2, 4}, {3, 7}});
        b = new Board();
        inSPlayer = new ArrayList<>();
        outSPlayer = new ArrayList<>();
        List<Tile> pile = new ArrayList<>();
        pile.addAll(Arrays.asList(t1, t2, t3));
        deck = new Deck();

        server.setState(b, inSPlayer, outSPlayer, deck);

        MPlayer mP = new MPlayer("LS");
        colors.add(1);
        mP.initialize(1, colors);
        Token token = mP.placePawn(b);
        server.registerPlayer(mP, token);

        SPlayer currentP = inSPlayer.get(0);
        Tile tileToPlay = t1;
        currentP.deal(tileToPlay);
        server.playATurn(tileToPlay);

        assertEquals("R", mP.Strategy, "Error: Player's cheating is not caught");
        assertEquals(true, server.isGameOver());
    }
}
