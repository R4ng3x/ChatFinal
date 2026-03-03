package ChatFinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Observable implements Runnable {

    private int puerto;

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public void run() {

        ServerSocket servidor = null;

        try {
            //Creamos el socket del servidor
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor iniciado");

            //Siempre estara escuchando peticiones
            while (true) {
                Socket sc = null;
                DataInputStream in;

                try {
                    //Espero a que un cliente se conecte
                    sc = servidor.accept();

                    // Add a timeout of 5 seconds to prevent slowloris/DoS attacks
                    // where a client connects but sends no data, blocking the server.
                    sc.setSoTimeout(5000);

                    System.out.println("Cliente conectado");
                    in = new DataInputStream(sc.getInputStream());

                    //Leo el mensaje que me envia
                    String mensaje = in.readUTF();

                    System.out.println(mensaje);

                    this.setChanged();
                    this.notifyObservers(mensaje);
                    this.clearChanged();

                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout waiting for client data. Closing connection.");
                } catch (IOException e) {
                    System.out.println("Error reading from client: " + e.getMessage());
                } finally {
                    if (sc != null) {
                        try {
                            //Cierro el socket
                            sc.close();
                            System.out.println("Cliente desconectado");
                        } catch (IOException e) {
                            System.out.println("Error closing socket: " + e.getMessage());
                        }
                    }
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
