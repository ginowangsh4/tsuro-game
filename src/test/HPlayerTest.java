package tsuro;

import org.junit.jupiter.api.Test;
import tsuro.admin.UIAdmin;
import tsuro.admin.UISuite;

import static org.junit.jupiter.api.Assertions.*;

class HPlayerTest {
    @Test
    void generateTokenBySideIndex() throws Exception {
        // left and even index
        Token expected = new Token(0, 2, new int[]{-1,1});
        Token token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.LEFT, 2);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // left and odd index
        expected = new Token(0, 3, new int[]{-1,3});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.LEFT, 7);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // right and even index
        expected = new Token(0, 7, new int[]{6,2});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.RIGHT, 4);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // right and odd index
        expected = new Token(0, 6, new int[]{6,5});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.RIGHT, 11);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // top and even index
        expected = new Token(0, 5, new int[]{3,-1});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.TOP, 6);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // top and odd index
        expected = new Token(0, 4, new int[]{4,-1});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.TOP, 9);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // bottom and even index
        expected = new Token(0, 0, new int[]{4,6});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.BOTTOM, 8);
        assertTrue(expected.equals(token), "Generated token is not as expected.");

        // bottom and odd index
        expected = new Token(0, 1, new int[]{2,6});
        token = HPlayer.generateTokenBySideIndex(0, UISuite.Side.BOTTOM, 5);
        assertTrue(expected.equals(token), "Generated token is not as expected.");
    }

}