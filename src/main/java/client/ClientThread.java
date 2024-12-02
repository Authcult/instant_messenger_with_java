package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientThread extends Thread{

    private Socket socket;
    private Socket fileSocket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageModel messageModel; // 共享的 MessageModel 实例

    public ClientThread(MessageModel messageModel, Socket socket,Socket fileSocket) {
        this.messageModel = messageModel; // 保存MessageModel引用
        this.socket=socket;
        this.fileSocket=fileSocket;
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
                    messageModel.change(data);
                }
                if (finalServerMessage.startsWith(MessageType.SendMessage)) {
                    System.out.println(finalServerMessage);
                    String data=finalServerMessage.substring(MessageType.SendMessage.length()+1);
                    String[] dataArray=data.split(" ");
                    String friendname=dataArray[0];
                    String message=data.substring(friendname.length()+1);
                    System.out.println("friendname:"+friendname);
                    System.out.println(message);
                    // 将服务器消息写入 MessageModel
                    messageModel.change(message,friendname);

                }
                if(finalServerMessage.startsWith(MessageType.SendFile)){
                    String data=finalServerMessage.substring(MessageType.SendFile.length()+1);
                    String[] dataArray=data.split(" ");
                    String friendname=dataArray[0];
                    receiveFile(friendname);
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

    private void receiveFile(String friendname) throws IOException {
        String savePath = "D:/received_file/";

        try {
            System.out.println("======== 文件接收开始 ========");
            InputStream inputStream = fileSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();

            File file = new File(savePath + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[4096];
            int bytesRead;
            long remainingBytes = fileSize;
            while ((bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, remainingBytes))) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                remainingBytes -= bytesRead;
                if (remainingBytes == 0) {
                    break;
                }
            }


            System.out.println("文件接收完毕，保存路径：" + file.getAbsolutePath());
            messageModel.change("已收到文件"+fileName+" "+"保存路径：" + file.getAbsolutePath(),friendname);
        } catch (Exception e) {
            System.out.println("======== 文件接收过程中发生错误 ========");
            e.printStackTrace();
        } finally {
            System.out.println("文件传输线程结束");
        }
    }
    //关闭线程

}
