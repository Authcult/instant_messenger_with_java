package MessageType;

import java.io.Serializable;

public class SendServerMessage implements Serializable {
    public SendServerMessage(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }
    public String message;
}
