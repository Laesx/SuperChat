package superchat;

import superchat.Helper.Historial;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static superchat.Helper.Historial.guardarMensajeTexto;

public class ChatRoom {
    private List<ClientHandler> clients = new ArrayList<>();

    private String nombre;

    public ChatRoom(String nombre) {
        this.nombre = nombre;
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized void broadcast(String message, ClientHandler sender) throws IOException {
        // TODO Standarizar esto a lo mejor?
        String formattedMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(HH:mm)")) + " " + sender.getNombre() + ": " + message;
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
        System.out.println(formattedMessage);
        // Lo guarda en el archivo de texto
        guardarMensajeTexto(formattedMessage, nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
