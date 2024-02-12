package superchat.Helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Auxiliar {
    public static String dameFechaActual() {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
        // Formatear la fecha en un formato específico, por ejemplo, "dd/MM/yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = fechaActual.format(formatter);
        return fechaFormateada;
    }

    public static String horaActual() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    public static String formatearMensaje(String mensaje, String nombre) {
        return "[" + horaActual() + "] <" + nombre + ">: " + mensaje;
    }
}