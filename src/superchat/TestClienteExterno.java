package superchat;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestClienteExterno extends Thread{

    private Cliente cliente;
    private String mensaje;

    public TestClienteExterno(String mensaje, Cliente cliente){
        this.mensaje = mensaje;
        this.cliente = cliente;
    }

    @Override
    public void run(){

        try {
            cliente.start();
            enviarMensajeTexto(mensaje);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //stop
    public void stopCliente() throws IOException {
        cliente.stop();
    }

    public void enviarMensajeTexto(String mensaje) throws IOException {
        //System.out.println(" (Cliente) Enviando mensaje...");
        cliente.abrirCanalesDeTexto();

        //Enviar mensajes al servidor
        cliente.enviarMensajeTexto(mensaje);

        //Recepción de la confirmacion
        String mensajeRecibido = cliente.leerMensajeTexto();
        System.out.println ( "Mensaje del servidor:"+mensajeRecibido);

        //Cerramos la comunicación
        cliente.cerrarCanalesDeTexto();
        //System.out.println(" (Cliente) Mensaje enviado.");
    }
}
