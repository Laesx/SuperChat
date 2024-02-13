package superchat.Helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase con métodos auxiliares
 */
public class Auxiliar {
    /**
     * @return Fecha actual en formato HH:mm:ss
     */
    public static String horaActual() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    /** Formatea un mensaje para que aparezca con la hora y el nombre del usuario
     * @param mensaje Mensaje a formatear
     * @param nombre Nombre del usuario que envía el mensaje
     * @return Mensaje formateado
     */
    public static String formatearMensaje(String mensaje, String nombre) {
        return "[" + horaActual() + "] <" + nombre + ">: " + mensaje;
    }
}