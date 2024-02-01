package superchat;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

class ClientHandler extends Thread {
    private Socket socket;
    private TestServidor server;

    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    private String nombre = "Anónimo";


    public ClientHandler(Socket socket, TestServidor server) throws IOException {
        this.socket = socket;
        this.server = server;
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //out = new PrintWriter(socket.getOutputStream(), true);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        System.out.println("(Servidor) Conexión establecida con cliente: " + socket.getRemoteSocketAddress());

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

    @Override
    public void run() {
        try {
            String mensaje;
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((mensaje = br.readLine()) != null) {
                server.broadcast(mensaje, this);
            }
            /*
            do {
                //servidor.abrirCanalesDeTexto();

                // Tomamos la hora en la que se produce la interaccion
                LocalDateTime horaLocal = LocalDateTime.now();
                int horas  = horaLocal.getHour();
                int minutos = horaLocal.getMinute();
                int segundos = horaLocal.getSecond();

                //Recepcion del mensaje del cliente
                //mensaje = servidor.leerMensajeTexto();

                mensaje = br.readLine();

                //String salida=horas+":"+minutos+":"+segundos+" - Host "+host+": "+mensaje;
                //System.out.println(salida);
                //servidor.guardarMensajeTexto(salida);

                //Envío de la confirmacion del mensaje al cliente
                //servidor.enviarMensajeTexto("ACK "+horas+":"+minutos+":"+segundos);
                pw.println("ACK "+horas+":"+minutos+":"+segundos);

                //Cerramos el canal
                //servidor.cerrarCanalesDeTexto();

            } while (mensaje != null && !mensaje.equals("END"));
            */
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
