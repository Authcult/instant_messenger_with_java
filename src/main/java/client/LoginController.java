package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import MessageType.*;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

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

//    private BufferedReader in;
//    private PrintWriter out;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @FXML
    public void initialize()  {
        try {
            socket = new Socket("localhost", 5000);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("服务器未上线");
            alert.setHeaderText(null);
            alert.setContentText("服务器暂未上线请联系管理员!");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
        signInButton.setOnAction(event -> {
            try {
                handleSignIn();
            } catch (IOException | ClassNotFoundException e) {
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

    private void handleSignIn() throws IOException, ClassNotFoundException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        oos = new ObjectOutputStream(socket.getOutputStream());
        Login login = new Login(username, password);
        oos.writeObject(login);
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());
        Object serverMessage;
        while ((serverMessage = ois.readObject())!=null) {
            if (serverMessage.getClass()== CommonMessage.class&&(!((CommonMessage) serverMessage).getMessage().equals("false"))){
                try {

                    // Load the main chat interface
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                    Stage stage = (Stage) signInButton.getScene().getWindow();
                    // 初始化MessageModel
                    MessageModel messageModel = new MessageModel();

                    Scene scene = new Scene(fxmlLoader.load(), 900, 630);
                    stage.setScene(scene);
                    MainpageController mainpageController = fxmlLoader.getController();

                    // 将MessageModel传递给MainpageController
                    mainpageController.setMessageModel(messageModel);
                    // 创建web实例并启动连接
                    System.out.println(socket);
                    mainpageController.setSocket(this.socket);
                    Profile user = new Profile(username);
                    mainpageController.loadProfile(user);
                    ClientThread webClient = new ClientThread(messageModel, socket);
                    new Thread(webClient::connectToServer).start();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }else{
                usernameField.clear();
                passwordField.clear();
                break;
            }
        }
    }
}
