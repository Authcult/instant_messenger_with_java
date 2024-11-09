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

    public class MainpageController {

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

            // Set button action
            stateButton.setOnAction(event -> toggleState());
            listButton.setOnAction(event -> listContacts());
            deleteButton.setOnAction(event -> deleteContact());
            chatHisButton.setOnAction(event -> chatHistory());
        }

        private void updateCStateLabel() {
            cstateLabel.setText("已关闭");
        }


        private void toggleState() {
            // Toggle the label text directly
            if ("已关闭".equals(cstateLabel.getText())) {
                cstateLabel.setText("已开启");
            } else {
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
