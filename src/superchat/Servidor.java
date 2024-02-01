/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package superchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author losgu
 */
public class Servidor {

    // Objetos para conexion
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    public Servidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    // igual que en el ejemplo anterior
    public SocketAddress start () throws IOException {
        //System.out.println(" (Servidor) Esperando conexiones...");
        socket=serverSocket.accept();
        is = socket.getInputStream();
        os = socket.getOutputStream();
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm:ss)")) +
                " (Servidor) Conexión establecida con cliente "+socket.getRemoteSocketAddress());
        SocketAddress sa = socket.getRemoteSocketAddress();
        return(sa);
    }

    // igual que en el ejemplo anterior
    public void stop() throws IOException {
        //System.out.println(" (Servidor) Cerrando conexiones...");
        is.close();
        os.close();
        socket.close();
        serverSocket.close();
        //System.out.println (" (Servidor) Conexiones cerradas.");
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

    public String identificarHost (SocketAddress IPcliente) {
        String cliente=IPcliente.toString();

        //Buscamos la posicion del tercer punto
        int puntos=0;
        int pos3punto=0;
        int i=0;
        while (puntos<3) {
            if(cliente.charAt(i) == '.') {
                puntos++;
                pos3punto=i;
            }
            i++;
        }
        //Buscamos la posicion de los dos puntos
        int posDosPuntos=cliente.indexOf(":");
        cliente=cliente.substring(pos3punto+1,posDosPuntos);

        return cliente;
    }

    public String identificarHost (Socket IPcliente) {
        String cliente=IPcliente.getLocalSocketAddress().toString();

        //Buscamos la posicion del tercer punto
        int puntos=0;
        int pos3punto=0;
        int i=0;
        while (puntos<3) {
            if(cliente.charAt(i) == '.') {
                puntos++;
                pos3punto=i;
            }
            i++;
        }
        //Buscamos la posicion de los dos puntos
        int posDosPuntos=cliente.indexOf(":");
        cliente=cliente.substring(pos3punto+1,posDosPuntos);

        return cliente;
    }

    public static void main (String[] args) {

        String mensaje;
        try {
            //Iniciamos
            Servidor servidor = new Servidor(49175);
            System.out.println("Sala Abierta.");

            // Hacer funcion para que genere un nuevo puerto para cada cliente y que compruebe que esta libre
            int i = 0;
            while (i < 5){
                Socket testSocket = servidor.serverSocket.accept();
                //new ClientHandler(testSocket).start();
                i++;
            }


            do {
                //SocketAddress IPcliente = servidor.start();
                Socket testSocket = servidor.serverSocket.accept();
                //new ClientHandler(testSocket).start();
                // Hacer funcion para que genere un nuevo puerto para cada cliente y que compruebe que esta libre
                //String host = servidor.identificarHost(IPcliente);
                String host = servidor.identificarHost(testSocket);
                servidor.abrirCanalesDeTexto();

                // Tomamos la hora en la que se produce la interaccion
                LocalDateTime horaLocal = LocalDateTime.now();
                int horas  = horaLocal.getHour();
                int minutos = horaLocal.getMinute();
                int segundos = horaLocal.getSecond();

                //Recepcion del mensaje del cliente
                mensaje = servidor.leerMensajeTexto();
                String salida=horas+":"+minutos+":"+segundos+" - Host "+host+": "+mensaje;
                System.out.println(salida);
                servidor.guardarMensajeTexto(salida);

                //Envío de la confirmacion del mensaje al cliente
                servidor.enviarMensajeTexto("ACK "+horas+":"+minutos+":"+segundos);

                //Cerramos el canal
                servidor.cerrarCanalesDeTexto();

            } while (!mensaje.equals("close"));

            //Cerramos el socket
            servidor.stop();
            System.out.println("Sala Cerrada.");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
}
