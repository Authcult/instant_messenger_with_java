module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // 允许JavaFX模块访问 server 和 client 包
    opens server to javafx.graphics, javafx.fxml;
    opens client to javafx.graphics, javafx.fxml;
    // 导出server包，使其可以被JavaFX模块访问


    exports server;
}
