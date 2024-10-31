package client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;


public class forgetController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField newpasswordField;
    @FXML
    private PasswordField confirmpasswordField;
    @FXML
    private Button confirmbutton;
    @FXML
    private Button back;
    public void initialize() {
        confirmbutton.setOnAction(event -> handleconfirm());
        back.setOnAction(event -> handleback());
    }

    private void handleback() {
        try {
            // Load the main chat interface
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) back.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleconfirm() {
    }
}


