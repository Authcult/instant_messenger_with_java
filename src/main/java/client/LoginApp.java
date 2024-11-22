//package client;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.TextArea;
//import javafx.stage.Stage;
//
//public class LoginApp extends Application {
//
//
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        primaryStage.setTitle("即时通讯 - 客户端");
//
//
//        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
//        Scene scene = new Scene(root,255,461);
//
//
//        primaryStage.setScene(scene);
//        primaryStage.setResizable(true);
//
//        primaryStage.show();
//        // 初始化MainpageController
//        MessageModel messageModel = new MessageModel();
//        MainpageController mainpageController = new MainpageController();
//
//        // 创建web实例并将MainpageController传递进去
//        web webClient = new web(messageModel, mainpageController);
//        new Thread(() -> webClient.connectToServer()).start();
//
//    }
//}

package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginApp extends Application {

    public static Socket socket;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
            primaryStage.setTitle("即时通讯 - 客户端");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 255, 461);
            primaryStage.setScene(scene);
            primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("客户端关闭");
    }
}

