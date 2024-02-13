package superchat;

import superchat.GUI.Chat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Cliente {

    private String serverIP;
    private int serverPort;
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader reader;

    private PrintWriter pw;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        chat.setNombre(nombre);
    }

    // Nombre del usuario
    private String nombre = "Anónimo";

    // GUI del chat
    private Chat chat;

    public Cliente (String serverIP, int serverPort, Chat chat) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.chat = chat;
    }

    public Cliente (String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    /** Establece la conexión con el servidor
     * @throws IOException Lanza una excepción si no se puede establecer la conexión
     */
    public void start () throws IOException {
        //System.out.println("(Cliente) Estableciendo conexión...");
        socket = new Socket(serverIP,serverPort);
        os = socket.getOutputStream();
        is = socket.getInputStream();
        //System.out.println("(Cliente) Conexión establecida.");
        abrirCanalesDeTexto();

        // Crear un hilo que continuamente lea y muestre los mensajes del servidor
        // Esto es necesario porque el hilo principal se bloquearía al estar leyendo los mensajes
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    // Si el mensaje empieza por Sistema:, es un mensaje interno del servidor y se envia en otro formato
                    if (message.startsWith("Sistema:")){
                        chat.addServerMessage(message);
                        continue;
                    }
                    // Si el mensaje empieza por $disconnect, el servidor ha cerrado la conexión
                    if (message.startsWith("$disconnect")){
                        chat.addServerMessage("El servidor ha cerrado la conexión");
                        stop();
                        chat.dispose();
                        break;
                    }
                    // Si el mensaje empieza por $, es un comando y no se enviará al chat
                    if (message.startsWith("$")){
                        //chat.addServerMessage(message);
                        handleCommands(message);
                        continue;
                    }
                    //System.out.println(message);
                    chat.addMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Error al recibir mensajes del servidor: " + e);
                chat.addServerMessage("Error al recibir mensajes del servidor");
                //e.printStackTrace();
            }
        }).start();
    }

    /** Maneja los comandos recibidos del servidor
     * @param comando Comando recibido del servidor
     */
    private void handleCommands(String comando){
        String[] parts = comando.split(" ");
        switch (parts[0]) {
            case "$setUser":
                this.setNombre(parts[1]);
                break;
            case "$roomName":
                chat.addRoom(parts[1]);
                break;
            case "$setRoom":
                chat.setRoomLabel(parts[1]);
                break;
            case "$login":
                //chat.addServerMessage("Usuario " + parts[1] + " ha iniciado sesión");
                if (loginLatch != null) {
                    loginLatch.countDown();
                }
                if (parts[1].equals("true")){
                    loggedUser = true;
                }
                break;
            default:
                chat.addServerMessage("Comando no reconocido: " + comando);
                System.out.println("Comando no reconocido: " + comando);
                break;
        }

    }


    private boolean loggedUser = false;
    // Este objeto es para bloquear el hilo hasta que nos llegue una respuesta del servidor.
    private CountDownLatch loginLatch;

    public boolean isLoggedIn() {
        return loggedUser;
    }

    /** Comprueba si el usuario y contraseña son correctos
     * @param user Nombre de usuario
     * @param pass Contraseña
     * @return true si el usuario y contraseña son correctos, false en caso contrario
     */
    public boolean checkLogin(String user, String pass) throws InterruptedException, TimeoutException {
        loginLatch = new CountDownLatch(1);
        enviarMensajeTexto("$checkLogin " + user + " " + pass);
        // Esperar 5 segundos a que el servidor responda
        if (!loginLatch.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("Se ha acabado el tiempo de espera para el login");
        }
        if (loggedUser){
            nombre = user;
        }
        return loggedUser;
    }

    /** Cierra el cliente
     * @throws IOException Lanza una excepción si no se puede cerrar el socket
     */
    public void stop () throws IOException {
        // El socket al cerrarse ya cierra los canales de entrada y salida automáticamente
        socket.close();
    }

    /**
     * Abre los canales de texto para enviar y recibir mensajes
     */
    public void abrirCanalesDeTexto() {
        //Canales de lectura
        isr = new InputStreamReader(is);
        reader = new BufferedReader(isr);
        //Canales de escritura
        pw = new PrintWriter(os, true);
    }

    /**
     * Cierra los canales de texto
     */
    public void cerrarCanalesDeTexto() throws IOException {
        //Canales de lectura
        reader.close();
        isr.close();
        //Canales de escritura
        pw.close();
    }

    /** Lee un mensaje de texto del servidor
     * @param mensaje Mensaje a enviar
     */
    public void enviarMensajeTexto(String mensaje) {
        pw.println(mensaje);
    }


    public static void main (String[] args) {

        //Abrimos la comunicación con el puerto de servicio
        Cliente cliente = new Cliente("localhost",49175);
        try {
            //Abrimos la comunicación
            cliente.start();
            cliente.abrirCanalesDeTexto();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        String mensaje;

        //Abrimos la comunicación con el puerto de servicio
        Cliente cliente = new Cliente("localhost",49175);
        try {
            //Abrimos la comunicación
            cliente.start();
            cliente.abrirCanalesDeTexto();
            do {

                //Enviar mensajes al servidor
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("Mensaje a enviar (END para terminar): ");
                mensaje = br.readLine();
                cliente.enviarMensajeTexto(mensaje);

                //Recepción de la confirmacion
                //String mensajeRecibido = cliente.leerMensajeTexto();
                //System.out.println ( "Mensaje del servidor:"+mensajeRecibido);

                //Cerramos la comunicación


            } while (!mensaje.equals("END"));
            cliente.cerrarCanalesDeTexto();
            cliente.stop();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }
}
