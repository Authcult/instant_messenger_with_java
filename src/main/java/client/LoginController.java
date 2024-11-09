package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Button forgetButton;



    @FXML
    public void initialize() {
        signInButton.setOnAction(event -> handleSignIn());
        signUpButton.setOnAction(event -> handleSignup());
        forgetButton.setOnAction(event -> handleforget());
    }

    private void handleforget() {
        try {
            // Load the main chat interface
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("forget.fxml"));
            Stage stage = (Stage) forgetButton.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleSignup() {


        try {
            // Load the main chat interface
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Signup.fxml"));
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleSignIn() {


            try {

                // Load the main chat interface
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Stage stage = (Stage) signInButton.getScene().getWindow();
                // 初始化MessageModel
                MessageModel messageModel = new MessageModel();
                Scene scene = new Scene(fxmlLoader.load(), 900, 600);
                stage.setScene(scene);
                MainpageController mainpageController = fxmlLoader.getController();

                // 将MessageModel传递给MainpageController
                mainpageController.setMessageModel(messageModel);
                // 创建web实例并启动连接

                ReceiveMessage webClient = new ReceiveMessage(messageModel);
                new Thread(webClient::connectToServer).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
