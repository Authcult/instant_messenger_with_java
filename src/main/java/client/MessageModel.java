package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MessageModel {
    private final StringProperty message = new SimpleStringProperty();
    private String name = null;


    public StringProperty messageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }
    public String getName() {
        return name;
    }

    public void change(String message) {
        this.name = "服务器";
        this.message.set(message);

    }
    public void change(String message,String name) {
        this.name = name;
        this.message.set(message);
    }
}
