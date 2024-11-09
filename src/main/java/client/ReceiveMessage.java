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

    public ReceiveMessage(MessageModel messageModel) {
        this.messageModel = messageModel; // 保存MessageModel引用
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 读取服务器消息
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.equalsIgnoreCase("exit")) {
                    break;
                }

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
