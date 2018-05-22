package tsuro;

public class PlayATurn {
    public static void main(String[] args) {
        String deckStr = args[0];
        String inPlayerStr = args[1];
        String outPlayerStr = args[2];
        String boardStr = args[3];
        String tileStr = args[4];


        Server server = Server.getInstance();
        server.setState(board, inSPlayer, outSPlayer, deck);
        server.playATurn(tile);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
