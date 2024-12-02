package server;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;

public class FileThread {
    private Socket socket;
    private String savePath = "D:/temp_file/";


    public FileThread(Socket socket) {
        this.socket = socket;
    }


    public void work() {
        try {
            File directory = new File(savePath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("目录已创建: " + savePath);
                } else {
                    System.out.println("无法创建目录: " + savePath);
                }
            }

            System.out.println("======== 文件接收开始 ========");
            System.out.println("文件传输端口号：" + socket.getPort());
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String fileName = dataInputStream.readUTF();
            System.out.println("文件名：" + fileName);
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

//            bufferedOutputStream.close();
//            dataInputStream.close();
            System.out.println("文件接收完毕，保存路径：" + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("======== 文件接收过程中发生错误 ========");
            e.printStackTrace();
        } finally {
            try {
                System.out.println("文件传输线程结束");
            } catch (Exception e) {}
        }
    }


    /**
     * 格式化文件大小
     * @param length
     * @return
     */

    private static DecimalFormat df = null;
    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }

}
