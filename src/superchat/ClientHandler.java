package superchat;

import java.io.*;
import java.net.Socket;

/**
 * Clase para manejar las conexiones con los clientes
 */
class ClientHandler extends Thread {
    private Socket socket;
    private Servidor server;

    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    // Nombre del cliente, por defecto "Anónimo"
    private String nombre = "Anónimo";

    // Sala de texto a la que está conectado el cliente
    private ChatRoom salaTexto;


    /** Constructor de la clase
     * @param socket Conexión con el cliente
     * @param server Servidor
     * @throws IOException Error de entrada/salida
     */
    public ClientHandler(Socket socket, Servidor server) throws IOException {
        this.socket = socket;
        this.server = server;

        is = socket.getInputStream();
        os = socket.getOutputStream();
        System.out.println("(Servidor) Conexión establecida con cliente: " + socket.getRemoteSocketAddress());

        //Canales de lectura
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);

        //Canales de escritura
        pw = new PrintWriter(os, true);

    }

    /** Envía un mensaje al cliente
     * @param message Mensaje a enviar
     */
    public void sendMessage(String message) {
        pw.println(message);
    }

    /** Envía un mensaje al cliente desde el servidor
     * @param message Mensaje a enviar
     */
    public void sendServerMessage(String message) {
        pw.println("Sistema: " + message);
    }

    @Override
    public void run() {
        // Aquí se manejarán los mensajes del cliente
        try {
            String mensaje;
            while ((mensaje = br.readLine()) != null) {
                //System.out.println("Mensaje del cliente: " + mensaje);
                // Si el mensaje empieza por /, es un comando y no se enviará al chat
                if (mensaje.startsWith("/") || mensaje.startsWith("$")) {
                    server.commands(mensaje, this);
                    // Sigue el loop para que no se envíe el mensaje al chat
                    continue;
                }

                //server.broadcast(mensaje, this);
                salaTexto.broadcast(mensaje, this);
            }
        } catch (IOException e) {
            System.err.println("Perdida conexión con el cliente: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("No se ha podido cerrar el socket: " + e);
            }
        }
    }


    /**
     * Desconecta al cliente
     */
    public void disconnect () throws IOException {
        //System.out.println("(Cliente) Cerrando conexiones.");
        is.close();
        os.close();
        socket.close();
        //System.out.println(" (Cliente) Conexiones cerradas.");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSalaTexto(ChatRoom salaTexto) {
        this.salaTexto = salaTexto;
    }

    public ChatRoom getSalaTexto() {
        return salaTexto;
    }
}
