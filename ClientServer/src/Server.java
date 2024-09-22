import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server; // ServerSocket to listen for incoming connections
    public static final int PORT = 2030; // Server port number
    public static final String STOP_STRING = "STOP"; // String for Client to disconnect
    private List<ConnectedClient> clients = new CopyOnWriteArrayList<>(); // List to hold number of connected clients
    private volatile boolean isRunning = true; // Flag for checking server's state

    public Server() {
        try {
            server = new ServerSocket(PORT);
            System.out.println();
            System.out.println("SYSTEM: Server started. Waiting for clients...");
            while(isRunning) iniConnections(); // Keep waiting and initializing Client Connections
        } catch (IOException e) {
            if (isRunning) e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    private void iniConnections() throws IOException {
        Socket clientSocket = server.accept(); // Accept incoming Client connection

        if (clientSocket.isConnected() && isRunning) {
            ConnectedClient client = new ConnectedClient(clientSocket, this); // Creates a new client
            clients.add(client); // Add client to list

            // Start thread for handling Client communication
            // Allows for multiple Clients to be handled simultaneously
            new Thread(() -> {
                broadcastConnectMessage(client.getUsername(), client); // Broadcast to other Clients of a new Client connection
                client.readMessages(); // Read messages sent by Client. Ends when Client inputs STOP_STRING
                broadcastDisconnectMessage(client.getUsername()); // Broadcast to other Clients that a Client has disconnected
                client.close();
                clients.remove(client);
                if (clients.isEmpty()) clientDisconnected(); // If no Clients are connected, automatically close server
            }).start();
        }
    }

    // Method to display a message on the Server when all Clients disconnect
    public void clientDisconnected() {
        if (clients.isEmpty()) {
            System.out.println("");
            System.out.println("SYSTEM: No clients are connected. The server will now close. Goodbye!");
            System.out.println("");
            shutdownServer();
        }
    }

    // Method to broadcast a message to all connected Clients
    public void broadcastMessage(String message, ConnectedClient sender) {
        for (ConnectedClient client : clients) {
            if (client != sender) { // Don't send message back to sender
                client.sendMessage(message);
            }
        }
    }

    // Method to broadcast arrival of a new Client to all connected Clients
    public void broadcastConnectMessage(String username, ConnectedClient newClient) {
        String message = "\n        " + username + " has entered the server.\n";
        for (ConnectedClient client : clients) {
            if (client != newClient) {
                client.sendMessage(message);
            }
        }
    }

    // Method to broadcast disconnection of a Client to all connected Clients
    public void broadcastDisconnectMessage(String username) {
        String message = "\n        " + username + " has disconnected.\n";
        System.out.println(message);
        broadcastMessage(message, null);
    }

    // Method to close the Server
    private void shutdownServer() {
        isRunning = false;
        try {
            if (server != null && !server.isClosed()) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ConnectedClient client : clients) {
            client.close();
        }
    }

    public static void main (String [] args) { new Server(); }
}