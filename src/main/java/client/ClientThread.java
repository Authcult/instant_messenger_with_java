package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientThread extends Thread{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageModel messageModel; // 共享的 MessageModel 实例

    public ClientThread(MessageModel messageModel, Socket socket) {
        this.messageModel = messageModel; // 保存MessageModel引用
        this.socket=socket;
    }

    private ObservableList<String> friendList = FXCollections.observableArrayList();

    public ObservableList<String> getFriendList() {
        return friendList;
    }
    public void updateFriendList(List<String> newFriendList) {
        Platform.runLater(() -> {
            friendList.setAll(newFriendList);
        });
    }
    public void connectToServer() {
        try {


            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 读取服务器消息
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                String finalServerMessage = serverMessage;

                if (finalServerMessage.startsWith(MessageType.SendServerMessage)) {
                    String data=finalServerMessage.substring(MessageType.SendServerMessage.length()+1);
                    // 将服务器消息写入 MessageModel
                    messageModel.setMessage(data);
                }
                if (finalServerMessage.startsWith(MessageType.Friendlist)) {
                    String data=finalServerMessage.substring(MessageType.Friendlist.length()+1);
                    List<String> friendList =new ArrayList<>(Arrays.asList(data.split(",")));
                    friendList.add(0,"服务器");
                    updateFriendList(friendList);
                }

            }

            // 关闭连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
