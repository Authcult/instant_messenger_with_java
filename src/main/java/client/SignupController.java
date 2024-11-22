package client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import MessageType.*;
import java.io.*;
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

//    private BufferedReader in;
//    private PrintWriter out;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
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
            oos = new ObjectOutputStream(socket.getOutputStream());
            CreateUser createUser = new CreateUser(username, password);
            oos.writeObject(createUser);
            oos.flush();
        }
    }
}



