package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MessageModel {
    private final StringProperty message = new SimpleStringProperty();
    private String name = null;
    private boolean isGroup = false;

    public StringProperty messageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }
    public String getName() {
        return name;
    }
    public boolean getIsGroup() {
            return isGroup;
    }
    public void change(String message,String name,boolean isGroup) {
        this.name = name;
        this.isGroup = isGroup;
        this.message.set(message);

    }
    public void change(String message,String name) {
        this.name = name;
        isGroup = false;
        this.message.set(message);

    }
}
