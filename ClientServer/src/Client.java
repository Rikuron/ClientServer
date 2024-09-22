import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out; // Sends data to Server
    private DataInputStream in; // Receives data from Server
    private Scanner scanner; // Allows for user input
    private volatile boolean isRunning = true;

    public Client() {
        try {
            scanner = new Scanner(System.in);
            System.out.print("Enter the IP address of the server: ");
            String ip = scanner.nextLine(); // Manually input IP of Server
            socket = new Socket(ip, Server.PORT); // Connects to Server
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            scanner = new Scanner(System.in);
            System.out.print("Please enter your username: ");
            String username = scanner.nextLine();
            out.writeUTF(username); // Sends username to Server
            System.out.println("");

            new Thread(this::readMessagesFromServer).start(); // Start thread to read messages from Server

            System.out.println();
            System.out.println("You have entered the server!");
            System.out.println();

            writeMessages(); // Start writing messages to Server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to write messages to Server
    private void writeMessages() throws IOException {
        String line = "";
        while (isRunning && !line.equals(Server.STOP_STRING)) {
            line = scanner.nextLine();
            out.writeUTF(line); // Sends Client's inputted message to Server
        } // Breaks when Client inputs STOP_STRING
        System.out.println("");
        System.out.println("        You left the server.");
        System.out.println("");

        close(); // Closes Client
    }

    // Method to read messages from Server
    private void readMessagesFromServer() {
        String message = "";
        try {
            while (isRunning) {
                message = in.readUTF(); // Reads a message from Server
                System.out.println(message); // Displays read message on the Client's screen
            }
        } catch (IOException e) {
            if (isRunning) {
                // Handle exceptions silently
            }
        } finally {
            close();
        }
    }

    // Method to close the Client
    private void close() {
        isRunning = false;
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}