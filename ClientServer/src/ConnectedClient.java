import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectedClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username; // Client's username
    private Server server; // Reference to Server

    public ConnectedClient(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            this.in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            this.username = in.readUTF(); // Reads Client's username

            System.out.println("");
            System.out.println("        " + username + " has entered.");
            System.out.println("");

        } catch (IOException e) {
            System.out.println("Error initializing client: " + e.getMessage());
            close();
        }
    }

    // Method to read messages sent by Client
    public void readMessages() {
        String line = "";
        try {
            // Continues to read messages until Client inputs STOP_STRING
            while ((line = in.readUTF()) != null && !line.equals(Server.STOP_STRING)) {
                String message = username + ": " + line; // Formats Client's message with their username
                System.out.println(message);
                server.broadcastMessage(message, this); // Broadcast message to other Clients except for sender
            }
        } catch (IOException e) {
            System.out.println("Connection lost with " + username);
        } finally {
            server.clientDisconnected();
            close();
        }
    }

    // Method of Client send messages
    public void sendMessage(String message) {
        try {
            if (!clientSocket.isClosed()) {
                out.writeUTF(message); // Send a message to Client
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    } // Returns Client's username

    public void close() {
        try {
            if (!clientSocket.isClosed())clientSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.out.println("Error closing client for " + username + ": " + e.getMessage());
        }
    }
}