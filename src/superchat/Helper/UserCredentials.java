package superchat.Helper;

import java.io.*;
import java.util.*;

/**
 * Clase para manejar las credenciales de los usuarios
 * Las credenciales se encriptan y se guardan en un archivo de texto
 */
public class UserCredentials {
    private static final String CREDENTIALS_FILE = "usuarios.txt";

    /** Guarda las credenciales del usuario en un archivo de texto
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public static void storeCredentials(String username, String password) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true)))) {
            String encryptedUsername = CipherHelper.encrypt(username);
            String encryptedPassword = CipherHelper.encrypt(password);
            out.println(encryptedUsername + "," + encryptedPassword);
        } catch (IOException e) {
            System.out.println("Error guardando en el archivo: " + e.getMessage());
        }
    }

    /** Comprueba si las credenciales del usuario son correctas
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si las credenciales son correctas, false en caso contrario
     */
    public static boolean checkCredentials(String username, String password) {
        try (BufferedReader in = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] credentials = line.split(",");
                String decryptedUsername = CipherHelper.decrypt(credentials[0]);
                String decryptedPassword = CipherHelper.decrypt(credentials[1]);
                if (decryptedUsername.equals(username) && decryptedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
        }
        return false;
    }
}
