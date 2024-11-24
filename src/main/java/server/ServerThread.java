package server;


import client.MessageType;
import javafx.scene.control.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread{
    private TextArea messageArea;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter ot;

    public ListView<String> contactListView;
    private ServerThreadCallback callback;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javawork";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";

    public interface ServerThreadCallback {
        void onThreadClosed(ServerThread thread);
    }

    public void setCallback(ServerThreadCallback callback) {
        this.callback = callback;
    }
    public void sendMessage(String message) {
        ot.println(MessageType.SendServerMessage+" "+message);
    }

    public ServerThread(Socket socket, TextArea messageArea, ListView<String> contactListView) throws IOException {
        this.socket = socket;
        this.messageArea = messageArea;
        this.contactListView = contactListView;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ot = new PrintWriter(socket.getOutputStream(), true);
    }
    public void run() {
        try {
            messageArea.appendText("客户端已连接\n");


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
                if (finalClientMessage.startsWith(MessageType.GetFriendList)) {
                    String data=finalClientMessage.substring(MessageType.GetFriendList.length()+1);
                    String[] dataArray=data.split(" ");
                    String username = dataArray[0];
                    List<String> friendlist=getFriendIdsByUserId(getUserId(username));
                    ot.println(MessageType.Friendlist+" "+String.join(",", friendlist));
                }
                if (finalClientMessage.startsWith(MessageType.Logout)) {
                    closeConnection();
                    break;
                }
            }
            if (socket != null && !socket.isClosed()) {
                // 关闭连接
                socket.close();
                serverSocket.close();
            }
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
    //根据用户名在数据库中查询用户id
    private String getUsernameById(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT username FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    //获取用户的好友列表
    private List<String> getFriendIdsByUserId(int userId) {
        List<String> friendIds = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int friendId = resultSet.getInt("friend_id");
                        friendIds.add(getUsernameById(friendId));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取好友ID列表时出错: " + e.getMessage());
        }
        return friendIds;
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
            listContacts();
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

            messageArea.appendText("用户列表已加载。\n");
        } catch (Exception e) {
            messageArea.appendText("加载用户列表时出错: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }


    private void listContacts() {
            loadUserList();
            messageArea.appendText("用户列表已刷新。\n");
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                messageArea.appendText("客户端已断开连接: " + socket.getRemoteSocketAddress() + "\n");
            }
        } catch (IOException e) {
            messageArea.appendText("关闭客户端连接时出错: " + e.getMessage() + "\n");
        } finally {
            if (callback != null) {
                callback.onThreadClosed(this);
            }
        }
    }
}
