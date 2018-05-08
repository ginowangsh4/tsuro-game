import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    void TournamentTest(){
        int winR = 0;
        int winLS = 0;
        int winMS = 0;
        int total = 1000;

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

            server.registerPlayer(mP1);
            server.registerPlayer(mP2);
            server.registerPlayer(mP3);

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
}
