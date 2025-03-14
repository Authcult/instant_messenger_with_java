//package server;
//
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import javafx.scene.shape.Line;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//
//public class MainpageController {
//
//    private ServerSocket serverSocket;
//    private Socket socket;
//
//    private static Set<ServerThread> ServerThreads = Collections.synchronizedSet(new HashSet<>());
//
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/javawork";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "123";
//
//    @FXML
//    private TextField inputField;
//    @FXML
//    private Button sendButton;
//
//    @FXML
//    public ListView<String> contactListView;
//
//    @FXML
//    private Label stateLabel;
//
//    @FXML
//    private Label cstateLabel;
//
//    @FXML
//    private Button stateButton;
//
//    @FXML
//    private Label numLabel;
//
//    @FXML
//    private Label numStateLabel;
//
//    @FXML
//    private Button listButton;
//
//    @FXML
//    private Button deleteButton;
//
//    @FXML
//    private Button chatHisButton;
//
//    @FXML
//    private TextArea log;
//
//    @FXML
//    private void initialize() {
//        // Set initial label text
//        updateCStateLabel();
//        updateNumStateLabel();
//        inputField.setPromptText("输入消息...");
//
//        // Set button action
//        sendButton.setOnAction(event -> Send());
//        stateButton.setOnAction(event -> {
//            try {
//                toggleState();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//        listButton.setOnAction(event -> listContacts());
//        deleteButton.setOnAction(event -> deleteContact());
//        chatHisButton.setOnAction(event -> chatHistory());
//
//        loadUserList();
//    }
//
//    private void startServer() throws IOException {
//        serverSocket = new ServerSocket(5000);
//        log.appendText("服务器已启动，等待客户端连接...\n");
//        while (true) {
//            socket = serverSocket.accept();
//
//            log.appendText("客户端已连接，开始通信...\n");
//            log.appendText("客户端IP地址: " + socket.getRemoteSocketAddress() + "\n");
//
//            // 创建线程处理客户端请求
//            ServerThread serverThread = new ServerThread(socket,log,contactListView);
//            serverThread.setCallback(this::onThreadClosed);
//            serverThread.start();
//            ServerThreads.add(serverThread);
//            updateNumStateLabel();
//        }
//    }
//    private void updateNumStateLabel() {
//        int threadCount = ServerThreads.size();
//        Platform.runLater(() -> numStateLabel.setText(String.valueOf(threadCount)));
//
//    }
//    public void onThreadClosed(ServerThread thread) {
//        synchronized (ServerThreads) {
//            ServerThreads.remove(thread);
//            updateNumStateLabel();
//        }
//    }
//    private void Send() {
//        String message = inputField.getText();
//        if (!message.isEmpty()) {
//            broadcastMessage(message, null);
//            log.appendText("服务器: " + message + "\n");
//            inputField.clear();
//        }
//    }
//
//    public static void broadcastMessage(String message, ServerThread excludeseverThread) {
//        synchronized (ServerThreads) {
//            for (ServerThread serverThread : ServerThreads) {
//                if (serverThread!= excludeseverThread) {
//                    serverThread.sendMessage(message);
//                }
//            }
//        }
//    }
//
//
//    public void stop() throws Exception {
//
//
//        // 关闭所有客户端连接
//        synchronized (ServerThreads) {
//            for (ServerThread serverThread : ServerThreads) {
//                serverThread.closeConnection();
//            }
//        }
//
//        // 关闭服务器套接字
//        if (serverSocket != null && !serverSocket.isClosed()) {
//            serverSocket.close();
//        }
//
//        log.appendText("服务器已关闭。\n");
//    }
//
//    private void updateCStateLabel() {
//        cstateLabel.setText("已关闭");
//    }
//
//
//    private void toggleState() throws Exception {
//        // Toggle the label text directly
//
//        if ("已关闭".equals(cstateLabel.getText())) {
//            new Thread(() -> {
//                try {
//                    startServer();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }).start();
//            cstateLabel.setText("已开启");
//        } else {
//            log.appendText("服务器已关闭。\n");
//            broadcastMessage("服务器已关闭。", null);
//            stop();
//            cstateLabel.setText("已关闭");
//        }
//    }
//
//    private void loadUserList() {
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             Statement statement = connection.createStatement()) {
//
//            String query = "SELECT username FROM users"; // 假设 user 表中有 username 列
//            ResultSet resultSet = statement.executeQuery(query);
//
//            contactListView.getItems().clear(); // 清空现有列表
//
//            while (resultSet.next()) {
//                String username = resultSet.getString("username");
//                contactListView.getItems().add(username); // 添加到列表视图
//            }
//
//            log.appendText("用户列表已加载。\n");
//        } catch (Exception e) {
//            log.appendText("加载用户列表时出错: " + e.getMessage() + "\n");
//            e.printStackTrace();
//        }
//    }
//
//
//    private void listContacts() {
//            loadUserList();
//            log.appendText("用户列表已刷新。\n");
//    }
//
//    private void deleteContact() {
//    }
//
//    private void chatHistory() {
//
//    }
//
//}
package server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainpageController {

    private ServerSocket serverSocket;
    private ServerSocket fileServerSocket;
    private Socket socket;
    private Socket fileSocket;

    // 使用 Map 存储客户端的标识符和对应的 ServerThread
    private static Map<Integer, ServerThread> ServerThreads = Collections.synchronizedMap(new HashMap<>());

    private static final String DB_URL = "jdbc:mysql://localhost:3306/javawork";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";

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
        updateNumStateLabel();
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

        loadUserList();
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(5000);
        fileServerSocket = new ServerSocket(8000);
        Platform.runLater(() -> {
            log.appendText("服务器已启动，等待客户端连接...\n");
        });
        while (true) {
            socket = serverSocket.accept();
            fileSocket = fileServerSocket.accept();
            Platform.runLater(() -> {
                log.appendText("客户端已连接，开始通信...\n");
                log.appendText("客户端IP地址: " + socket.getRemoteSocketAddress() + "\n");
            });
            // 创建线程处理客户端请求
            ServerThread serverThread = new ServerThread(socket,fileSocket, log, contactListView);
            serverThread.setCallbacks(this::onThreadClosed,this::onThreadOpened,this::onThreadSend);
            serverThread.start();


            updateNumStateLabel();
        }
    }

    private void onThreadSend(ServerThread thread){
        synchronized (ServerThreads) {
            int clientUserid = thread.getUserid(); // 获取客户端的用户名
            int friendid = thread.getCurfriendid();
            System.out.println("客户端id " + clientUserid);
            boolean isFile=thread.getIsFile();
            boolean isGroup=thread.getIsGroup();
            if (isGroup){
                String groupName=thread.getGroupName();
                SendGroupMessage(thread.getCurmessage(),groupName,clientUserid);
            }
            if (isFile){
                String filePath=thread.getFilePath();
                try {
                    sendFile(clientUserid,friendid,filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                SendMessage(thread.getCurmessage(), friendid, clientUserid);
            }
        }
    }

    private void SendGroupMessage(String message, String groupName, int userid) {
        synchronized (ServerThreads) {
            for(int i=1;i<=3;i++) {
                ServerThread serverThread = ServerThreads.get(i);
                if (serverThread != null) {
                    serverThread.sendGroupMessage(userid, message);
                }
            }
        }
    }

    private void sendFile(int userid, int friendid, String filePath) throws IOException {
        synchronized (ServerThreads) {
            ServerThread serverThread = ServerThreads.get(friendid);
            if (serverThread != null) {
                serverThread.sendFile(userid,filePath);
            }
        }
    }

    public void onThreadOpened(ServerThread thread){
        synchronized (ServerThreads) {
            int clientUserid = thread.getUserid(); // 获取客户端的用户名
            System.out.println("客户端id " + clientUserid);
            ServerThreads.put(clientUserid, thread);
            updateNumStateLabel();
        }
    }

    private void updateNumStateLabel() {
        int threadCount = ServerThreads.size();
        //输出ServerThreads
        System.out.println("ServerThreads: " + ServerThreads);
        Platform.runLater(() -> numStateLabel.setText(String.valueOf(threadCount)));
    }

    public void onThreadClosed(ServerThread thread) {
        synchronized (ServerThreads) {
            int clientUserid = thread.getUserid();
            ServerThreads.remove(clientUserid);
            while (ServerThreads.containsKey(clientUserid)) {
                ServerThreads.remove(clientUserid);
            }
            updateNumStateLabel();
        }
    }

    private void Send() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            broadcastMessage(message, null);
            Platform.runLater(() -> {
                log.appendText("服务器: " + message + "\n");
                inputField.clear();
            });
        }
    }

    public static void broadcastMessage(String message, ServerThread excludeServerThread) {
        synchronized (ServerThreads) {
            for (Map.Entry<Integer, ServerThread> entry : ServerThreads.entrySet()) {
                ServerThread serverThread = entry.getValue();
                if (serverThread != excludeServerThread) {
                    serverThread.sendServerMessage(message);
                }
            }
        }
    }

    public static void SendMessage(String message, int friendid,int userid) {
        synchronized (ServerThreads) {
            ServerThread serverThread = ServerThreads.get(friendid);
            if (serverThread != null) {
                serverThread.sendMessage(userid, message);
            }
        }
    }

    public void stop() throws Exception {
        // 关闭所有客户端连接
        synchronized (ServerThreads) {
            for (ServerThread serverThread : ServerThreads.values()) {
                serverThread.closeConnection();
            }
        }

        // 关闭服务器套接字
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        Platform.runLater(() -> {
            log.appendText("服务器已关闭。\n");
        });
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
            log.appendText("服务器已关闭。\n");
            broadcastMessage("服务器已关闭。", null);
            stop();
            cstateLabel.setText("已关闭");
        }
    }

    private void loadUserList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT username FROM users"; // 假设 user 表中有 username 列
            ResultSet resultSet = statement.executeQuery(query);

            contactListView.getItems().clear(); // 清空现有列表

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                contactListView.getItems().add(username); // 添加到列表视图
            }
            Platform.runLater(() -> {
                log.appendText("用户列表已加载。\n");
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                log.appendText("加载用户列表时出错: " + e.getMessage() + "\n");
            });
            e.printStackTrace();
        }
    }

    private void listContacts() {
        loadUserList();
        Platform.runLater(() -> {
            log.appendText("用户列表已刷新。\n");
        });
    }

    private void deleteContact() {
        // 删除联系人功能
    }

    private void chatHistory() {
        // 聊天记录功能
    }
}
