package tsuro.admin;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class AdminSocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public AdminSocket(String hostName, int postNum) throws IOException {

        this.socket = new Socket(InetAddress.getByName(hostName), postNum);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public boolean connectionEstablished() { return socket.isConnected(); }

    public String readInputFromServer() throws IOException { return in.readLine(); }

    public void writeOutputToServer(String output) { out.println(output); }

    public void closeConnection() throws IOException { socket.close(); }
}
