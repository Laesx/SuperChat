package superchat.Test;

import superchat.Cliente;

import java.io.IOException;

public class TestMultipleClients {
    public static void main(String[] args) {
        try {
            Cliente client1 = new Cliente("localhost", 49175);
            Cliente client2 = new Cliente("localhost", 49175);
            Cliente client3 = new Cliente("localhost", 49175);

            TestClienteExterno testClient1 = new TestClienteExterno("Client 1", client1);
            TestClienteExterno testClient2 = new TestClienteExterno("Client 2", client2);
            TestClienteExterno testClient3 = new TestClienteExterno("Client 3", client3);

            testClient1.start();
            testClient2.start();
            testClient3.start();

            for (int i = 0; i < 10; i++) {
                System.out.println("Main thread: " + i);
                Thread.sleep(1000);
                testClient1.enviarMensajeTexto("Hello from client 1");
                Thread.sleep(1000);
                testClient1.enviarMensajeTexto("Hello from client 2");
                Thread.sleep(1000);
                testClient1.enviarMensajeTexto("Hello from client 3");

            }

            testClient1.enviarMensajeTexto("END");
            testClient1.enviarMensajeTexto("END");
            testClient1.enviarMensajeTexto("END");

            client1.stop();
            client2.stop();
            client3.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}