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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class MainpageController {

    @FXML
    public Label contactNameLabel;

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
    @FXML
    private Button sendFileButton;
    private String currentUser = "Me";

    private String selectedContact = "";

    private BufferedReader in;

    private PrintWriter out;

    private FileInputStream fis;

    private DataOutputStream dos;

    private boolean isFile = false;

    private Socket socket;

    private Socket fileSocket;
    private String fileName;
    private Profile user= new Profile();



    private Image userAvatar; // 当前用户头像

    private final Image defaultAvatar = new Image(getClass().getResource(user.getAvatarPath()).toExternalForm());// 替换为默认头像路径

    public MainpageController() throws IOException, URISyntaxException {
    }


    public void getfriendlist() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(MessageType.GetFriendList+" "+user.getUsername());
    }
    public void loadProfile(Profile user) {
        this.user = user;
    }

    private MessageModel messageModel;

    public void setSocket(Socket socket,Socket fileSocket) {
        this.socket = socket;
        this.fileSocket = fileSocket;
    }

    private ClientThread clientThread;

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
        contactListView.setItems(clientThread.getFriendList());
    }


    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
        // 绑定 messageProperty，当消息有变化时更新聊天框
        messageModel.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            if (newMessage != null && !newMessage.isEmpty()) {
                String sender = messageModel.getName();
                displayMessage(sender, newMessage, Pos.CENTER_LEFT, "#FFE0E0");
                System.out.println(sender);
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
        sendFileButton.setOnAction(event -> {
            try {
                sendFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        setAvatarButton.setOnAction(event -> setAvatar());
        contactListView.getItems().addAll();
        contactListView.setOnMouseClicked(event -> switchChat());
        sendButton.setOnAction(event -> {
            try {
                sendMessage();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        backButton.setOnAction(actionEvent -> back());
        clearButton.setOnAction(actionEvent -> clearChat());
        addFriendButton.setOnAction(actionEvent -> {
            try {
                addFriend();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        selectedContact="服务器";
        refreshFriendList();
    }

    public void sendFile() throws IOException {
        browseFile();
        isFile = true;
    }


    private void browseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null); // 打开文件选择框

        if (selectedFile != null) {
            messageInput.setText(selectedFile.getAbsolutePath()); // 显示选择的文件路径
            fileName = selectedFile.getName();
        }
    }

    private void addFriend() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加好友");
        dialog.setHeaderText("请输入好友的用户名以添加好友");
        dialog.setContentText("请输入您好友的用户名:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(MessageType.AddFriend+" "+user.getUsername()+" "+result.get());;
        }
        refreshFriendList();
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
            System.out.println("切换到联系人：" + selectedContact);
            clearChat();
            if (selectedContact.equals("服务器")){
                contactNameLabel.setText("正在与["+selectedContact+"]通信");
            }else {
                contactNameLabel.setText("正在与[" + selectedContact + "]聊天");
            }
        }
    }

    private void sendMessage() throws IOException {
        String message = messageInput.getText();
        if(!isFile) {
            if (!message.isEmpty()) {
                displayMessage(currentUser, message, Pos.CENTER_RIGHT, "#E0FFE0");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(MessageType.SendMessage + " " + user.getUsername() + " " + selectedContact + " " + message);
                messageInput.clear();
                chatScrollPane.setVvalue(1.0);
            }
        }else{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(MessageType.SendFile+" "+user.getUsername()+" "+selectedContact+" "+fileName);
            isFile = false;
            File file = new File(message);
            messageInput.clear();
            if (file.exists()) {
                System.out.println("连接到服务器...");
                String fileName = file.getName();
                long fileSize = file.length();

                OutputStream outputStream = fileSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF(fileName);
                dataOutputStream.writeLong(fileSize);

                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }


                System.out.println("文件发送完毕！");
                displayMessage(currentUser, "已发送文件"+fileName+" ", Pos.CENTER_RIGHT, "#E0FFE0");
            } else {
                System.out.println("文件不存在: " + file.getAbsolutePath());
            }
        }
    }

    private void back() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            loginout();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 255,461);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            stage.setScene(scene);
            e.printStackTrace();
        }
    }
    private void displayMessage(String sender, String message, Pos alignment, String bgColor) {
        if (sender.equals(selectedContact)||sender.equals(currentUser)) {
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


    private void loginout() {
        out.println(MessageType.Logout);
    }
}