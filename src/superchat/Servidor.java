package superchat;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static superchat.Helper.Historial.guardarMensajeTexto;
import static superchat.Helper.Historial.recuperarChat;
import static superchat.Helper.UserCredentials.checkCredentials;

public class Servidor {
    private ServerSocket serverSocket;

    private static ArrayList<ClientHandler> clientes = new ArrayList<>();

    private ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    public Servidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    public void start () throws IOException {
        System.out.println("(Servidor) Esperando conexiones...");
        chatRooms.add(new ChatRoom("#General"));
        chatRooms.add(new ChatRoom("#Offtopic"));
        chatRooms.add(new ChatRoom("#Juegos"));
        while (true) {
            Socket socket=serverSocket.accept();
            //ClientHandler clientThread =
            inicializarCliente(new ClientHandler(socket, this));
        }

        /*
        //Cerramos el socket
        servidor.stop();
        System.out.println("Sala Cerrada.");
        */
    }

    /** Inicializa un cliente que se ha conectado al servidor
     * @param clientThread El hilo del cliente que se ha conectado
     */
    private void inicializarCliente(ClientHandler clientThread) {
        clientes.add(clientThread);

        // TODO TEST PARA SALAS
        chatRooms.get(0).addClient(clientThread);
        clientThread.setSalaTexto(chatRooms.get(0));

        clientThread.start();

        // Enviar mensajes de bienvenida y parámetros iniciales
        clientThread.sendMessage("$setUser " + clientThread.getNombre());
        clientThread.sendMessage("$setRoom " + chatRooms.get(0).getNombre());
        clientThread.sendServerMessage("Bienvenido a SuperChat. Escribe /help para ver los comandos disponibles.");
        clientThread.sendServerMessage("Te has unido a " + chatRooms.get(0).getNombre());
        // TODO Provisionalmente llamar a un método que envíe el chat a los clientes
        // Que lo maneje el cliente con la polla
        clientThread.sendMessage(recuperarChat(clientThread.getSalaTexto().getNombre()));
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
        //guardarMensajeTexto(formattedMessage);
    }

    // TODO Esto seguramente habría que hacerlo más seguro para que los usuarios no puedan enviar comandos que no deben
    public void commands(String mensaje, ClientHandler sender) throws IOException {
        String[] parts = mensaje.split(" ");
        switch (parts[0]) {
            case "/nick":
                sender.setNombre(parts[1]);
                sender.sendServerMessage("Tu nuevo nombre es " + parts[1]);
                sender.sendMessage("$setUser " + sender.getNombre());
                break;
            case "/list":
                // TODO Implementar un método para enviar la lista de clientes
                for (ClientHandler client : clientes) {
                    sender.sendMessage("Sistema: "+ client.getNombre());
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
                sender.sendServerMessage("Adiós, " + sender.getNombre());
                //sender.sendMessage("Server: Adiós, " + sender.getNombre());
                sender.sendMessage("$disconnect");
                //sender.disconnect();
                // Implementar un método para cerrar la conexión
                clientes.remove(sender);
                break;
            case "/help":
                sender.sendServerMessage("Comandos disponibles: /nick, /list, /msg, /quit, /help");
                sender.sendServerMessage("Comandos de sala: /create, /join, /leave, /room");
                //sender.sendServerMessage("Para saber más sobre un comando, escribe /help [comando]");
                break;
            case "/join", "$joinRoom":
                for (ChatRoom chatRoom : chatRooms) {
                    if (chatRoom.getNombre().equals(parts[1])) {
                        // Añade al cliente a la sala
                        chatRoom.addClient(sender);
                        // Borra al cliente de la sala en la que ya estaba
                        sender.getSalaTexto().removeClient(sender);
                        // Cambia la sala del cliente
                        sender.setSalaTexto(chatRoom);
                        sender.sendServerMessage("Te has unido a " + parts[1]);
                    }
                }
                break;
            case "/leave":
                for (ChatRoom chatRoom : chatRooms) {
                    if (chatRoom.getNombre().equals(parts[1])) {
                        chatRoom.removeClient(sender);
                        sender.setSalaTexto(null);
                        sender.sendServerMessage("Has abandonado " + parts[1]);
                    }
                }
                break;
            case "/room":
                sender.sendServerMessage("Sala de texto actual: " + sender.getSalaTexto().getNombre());
                break;
            // Esta sección es para comandos que vienen internamente del cliente
            case "$getMessages":
                sender.sendMessage(recuperarChat(sender.getSalaTexto().getNombre()));
                break;
            case "$getRooms":
                //System.out.println("Enviando lista de salas");
                /*
                String listaSalas = "";
                for (ChatRoom chatRoom : chatRooms) {
                    listaSalas += "$roomName" + chatRoom.getNombre() + "\n";
                }
                sender.sendMessage(listaSalas);
                */
                for (ChatRoom chatRoom : chatRooms) {
                    sender.sendMessage("$roomName " + chatRoom.getNombre());
                }
                break;
            case "$checkLogin":
                boolean loginCheck = checkCredentials(parts[1], parts[2]);
                if (loginCheck) {
                    sender.sendMessage("$login true");
                    sender.setNombre(parts[1]);
                } else {
                    sender.sendMessage("$login false");
                }
                break;
            default:
                sender.sendServerMessage("Comando no reconocido. Usa /help para ver los comandos disponibles.");
                break;
        }
    }



    public void stop() throws IOException {
        //System.out.println(" (ServidorAntiguo) Cerrando conexiones...");
        serverSocket.close();
        //System.out.println (" (ServidorAntiguo) Conexiones cerradas.");
    }

}
