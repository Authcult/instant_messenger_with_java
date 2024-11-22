package server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    private String username;

    private String password;




    @FXML
    public void initialize() {
        signInButton.setOnAction(event -> handleSignIn());
    }



    private void handleSignIn() {

        username = usernameField.getText();
        password = passwordField.getText();
        if (Objects.equals(username, "admin")&& Objects.equals(password, "123")) {
            try {

                // Load the main chat interface
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Stage stage = (Stage) signInButton.getScene().getWindow();

                Scene scene = new Scene(fxmlLoader.load(), 920, 600);
                stage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            usernameField.clear();
            passwordField.clear();
        }
    }
}
