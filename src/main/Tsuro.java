package tsuro;

public class Tsuro {

    // CML arguments:
    // 0: Port_Number,
    // 1: Number_of_HPlayer,
    // 2: Number_of_MPlayerRandom,
    // 3: Number_of_MPlayerMostSym,
    // 4: Number_of_MPlayerLeastSym,
    // 5: Number_of_RemotePlayer
    public static void main(String[] args) {
        // start a local host for networked tournament
        Server server = Server.getInstance();
        server.PORT_NUM = Integer.parseInt(args[0]);
        try {
            server.startGame(Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]),Integer.parseInt(args[5]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
