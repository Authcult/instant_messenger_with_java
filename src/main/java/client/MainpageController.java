
package client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import MessageType.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class MainpageController {

    @FXML
    private ListView<String> contactListView;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private VBox chatBox;

    @FXML
    private TextArea messageInput;

    @FXML
    private Button sendButton;
    @FXML
    private Button backButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button addFriendButton;
    @FXML
    private Button setAvatarButton;

    private String currentUser = "Me";

    private String selectedContact = "";

//    private BufferedReader in;
//
//    private PrintWriter out;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;

    private Profile user= new Profile();



    private Image userAvatar; // 当前用户头像

    private final Image defaultAvatar = new Image(getClass().getResource(user.getAvatarPath()).toExternalForm());// 替换为默认头像路径

    public MainpageController() throws IOException, URISyntaxException {
    }


    public void getfriendlist() throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(new GetFriendList(user.getUsername()));
        oos.flush();
    }
    public void loadProfile(Profile user) {
        this.user = user;
    }

    private MessageModel messageModel;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
        // 绑定 messageProperty，当消息有变化时更新聊天框
        messageModel.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            if (newMessage != null && !newMessage.isEmpty()) {
                displayMessage("服务器", newMessage, Pos.CENTER_LEFT, "#FFE0E0");
            }
        });
    }

    @FXML
    public void initialize() throws IOException {
// 检查 user 是否为 null
        if (user != null) {
            // 加载用户头像
            String avatarPath = user.getAvatarPath();
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                userAvatar = new Image(avatarFile.toURI().toString());
            } else {
                userAvatar = defaultAvatar; // 如果路径无效则使用默认头像
            }
        } else {
            // 如果 user 为 null，则使用默认头像
            userAvatar = defaultAvatar;
        }

        setAvatarButton.setOnAction(event -> setAvatar());
        contactListView.getItems().addAll();
        contactListView.setOnMouseClicked(event -> switchChat());
        sendButton.setOnAction(event -> sendMessage());
        backButton.setOnAction(actionEvent -> back());
        clearButton.setOnAction(actionEvent -> clearChat());
        addFriendButton.setOnAction(actionEvent -> {
            try {
                addFriend();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        refreshFriendList();
    }

    private void addFriend() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加好友");
        dialog.setHeaderText("请输入好友的用户名以添加好友");
        dialog.setContentText("请输入您好友的用户名:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            oos = new ObjectOutputStream(socket.getOutputStream());
            AddFriend addFriend = new AddFriend(user.getUsername(),result.get());
            oos.writeObject(addFriend);
            oos.flush();
        }
    }

    private void refreshFriendList() {
        Platform.runLater(() -> {
            try {
                //清除原有列表中的内容
                contactListView.getItems().clear();
                getfriendlist();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
    }
    private void switchChat() {
        String newContact = contactListView.getSelectionModel().getSelectedItem();
        if (newContact != null && !newContact.equals(selectedContact)) {
            selectedContact = newContact;
            clearChat();
        }
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            displayMessage(currentUser, message, Pos.CENTER_RIGHT, "#E0FFE0");
            messageInput.clear();
            chatScrollPane.setVvalue(1.0);
        }
    }

    private void back() {
        try {
            loginout();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void displayMessage(String sender, String message, Pos alignment, String bgColor) {
        Platform.runLater(() -> {
            VBox messageContainer = new VBox(2);
            messageContainer.setAlignment(alignment);

            ImageView avatarView = new ImageView(userAvatar != null ? userAvatar : defaultAvatar);
            avatarView.setFitWidth(40);
            avatarView.setFitHeight(40);

            Text senderText = new Text(sender);
            senderText.setStyle("-fx-font-weight: bold; -fx-padding: 2;");

            Text messageText = new Text(message);
            messageText.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10; -fx-background-radius: 10;");

            messageContainer.getChildren().addAll(senderText, messageText);

            HBox alignmentBox = new HBox();
            if (alignment == Pos.CENTER_LEFT) {
                alignmentBox.getChildren().addAll(avatarView, messageContainer);
            } else {
                alignmentBox.getChildren().addAll(messageContainer, avatarView);
            }
            alignmentBox.setAlignment(alignment);
            alignmentBox.setSpacing(10);

            chatBox.getChildren().add(alignmentBox);
        });
    }



    private void setAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(setAvatarButton.getScene().getWindow());
        if (selectedFile != null) {
            userAvatar = new Image(selectedFile.toURI().toString());

        }
    }


    private void clearChat() {
        chatBox.getChildren().clear();
    }


    private void loginout() throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        Logout logout = new Logout(user.getUsername());
        oos.writeObject(logout);
        oos.flush();
    }
}