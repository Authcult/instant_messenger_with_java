package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    public void initialize() {
        // Sample contacts for the list view
        contactListView.getItems().addAll("Contact 1", "Contact 2", "Contact 3");

        // Send button action
        sendButton.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            Text messageText = new Text(message);
            messageText.setStyle("-fx-background-color: #E0FFE0; -fx-padding: 10; -fx-background-radius: 10;");
            chatBox.getChildren().add(messageText);
            messageInput.clear();
            chatScrollPane.setVvalue(1.0); // Scroll to the bottom
        }
    }
}
