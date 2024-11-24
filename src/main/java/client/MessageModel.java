package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MessageModel {
    private final StringProperty message = new SimpleStringProperty();

    public StringProperty messageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
