package tsuro;

public class Tsuro {
    public static void main(String[] args) {
        Server server = Server.getInstance();
        try {
            server.startGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
