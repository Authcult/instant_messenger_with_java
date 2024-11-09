package server;


import client.MessageType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ServerThread extends Thread{
    private TextArea messageArea;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter ot;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/javawork";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";


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

                if (finalClientMessage.startsWith(MessageType.Login)) {
                    String data=finalClientMessage.substring(MessageType.Login.length()+1);
                    String[] dataArray=data.split(" ");
                    String username = dataArray[0];
                    String password = dataArray[1];
                    checkAccess(username, password);
                }
                if (finalClientMessage.startsWith(MessageType.CreateUser)) {
                    String data=finalClientMessage.substring(MessageType.CreateUser.length()+1);
                    String[] dataArray=data.split(" ");
                    String username = dataArray[0];
                    String password = dataArray[1];
                    addUserToDatabase(username, password);
                }
            }

            // 关闭连接
            socket.close();
            serverSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUserToDatabase(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                System.out.println("用户添加成功: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("用户添加失败: " + e.getMessage());
        }
    }

    private void checkAccess(String username, String password) throws SQLException {
        // 检查用户名和密码是否正确
        boolean isValidUser = validateUser(username, password);
        if (isValidUser) {
            ot.println(username);
        } else {
            ot.println("false");
        }
    }

    private boolean validateUser(String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
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
