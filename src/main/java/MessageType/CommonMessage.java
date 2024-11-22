package MessageType;

import java.io.Serializable;

public class CommonMessage implements Serializable {
    public CommonMessage(String message) {
        this.message = message;
    }
    public String message;
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return message;
    }
}
