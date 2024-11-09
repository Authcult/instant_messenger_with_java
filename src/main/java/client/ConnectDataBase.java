package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDataBase {
    public static void main(String[] args) throws Exception {
        //第二、三个参数是连接数据库的用户名和密码
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "120921");
//        4. 定义sql语句
        String sql="update account set balance =500 where id=1";
//        5. 获取执行sql语句的对象 statement
        Statement statement =connection.createStatement();
//        6. 执行sql，接收返回结果
        int count=statement.executeUpdate(sql);
//        7. 处理结果
        System.out.println(count);
//        8. 释放资源
        statement.close();
        connection.close();
    }

}
