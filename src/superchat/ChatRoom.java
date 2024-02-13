package superchat;

import superchat.Helper.Historial;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static superchat.Helper.Auxiliar.formatearMensaje;
import static superchat.Helper.Historial.guardarMensajeTexto;

/**
 * Clase que representa una sala de chat
 */
public class ChatRoom {
    /**
     * Lista de clientes conectados a la sala
     */
    private List<ClientHandler> clients = new ArrayList<>();

    /**
     * Nombre de la sala
     */
    private String nombre;

    public ChatRoom(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @param client Añade un cliente a la sala
     */
    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    /**
     * @param client Elimina un cliente de la sala
     */
    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    /** Envía un mensaje a todos los clientes conectados, menos al que lo envía, y lo guarda en el archivo de texto
     * @param message Mensaje a enviar
     * @param sender Cliente que envía el mensaje
     * @throws IOException Lanza una excepción si no se puede enviar el mensaje
     */
    public synchronized void broadcast(String message, ClientHandler sender) throws IOException {
        //String formattedMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm)")) + " " + sender.getNombre() + ": " + message;
        String formattedMessage = formatearMensaje(message, sender.getNombre());
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
        // TODO quitar esto o dejarlo como LOG
        System.out.println(formattedMessage);
        // Guarda en el archivo de texto
        guardarMensajeTexto(formattedMessage, nombre);
    }

    /**
     * @return Nombre de la sala
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre Nombre de la sala
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
