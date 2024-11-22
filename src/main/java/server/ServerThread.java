package server;


import client.MessageType;
import MessageType.*;
import javafx.scene.control.*;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread{
    private TextArea messageArea;
    private ServerSocket serverSocket;
    private Socket socket;
//    private BufferedReader in;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ListView<String> contactListView;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javawork";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";


    public void sendMessage(String message) throws IOException {
        SendServerMessage sendServerMessage = new SendServerMessage(message);
        oos.writeObject(sendServerMessage);
        oos.flush();
    }

    public ServerThread(Socket socket, TextArea messageArea, ListView<String> contactListView) throws IOException {
        this.socket = socket;
        this.messageArea = messageArea;
        this.contactListView = contactListView;
        oos = new ObjectOutputStream(socket.getOutputStream());
    }
    public void run() {
        try {
            messageArea.appendText("客户端已连接\n");

            // 读取客户端消息
            Object clientMessage;

            while (true) {
                ois = new ObjectInputStream(socket.getInputStream());
                clientMessage = ois.readObject();
                Object finalClientMessage = clientMessage;
                javafx.application.Platform.runLater(() ->
                        messageArea.appendText("客户端: " + finalClientMessage.toString() + "\n")
                );
                if (clientMessage == null) {
                        break;
                }

                if (finalClientMessage instanceof Login) {
                    checkAccess(((Login) finalClientMessage).getUsername(),((Login) finalClientMessage).getPassword());
                }
                if (finalClientMessage instanceof CreateUser) {
                    addUserToDatabase(((CreateUser) finalClientMessage).getUsername(),((CreateUser) finalClientMessage).getPassword());
                }
                if (finalClientMessage instanceof AddFriend) {
                    addFriendToDatabase(((AddFriend) finalClientMessage).getUsername(),((AddFriend) finalClientMessage).getFriendName());
                }
                if (finalClientMessage instanceof GetFriendList) {
                    List<String> list=getFriendIdsByUserId(getUserId(((GetFriendList) finalClientMessage).getUsername()));
                    FriendList friendList=new FriendList(list);
                    oos.writeObject(friendList);
                    oos.flush();
                }
                if (finalClientMessage instanceof Logout) {

                }
            }

            // 关闭连接
            socket.close();
            serverSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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

    private void checkAccess(String username, String password) throws SQLException, IOException {
        // 检查用户名和密码是否正确
        boolean isValidUser = validateUser(username, password);
        if (isValidUser) {
            messageArea.appendText("用户"+username+"已上线\n");
            CommonMessage loginCheck = new CommonMessage(username);
            oos.writeObject(loginCheck);
            oos.flush();
        } else {
            oos.writeObject(new CommonMessage("false"));
            oos.flush();
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

            String query = "SELECT username FROM users";

            List<String> usernames = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    usernames.add(username);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 在JavaFX Application Thread中更新UI
            Platform.runLater(() -> {
                contactListView.getItems().clear();
                contactListView.getItems().addAll(usernames);
                messageArea.appendText("用户列表加载成功\n");
            });
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
