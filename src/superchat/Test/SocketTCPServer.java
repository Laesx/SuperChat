package superchat.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTCPServer {
    private ServerSocket serverSocket;

    public SocketTCPServer(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("(Servidor) A la espera de conexiones.");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("(Servidor) Conexión establecida.");
            new GestorProcesos(socket).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) {
        try {
            SocketTCPServer servidor = new SocketTCPServer(49171);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
