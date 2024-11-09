package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    private BufferedReader in;
    private PrintWriter out;

    private Socket socket;

    @FXML
    public void initialize() throws IOException {
        socket = new Socket("localhost", 5000);
        signInButton.setOnAction(event -> {
            try {
                handleSignIn();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
            MessageModel messageModel = new MessageModel();
            MainpageController mainpageController = fxmlLoader.getController();

            // 将MessageModel传递给MainpageController
            mainpageController.setMessageModel(messageModel);
            // 创建web实例并启动连接

            ReceiveMessage webClient = new ReceiveMessage(messageModel,socket);
            new Thread(webClient::connectToServer).start();
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
            SignupController signupController = fxmlLoader.getController();
            signupController.setSocket(socket);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleSignIn() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(MessageType.Login+" "+username+" "+password);
        String serverMessage;
        while ((serverMessage = in.readLine()) != null) {
            if (!serverMessage.equals("false")){
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

                    ReceiveMessage webClient = new ReceiveMessage(messageModel, socket);
                    new Thread(webClient::connectToServer).start();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                usernameField.clear();
                passwordField.clear();
                break;
            }
        }
    }
}
