package org.example.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private BufferedReader serverReader;
    private PrintWriter writer;
    private Scanner scanner;
    private boolean serverFinished = false;

    public void run(String host, int port) {
        try (Socket clientSocket = new Socket(host, port)) {
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            scanner = new Scanner(System.in);

            Thread serverReaderThread = new Thread(this::readFromServer);
            serverReaderThread.start();

            while (!serverFinished) {
                writeToServer();
            }

        } catch (UnknownHostException e) {
            logger.log(Level.WARNING, "Server not found: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.WARNING, "An IO exception occurred: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    serverReader.close();
                    writer.close();
                    scanner.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing resources: " + e.getMessage());
            }
        }
    }

    private void readFromServer() {
        try {
            String serverLine;
            while ((serverLine = serverReader.readLine()) != null) {
                System.out.println("Повідомлення сервера: " + serverLine);
                if (serverLine.contains("З'єднання буде зачинено")) {
                    serverFinished = true;
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO exception while reading from server: " + e.getMessage());
        }
    }

    private void writeToServer() {
        System.out.print("Написати на сервер: ");
        if (scanner.hasNextLine()) {
            String clientLine = scanner.nextLine();
            writer.println(clientLine);
        } else {
            logger.warning("Scanner has no more input.");
        }
    }
}
