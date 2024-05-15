package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private BufferedReader clientReader;
    private PrintWriter writer;
    private Socket clientSocket;

    public void run(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server on port: " + port);
            clientSocket = serverSocket.accept();
            logger.info("The client has arrived.");

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                 BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                this.writer = writer;
                this.clientReader = clientReader;

                writer.println("Hello!");

                String clientInput = readClient();
                if (!checkClientsGreetings(clientInput)) {
                    this.extraQuestion();
                } else {
                    writer.println("Вітаю, відповідь правільна. До зустрічі!");
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, "IO exception occurred: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Server socket IO exception: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                    logger.info("Client connection closed.");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while closing client socket: " + e.getMessage());
            }
        }
    }

    private void extraQuestion() throws IOException {
        writer.println("Що таке паляниця?");
        logger.info("The client turned off and asked about the 'паляниця'.");

        String clientInput = readClient();
        if (checkClientsPalianytsiaAnswer(clientInput)) {
            writer.println("Відповідь правильна! " + LocalDateTime.now());
        } else {
            writer.println("Відповідь неправильна!!!");
        }
    }

    private String readClient() throws IOException {
        logger.info("Waiting for a response from the client...");
        String clientInput = clientReader.readLine();
        logger.info("The client sent: " + clientInput);
        return clientInput;
    }

    private boolean checkClientsGreetings(String clientInput) {
        if (clientInput == null || clientInput.isEmpty() || clientInput.equalsIgnoreCase("привет")) {
            logger.info("The client did not say hello in Ukrainian.");
            return false;
        }
        logger.info("The client greeted in Ukrainian.");
        return true;
    }

    private boolean checkClientsPalianytsiaAnswer(String clientInput) {
        if (!"хліб".equalsIgnoreCase(clientInput)) {
            logger.info("The client answered incorrectly, the connection will be interrupted");
            return false;
        }
        logger.info("The client answered correctly, he will be notified of the current date and time.");
        return true;
    }
}
