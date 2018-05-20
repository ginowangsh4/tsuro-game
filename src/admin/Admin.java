package tsuro.admin;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Admin {
    Socket socket;
    DocumentBuilder db;
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    public Admin(String hostName, int postNum, DocumentBuilder db) throws IOException {
        this.socket = new Socket(hostName, postNum);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.db = db;
    }

    public boolean connectionEstablished() {
        return socket.isConnected();
    }

    public String readInputFromClient() throws IOException {
        String input = bufferedReader.readLine();
        return input;
    }

    public void writeOutputToClient(String output) {
        printWriter.println(output);
    }
}
