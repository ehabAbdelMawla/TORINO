/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.validation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.Logger;

/**
 *
 * @author Bayoumi
 */
public class SingleInstance {

    private static SingleInstance singleInstance = null;

    private ServerSocket server;
    private final int PORT = 12348;
    private Stage currentStage;

    private SingleInstance() {
        singleInstanceApplicationCheck();
    }

    public static SingleInstance getInstance() {
        if (singleInstance == null) {
            singleInstance = new SingleInstance();
        }
        return singleInstance;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    private void singleInstanceApplicationCheck() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            System.out.println("InetAddress.getLocalHost(): " + localAddress + ":" + PORT);
            server = new ServerSocket(PORT, 1, localAddress);
            System.out.println("Server Online ...");
            // listen to other Instances
            new Thread(() -> {
                try {
                    while (true) {
                        Socket serverClient = server.accept();  //server accept the client connection request
                        listenToInstance(serverClient);
                    }
                } catch (Exception ex) {
                    Logger.writeLog("Error in " + getClass().getName() + ".singleInstanceApplicationCheck() => listen to other Instances => " + ex);
                }
            }).start();
        } catch (IOException iOException) {
            // port is taken -> there is a running instance
            // notify the running instance to be on Top
            sendToServer();
        } catch (Exception ex) {
            Platform.runLater(() -> {
                showAlreadyRunningError();
            });
        }
    }

    private void listenToInstance(Socket serverClient) {
        try {
            String clientMessage;
            Scanner scan = new Scanner(serverClient.getInputStream());
            while (scan.hasNext()) {
                clientMessage = scan.next();
                System.out.println("Other Instance: " + clientMessage);
                Platform.runLater(() -> {
                    openCurrentStage();
                });
            }
        } catch (IOException ex) {
            Logger.writeLog("Error in " + getClass().getName() + ".listenToInstance() => listen to other Instances => " + ex);
        }
    }

    private void sendToServer() {
        try {
            final Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
            System.out.println("Connection Success ...");
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                String clientMessage = "1";
                pw.println(clientMessage);
                System.exit(0);
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + "sendToServer() => sending to server => " + e);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Connecting to server : " + e);
            Platform.runLater(() -> {
                showAlreadyRunningError();
            });
        }
    }

    private void showAlreadyRunningError() {
        // catch anything unexpected !
        System.out.println("Program already running, exiting");
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.setHeaderText("Program already running, exiting");
        warning.showAndWait();
        System.exit(0);
    }

    private void openCurrentStage() {
        if (currentStage == null) {
            System.out.println("null");
            System.exit(0);
        } else if (currentStage.isIconified()) {
            System.out.println("isIconified");
            currentStage.setIconified(false);
        } else {
            System.out.println("setAlwaysOnTop");
            currentStage.setAlwaysOnTop(true);
            currentStage.setAlwaysOnTop(false);
        }
    }
}
