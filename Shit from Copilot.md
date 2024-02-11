To create an IRC-like chat system using Java Sockets, you can follow these steps:

1. Create a `Server` class that will accept incoming connections from clients. This class should have a `ServerSocket` to listen for incoming connections and a list to keep track of all connected clients.

2. Create a `ClientHandler` class that will handle communication with each connected client. This class should have a `Socket` for communication with the client and methods for reading and writing messages.

3. In the `Server` class, whenever a new connection is accepted, create a new `ClientHandler` for that connection and add it to the list of connected clients.

4. In the `ClientHandler` class, whenever a message is received from the client, broadcast it to all other connected clients.

5. Create a `Client` class that will connect to the server and send and receive messages. This class should have a `Socket` for communication with the server and methods for reading and writing messages.

Here's how you can modify your existing code to achieve this:

```java
// Server class
public class TestServidor {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public TestServidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public void broadcast(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // ... rest of your code ...
}
```

```java
// ClientHandler class
class ClientHandler implements Runnable {
    private Socket socket;
    private TestServidor server;
    private PrintWriter out;

    public ClientHandler(Socket socket, TestServidor server) throws IOException {
        this.socket = socket;
        this.server = server;
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = in.readLine()) != null) {
                server.broadcast(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
// Client class
public class Cliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Cliente(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    // ... rest of your code ...
}
```

In this code, the `TestServidor` class accepts incoming connections and creates a new `ClientHandler` for each one. The `ClientHandler` class reads messages from its client and broadcasts them to all other clients. The `Cliente` class connects to the server and sends and receives messages.

This is a basic implementation and there are many ways to improve and expand it. For example, you could add support for different types of messages (like private messages or system messages), handle disconnections gracefully, or add a GUI.


