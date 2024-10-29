package com.example.demo;

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
    public void initialize() {
        signInButton.setOnAction(event -> handleSignIn());
    }

    private void handleSignIn() {


            try {
                // Load the main chat interface
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Stage stage = (Stage) signInButton.getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load(), 900, 600);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
