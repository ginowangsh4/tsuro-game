package tsuro;

import org.junit.jupiter.api.Test;
import tsuro.admin.PlacePawnController;

import static org.junit.jupiter.api.Assertions.*;

class HPlayerTest {
    @Test
    void generateTokenBySideIndex() throws Exception {
        // left and even index
        Token expected = new Token(0, new int[]{-1,1}, 2);
        Token token = HPlayer.buildTokenFromSideAndIndex(0, "LEFT", 2);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // left and odd index
        expected = new Token(0, new int[]{-1,3}, 3);
        token = HPlayer.buildTokenFromSideAndIndex(0, "LEFT", 7);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // right and even index
        expected = new Token(0, new int[]{6,2}, 7);
        token = HPlayer.buildTokenFromSideAndIndex(0, "RIGHT", 4);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // right and odd index
        expected = new Token(0, new int[]{6,5}, 6);
        token = HPlayer.buildTokenFromSideAndIndex(0, "RIGHT", 11);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // top and even index
        expected = new Token(0, new int[]{3,-1}, 5);
        token = HPlayer.buildTokenFromSideAndIndex(0, "TOP", 6);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // top and odd index
        expected = new Token(0, new int[]{4,-1}, 4);
        token = HPlayer.buildTokenFromSideAndIndex(0, "TOP", 9);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // bottom and even index
        expected = new Token(0, new int[]{4,6}, 0);
        token = HPlayer.buildTokenFromSideAndIndex(0, "BOTTOM", 8);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");

        // bottom and odd index
        expected = new Token(0, new int[]{2,6}, 1);
        token = HPlayer.buildTokenFromSideAndIndex(0, "BOTTOM", 5);
        assertTrue(expected.isSameToken(token), "Generated token is not as expected.");
    }

}