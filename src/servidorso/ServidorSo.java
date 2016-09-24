/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorso;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author Rodrigo
 */
public class ServidorSo {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("The server is running.");
        int clientNumber = 0;
        final Car mCar = new Car();
        try (ServerSocket listener = new ServerSocket(9898)) {
            while (true) {
                new Conexion(listener.accept(), clientNumber++, mCar).start();
            }
        }
    }

}
