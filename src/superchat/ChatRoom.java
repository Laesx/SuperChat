package superchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
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
