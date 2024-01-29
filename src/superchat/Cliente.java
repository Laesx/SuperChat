package superchat;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    private String serverIP;
    private int serverPort;
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    public Cliente (String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void start () throws UnknownHostException, IOException {
        //System.out.println("(Cliente) Estableciendo conexión...");
        socket = new Socket(serverIP,serverPort);
        os = socket.getOutputStream();
        is = socket.getInputStream();
        //System.out.println("(Cliente) Conexión establecida.");

        // Start a new thread that will continuously read and print messages from the server
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Mensaje del servidor: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop () throws IOException {
        //System.out.println("(Cliente) Cerrando conexiones.");
        is.close();
        os.close();
        socket.close();
        //System.out.println(" (Cliente) Conexiones cerradas.");
    }

    // abrimos los canales de lectura y de escritura - Igual que en el servidor
    public void abrirCanalesDeTexto() {
        //System.out.println(" (Cliente) Abriendo canales de texto...");
        //Canales de lectura
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        //Canales de escritura
        pw = new PrintWriter(os, true);
        //System.out.println("(Cliente) Cerrando canales de texto.");
    }

    // cerramos los canales de lectura y de escritura - Igual que en el servidor
    public void cerrarCanalesDeTexto() throws IOException {
        //System.out.println(" (Cliente) Cerrando canales de texto...");
        //Canales de lectura
        br.close();
        isr.close();
        //Canales de escritura
        pw.close();
        //System.out.println("(Cliente) Cerrando canales de texto.");
    }

    public String leerMensajeTexto()  throws IOException {
        //System.out.println(" (Cliente) Leyendo mensaje...");
        String mensaje = br.readLine();
        //System.out.println(" (Cliente) Mensaje leido.");
        return mensaje;
    }

    public void enviarMensajeTexto(String mensaje) {
        //System.out.println(" (Cliente) Enviando mensaje...");
        pw.println(mensaje);

        //System.out.println(" (Cliente) Mensaje enviado.");
    }


    public static void main (String[] args) {


        String mensaje;

        //Abrimos la comunicación con el puerto de servicio
        Cliente cliente = new Cliente("localhost",49175);
        try {
            do {
                //Abrimos la comunicación
                cliente.start();
                cliente.abrirCanalesDeTexto();

                //Enviar mensajes al servidor
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Mensaje a enviar (END para terminar): ");
                mensaje = br.readLine();
                cliente.enviarMensajeTexto(mensaje);

                //Recepción de la confirmacion
                //String mensajeRecibido = cliente.leerMensajeTexto();
                //System.out.println ( "Mensaje del servidor:"+mensajeRecibido);

                //Cerramos la comunicación
                cliente.cerrarCanalesDeTexto();

            } while (!mensaje.equals("END"));
            cliente.stop();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
