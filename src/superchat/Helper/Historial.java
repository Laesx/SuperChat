package superchat.Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Historial {


    // Guarda el mensaje en un archivo de texto
    public static void guardarMensajeTexto(String mensaje, String nombreSala) {
        try {
            FileWriter fw = new FileWriter("ficheros/chat_"+ nombreSala +".txt", true);
            fw.write("\r\n" + mensaje);
            fw.close();
        } catch (Exception e) {
            System.out.println("Error guardando el archivo de texto: " + e);
        }
    }// Recupera el chat desde el archivo de texto

    public static String recuperarChat(String nombreSala) {
        String chat = "";
        try {
            FileReader fr = new FileReader("ficheros/chat_"+ nombreSala +".txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                chat += linea + "\n";
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("Error recuperando el archivo de texto: " + e);
        }
        return chat;
    }
}