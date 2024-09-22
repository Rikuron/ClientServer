# ClientServer
A simple chatting Client Server made with Java and IntelliJ as submission for my college Database Security requirement. 

Code is contained within the "src" folder. 

There are 3 java classes:

• Server.java
    - imports the following classes:
        • java.io.IOException
        • java.net.ServerSocket
        • java.net.Socket
        • java.util.list
        • java.util.concurrentCopyOnWriteArrayList
    - Allows for connections 
    - Uses multithreading to handle multiple concurrent clients
    - To run the Client Server, run this file first
    - Uses a List to check for connected Clients, if all Clients disconnect, Server is automatically shut down

• Client.java
    - imports the following classes:
        • java.io.DataInputStream
        • java.io.DataOutputStream
        • java.io.IOException
        • java.net.Socket
        • java.util.Scanner
    - Only run after running "Server.java"
    - DataOutputStream and DataInputStream to send and receive messages to and from the Server
    - Requests for the Client user to input the IP address of the Server they wish to connect to
    - After connecting, the Client user is asked to input a username to indicate which Clients are sending which messages
    - Starts a thread to read messages broadcasted from the Server
    - To disconnect, type the stop string "STOP"

• ConnectedClient.java
    - imports the following classes:
        • java.io.BufferedInputStream
        • java.io.DataInputStream
        • java.io.DataOutputStream
        • java.io.IOException
        • java.net.Socket
    - Manages the connection between the Server and the Client
    - Don't run this class
    - Calls methods from the Server for every action done by the Client:
        • After Client connects and enters their username, calls the broadcastConnectMessage method, prompting the Server to alert all other connected Clients that a new Client has connected to the Server.
        • When a Client sends a message, calls the broadcastMessage method, prompting the Server to send the message of the sender Client along with their attached username to every connected Client except the sender Client.
        • When a Client disconnects, calls the broadcastDisconnectMessage method, prompting the Server to alert all other connected Clients that a Client has disconnected from the Server. 
