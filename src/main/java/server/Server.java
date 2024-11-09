package server;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Application {
    private TextArea messageArea;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static Set<ServerThread> ServerThreads = Collections.synchronizedSet(new HashSet<>());
    private TextField inputField;

    private Button sendButton;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("即时通讯 - 服务器");

        // 设置UI组件
        messageArea = new TextArea();
        messageArea.setEditable(false);
        inputField = new TextField();
        inputField.setPromptText("输入消息...");
        sendButton = new Button("发送");

        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                broadcastMessage(message, null);
                messageArea.appendText("服务器: " + message + "\n");
                inputField.clear();
            }
        });

        VBox vbox = new VBox(10, messageArea, inputField, sendButton);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                startServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(5000);
        messageArea.appendText("服务器已启动，等待客户端连接...\n");
        while (true) {
            socket = serverSocket.accept();

            messageArea.appendText("客户端已连接，开始通信...\n");
            messageArea.appendText("客户端IP地址: " + socket.getRemoteSocketAddress() + "\n");

            // 创建线程处理客户端请求
            ServerThread serverThread = new ServerThread(socket,messageArea);
            serverThread.start();
            ServerThreads.add(serverThread);
        }
    }

    public static void broadcastMessage(String message, ServerThread excludeseverThread) {
        synchronized (ServerThreads) {
            for (ServerThread serverThread : ServerThreads) {
                if (serverThread!= excludeseverThread) {
                    serverThread.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // 关闭所有客户端连接
        synchronized (ServerThreads) {
            for (ServerThread serverThread : ServerThreads) {
                serverThread.closeConnection();
            }
        }

        // 关闭服务器套接字
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        messageArea.appendText("服务器已关闭。\n");
    }
}
