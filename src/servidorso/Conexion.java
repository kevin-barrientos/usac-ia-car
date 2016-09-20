/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Rodrigo
 */
public class Conexion extends Thread {

    private final Socket socket;
    private final int clientNumber;
    private final Car car;

    public Conexion(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.car = null;
        log("New connection with client# " + clientNumber + " at " + socket);
    }

    Conexion(Socket socket, int clientNumber, Car car) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.car = car;
        log("New connection with client# " + clientNumber + " at " + socket);
    }

    /**
     * Services this thread client by first sending the client a welcome message
     * then repeatedly reading strings and sending back the processed response.
     */
    @Override
    public void run() {
        
        CommandInterpreter commandInterpreter = new CommandInterpreter(car);

        try {

            // Decorate the streams so we can send characters
            // and not just bytes.  Ensure output is flushed
            // after every newline.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Get messages from the client, line by line;
            while (true) {
                String input = in.readLine();
                if (input == null || input.equals(".")) {
                    break;
                }
                log(input.toUpperCase());

                try {
                    String result = commandInterpreter.executeCommand(Integer.valueOf(input));
                    out.println(result);
                } catch (NumberFormatException e) {
                    if(!car.tune(input))
                        out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            log("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            log("Connection with client# " + clientNumber + " closed");
        }
    }

    /**
     * Logs a simple message. In this case we just write the message to the
     * server applications standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }
}
