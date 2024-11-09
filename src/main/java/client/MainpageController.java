//package client;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ListView;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class MainpageController {
//
//    @FXML
//    private ListView<String> contactListView;
//
//    @FXML
//    private ScrollPane chatScrollPane;
//
//    @FXML
//    public VBox chatBox;
//
//    @FXML
//    private TextArea messageInput;
//
//    @FXML
//    private Button sendButton;
//    @FXML
//    private Button backButton;
//    @FXML
//    private Button clearButton;
//
//    private String currentUser = "Me";
//
//    private String selectedContact = ""; // 当前选中的联系人// 当前用户名称
//
//    @FXML
//    public void initialize() {
//        // Sample contacts for the list view
//        contactListView.getItems().addAll("Contact 1", "Contact 2", "Contact 3");
////        messageInput.textProperty().bind(messageModel.messageProperty());
//        // 设置点击联系人列表的事件
//        contactListView.setOnMouseClicked(event -> switchChat());
//
//        // Send button action
//        sendButton.setOnAction(event -> sendMessage());
//        backButton.setOnAction(actionEvent -> back());
//        clearButton.setOnAction(actionEvent -> clearChat());
//    }
//
//    private void back() {
//        try {
//            // Load the main chat interface
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
//            Stage stage = (Stage) backButton.getScene().getWindow();
//            Scene scene = new Scene(fxmlLoader.load(), 255,461);
//            stage.setScene(scene);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void switchChat() {
//        String newContact = contactListView.getSelectionModel().getSelectedItem();
//        if (newContact != null && !newContact.equals(selectedContact)) {
//            selectedContact = newContact;  // 更新当前选中的联系人
//            clearChat(); // 清空聊天界面
//
//        }
//    }
//
//    private void sendMessage() {
//        String message = messageInput.getText();
//        if (!message.isEmpty()) {
//            displayMessage(currentUser, message, Pos.CENTER_RIGHT, "#E0FFE0"); // 显示当前用户的消息
//            messageInput.clear();
//            chatScrollPane.setVvalue(1.0); // Scroll to the bottom
//        }
//    }
//
//    public void displayMessage(String sender, String message, Pos alignment, String bgColor) {
//        // 创建一个 VBox 包含名称和消息
//        VBox messageContainer = new VBox(2);
//        messageContainer.setAlignment(alignment);
//
//        // 创建名称文本
//        Text senderText = new Text(sender);
//        senderText.setStyle("-fx-font-weight: bold; -fx-padding: 2;");
//
//        // 创建消息文本
//        Text messageText = new Text(message);
//        messageText.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10; -fx-background-radius: 10;");
//
//        // 将名称和消息添加到 VBox
//        messageContainer.getChildren().addAll(senderText,messageText);
//
//        // 使用 HBox 包含消息容器并根据对齐方式调整位置
//        HBox alignmentBox = new HBox(messageContainer);
//        alignmentBox.setAlignment(alignment);
//
//        chatBox.getChildren().add(alignmentBox);
//    }
//
//    private void clearChat() {
//        chatBox.getChildren().clear(); // 清除chatBox中的所有消息
//    }
//}
package client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


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

    private String currentUser = "Me";

    private String selectedContact = "";

    // 引入MessageModel
    private MessageModel messageModel;

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
    public void initialize() {
        contactListView.getItems().addAll("Contact 1", "Contact 2", "Contact 3");
        contactListView.setOnMouseClicked(event -> switchChat());
        sendButton.setOnAction(event -> sendMessage());
        backButton.setOnAction(actionEvent -> back());
        clearButton.setOnAction(actionEvent -> clearChat());
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
            // Load the main chat interface
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

            Text senderText = new Text(sender);
            senderText.setStyle("-fx-font-weight: bold; -fx-padding: 2;");

            Text messageText = new Text(message);
            messageText.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10; -fx-background-radius: 10;");

            messageContainer.getChildren().addAll(senderText, messageText);

            HBox alignmentBox = new HBox(messageContainer);
            alignmentBox.setAlignment(alignment);

            chatBox.getChildren().add(alignmentBox);
        });
    }

    private void clearChat() {
        chatBox.getChildren().clear();
    }
}
