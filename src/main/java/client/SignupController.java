package client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class SignupController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private Button confirmbutton;
    @FXML
    private Button back;

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public void initialize() {
        confirmbutton.setOnAction(event -> {
            try {
                handleconfirm();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        back.setOnAction(event -> handleback());
    }
    public void setSocket(Socket socket){
        this.socket = socket;
    }
    private void handleback() {
        try {
            // Load the main chat interface
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(MessageType.Logout);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = (Stage) back.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 255,461);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleconfirm() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String password2 = passwordField2.getText();
        if (Objects.equals(password, password2)&& !Objects.equals(username, "")&& !Objects.equals(password, "")&& !Objects.equals(password2, "")) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(MessageType.CreateUser+" "+username+" "+password);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("账号注册成功");
            alert.setHeaderText(null);
            alert.setContentText("账号注册成功!");
            alert.showAndWait();
        }
    }
}



