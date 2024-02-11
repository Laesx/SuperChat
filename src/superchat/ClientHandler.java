package superchat;

import java.io.*;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket socket;
    private Servidor server;

    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    private String nombre = "Anónimo";

    private ChatRoom salaTexto;


    public ClientHandler(Socket socket, Servidor server) throws IOException {
        this.socket = socket;
        this.server = server;
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //out = new PrintWriter(socket.getOutputStream(), true);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        System.out.println("(ServidorAntiguo) Conexión establecida con cliente: " + socket.getRemoteSocketAddress());

        //Canales de lectura
        // ME PARECE QUE NO HACEN FALTA AQUI
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);

        //Canales de escritura
        pw = new PrintWriter(os, true);

    }

    public void sendMessage(String message) {
        pw.println(message);
    }

    public void sendServerMessage(String message) {
        pw.println("Sistema: " + message);
    }

    @Override
    public void run() {
        try {
            String mensaje;
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((mensaje = br.readLine()) != null) {
                // Si el mensaje empieza por /, es un comando y no se enviará al chat
                if (mensaje.startsWith("/")){
                    //String[] parts = mensaje.split(" ");
                    //this.setNombre(parts[1]);

                    server.comando(mensaje, this);
                    // Sigue el loop para que no se envíe el mensaje al chat
                    continue;
                }
                server.broadcast(mensaje, this);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
