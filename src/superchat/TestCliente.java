package superchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestCliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TestCliente(String serverAddress, int serverPort) throws IOException {
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

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            TestCliente cliente = new TestCliente("localhost", 49175);
            cliente.sendMessage("Hola");
            System.out.println("Mensaje del servidor: " + cliente.receiveMessage());
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
