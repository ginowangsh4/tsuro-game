package tsuro;

public class Tsuro {
    public static void main(String[] args) {
        // start a local host for networked tournament
        Server server = Server.getInstance();
        try {
            server.startGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
