package server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainpageController {

    private ServerSocket serverSocket;
    private Socket socket;

    private static Set<ServerThread> ServerThreads = Collections.synchronizedSet(new HashSet<>());
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    @FXML
    public ListView<String> contactListView;

    @FXML
    private Label stateLabel;

    @FXML
    private Label cstateLabel;

    @FXML
    private Button stateButton;

    @FXML
    private Label numLabel;

    @FXML
    private Label numStateLabel;

    @FXML
    private Button listButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button chatHisButton;

    @FXML
    private TextArea log;

    @FXML
    private void initialize() {
        // Set initial label text
        updateCStateLabel();
        inputField.setPromptText("输入消息...");

        // Set button action
        sendButton.setOnAction(event -> Send());
        stateButton.setOnAction(event -> {
            try {
                toggleState();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        listButton.setOnAction(event -> listContacts());
        deleteButton.setOnAction(event -> deleteContact());
        chatHisButton.setOnAction(event -> chatHistory());

    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(5000);
        log.appendText("服务器已启动，等待客户端连接...\n");
        while (true) {
            socket = serverSocket.accept();

            log.appendText("客户端已连接，开始通信...\n");
            log.appendText("客户端IP地址: " + socket.getRemoteSocketAddress() + "\n");

            // 创建线程处理客户端请求
            ServerThread serverThread = new ServerThread(socket,log);
            serverThread.start();
            ServerThreads.add(serverThread);
        }
    }

    private void Send() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            broadcastMessage(message, null);
            log.appendText("服务器: " + message + "\n");
            inputField.clear();
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


    public void stop() throws Exception {


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

        log.appendText("服务器已关闭。\n");
    }

    private void updateCStateLabel() {
        cstateLabel.setText("已关闭");
    }


    private void toggleState() throws Exception {
        // Toggle the label text directly

        if ("已关闭".equals(cstateLabel.getText())) {
            new Thread(() -> {
                try {
                    startServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            cstateLabel.setText("已开启");
        } else {
            stop();
            cstateLabel.setText("已关闭");
        }
    }

    private void listContacts() {
    }

    private void deleteContact() {
    }

    private void chatHistory() {

    }
}
