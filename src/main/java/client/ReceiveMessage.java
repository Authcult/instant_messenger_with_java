package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ReceiveMessage {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageModel messageModel; // 共享的 MessageModel 实例

    public ReceiveMessage(MessageModel messageModel,Socket socket) {
        this.messageModel = messageModel; // 保存MessageModel引用
        this.socket=socket;
    }

    public void connectToServer() {
        try {


            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 读取服务器消息
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {

                // 将服务器消息写入 MessageModel
                messageModel.setMessage(serverMessage);
            }

            // 关闭连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
