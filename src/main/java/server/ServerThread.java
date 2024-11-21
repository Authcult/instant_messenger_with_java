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
                if (finalClientMessage.startsWith(MessageType.AddFriend)) {
                    String data=finalClientMessage.substring(MessageType.AddFriend.length()+1);
                    String[] dataArray=data.split(" ");
                    String username = dataArray[0];
                    String friendUsername = dataArray[1];
                    addFriendToDatabase(username, friendUsername);
                }
                if (finalClientMessage.startsWith(MessageType.Logout)) {

                }
            }

            // 关闭连接
            socket.close();
            serverSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    //根据用户名在数据库中查询用户id
    private int getUserId(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    private void addFriendToDatabase(String username, String friendUsername) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO friendships (user_id, friend_id,status) VALUES (?, ?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, getUserId(username));
                preparedStatement.setInt(2, getUserId(friendUsername));
                preparedStatement.setString(3,"正常");
                preparedStatement.executeUpdate();
                System.out.println("添加好友成功: " +username + " 和 " + friendUsername);
            }
            String sql2 = "INSERT INTO friendships (user_id, friend_id,status) VALUES (?, ?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
                preparedStatement.setInt(1, getUserId(friendUsername));
                preparedStatement.setInt(2, getUserId(username));
                preparedStatement.setString(3,"正常");
                preparedStatement.executeUpdate();
                System.out.println("添加好友成功: " +friendUsername + " 和 " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("用户添加失败: " + e.getMessage());
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
            messageArea.appendText("用户"+username+"已上线\n");
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
