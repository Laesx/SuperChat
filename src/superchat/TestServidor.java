package superchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TestServidor {

    private ServerSocket serverSocket;

    private static ArrayList<ClientHandler> clientes = new ArrayList<>();

    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    public TestServidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);

    }

    public void start () throws IOException {
        //System.out.println(" (Servidor) Esperando conexiones...");
        while (true) {
            Socket socket=serverSocket.accept();
            ClientHandler clientThread = new ClientHandler(socket, this);
            clientes.add(clientThread);
            clientThread.start();
        }

/*
        String mensaje;
        //Iniciamos

        System.out.println("Sala Abierta.");


        int i = 0;
        while (i < 500){
            Socket socketCliente = servidor.serverSocket.accept();
            System.out.println("(Servidor) Conexión establecida.");
            ClientHandler clientHandler = new ClientHandler(socketCliente, servidor);
            clientes.add(clientHandler);
            new Thread(clientHandler).start();
            i++;
        }

        //Cerramos el socket
        servidor.stop();
        System.out.println("Sala Cerrada.");
        */
    }

    // abrimos los canales de lectura y de escritura
    public void abrirCanalesDeTexto() {
        //System.out.println(" (Servidor) Abriendo canales de texto...");
        //Canales de lectura
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        //Canales de escritura
        pw = new PrintWriter(os, true);
        //System.out.println("(Servidor) Cerrando canales de texto.");
    }

    // cerramos los canales de lectura y de escritura
    public void cerrarCanalesDeTexto() throws IOException {
        //System.out.println(" (Servidor) Cerrando canales de texto...");
        //Canales de lectura
        br.close();
        isr.close();
        //Canales de escritura
        pw.close();
        //System.out.println("(Servidor) Cerrando canales de texto.");
    }

    public String leerMensajeTexto()  throws IOException {
        //System.out.println(" (Servidor) Leyendo mensaje...");
        String mensaje = br.readLine();
        //System.out.println(" (Servidor) Mensaje leido.");
        return mensaje;
    }

    public void enviarMensajeTexto(String mensaje) {
        //System.out.println(" (Servidor) Enviando mensaje...");
        pw.println(mensaje);
        //System.out.println(" (Servidor) Mensaje enviado.");
    }

    public void guardarMensajeTexto(String mensaje) {

        try {
            FileWriter fw = new FileWriter("chat.txt", true);
            fw.write("\r\n"+mensaje);
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public static void main (String[] args) {
        try {
            TestServidor servidor = new TestServidor(49175);
            servidor.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void broadcast(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clientes) {
            if (client != sender) {
                client.sendMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm)")) + " " + client.getNombre() + ": " + message);
            }
        }
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm)")) + " " + sender.getNombre() + ": " + message);
    }

    public void stop() throws IOException {
        //System.out.println(" (Servidor) Cerrando conexiones...");
        serverSocket.close();
        //System.out.println (" (Servidor) Conexiones cerradas.");
    }

}
