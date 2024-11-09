package server;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class ServerThread extends Thread{
    private TextArea messageArea;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter ot;


    public void sendMessage(String message) {
        ot.println(message);
    }

    public ServerThread(Socket socket, TextArea messageArea) {
        this.socket = socket;
        this.messageArea = messageArea;

    }
    public void run() {
        try {
            messageArea.appendText("客户端已连接\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ot = new PrintWriter(socket.getOutputStream(), true);


            // 读取客户端消息
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                String finalClientMessage = clientMessage;
                javafx.application.Platform.runLater(() ->
                        messageArea.appendText("客户端: " + finalClientMessage + "\n")
                );

                if (clientMessage.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            // 关闭连接
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
