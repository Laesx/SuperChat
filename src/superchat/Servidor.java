package superchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Servidor {

    private ServerSocket serverSocket;

    private static ArrayList<ClientHandler> clientes = new ArrayList<>();

    private ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    public Servidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    public void start () throws IOException {
        System.out.println(" (Servidor) Esperando conexiones...");
        while (true) {
            Socket socket=serverSocket.accept();
            ClientHandler clientThread = new ClientHandler(socket, this);
            clientes.add(clientThread);
            clientThread.start();
            // Provisionalmente llamar a un método que envíe el chat a los clientes
            clientThread.sendMessage(recuperarChat());
        }

        /*
        //Cerramos el socket
        servidor.stop();
        System.out.println("Sala Cerrada.");
        */
    }

    // abrimos los canales de lectura y de escritura
    public void abrirCanalesDeTexto() {
        //System.out.println(" (ServidorAntiguo) Abriendo canales de texto...");
        //Canales de lectura
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        //Canales de escritura
        pw = new PrintWriter(os, true);
        //System.out.println("(ServidorAntiguo) Cerrando canales de texto.");
    }

    // cerramos los canales de lectura y de escritura
    public void cerrarCanalesDeTexto() throws IOException {
        //System.out.println(" (ServidorAntiguo) Cerrando canales de texto...");
        //Canales de lectura
        br.close();
        isr.close();
        //Canales de escritura
        pw.close();
        //System.out.println("(ServidorAntiguo) Cerrando canales de texto.");
    }

    public void guardarMensajeTexto(String mensaje) {

        try {
            FileWriter fw = new FileWriter("chat.txt", true);
            fw.write("\r\n"+mensaje);
            fw.close();
        } catch (Exception e) {
            System.out.println("Error guardando el archivo de texto: " + e);
        }
    }

    public String recuperarChat(){
        String chat = "";
        try {
            FileReader fr = new FileReader("chat.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while((linea = br.readLine()) != null){
                chat += linea + "\n";
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("Error recuperando el archivo de texto: " + e);
        }
        return chat;
    }



    public static void main (String[] args) {
        try {
            Servidor servidor = new Servidor(49175);
            servidor.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void broadcast(String message, ClientHandler sender) throws IOException {
        String formattedMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm)")) + " " + sender.getNombre() + ": " + message;
        for (ClientHandler client : clientes) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
        System.out.println(formattedMessage);
        // Lo guarda en el archivo de texto
        guardarMensajeTexto(formattedMessage);
    }

    public void comando(String mensaje, ClientHandler sender) throws IOException {
        String[] parts = mensaje.split(" ");
        switch (parts[0]) {
            case "/nick":
                sender.setNombre(parts[1]);
                sender.sendServerMessage("Tu nuevo nombre es " + parts[1]);
                sender.sendMessage("$setUser " + sender.getNombre());
                break;
            case "/list":
                for (ClientHandler client : clientes) {
                    sender.sendMessage("Server: "+ client.getNombre());
                }
                break;
            case "/msg":
                for (ClientHandler client : clientes) {
                    if (client.getNombre().equals(parts[1])) {
                        client.sendMessage(sender.getNombre() + " te susurra: " + mensaje.substring(parts[0].length() + parts[1].length() + 2));
                    }
                }
                break;
            case "/quit":
                sender.sendMessage("Server: Adiós, " + sender.getNombre());
                //sender.stop();
                // Implementar un método para cerrar la conexión
                //clientes.remove(sender);
                break;
            case "/help":
                sender.sendMessage("Server: Comandos disponibles: /nick, /list, /msg, /quit, /help");
                break;
            default:
                sender.sendMessage("Server: Comando no reconocido. Usa /help para ver los comandos disponibles.");
                break;
        }
    }



    public void stop() throws IOException {
        //System.out.println(" (ServidorAntiguo) Cerrando conexiones...");
        serverSocket.close();
        //System.out.println (" (ServidorAntiguo) Conexiones cerradas.");
    }

}
