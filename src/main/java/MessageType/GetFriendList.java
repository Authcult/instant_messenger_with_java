package MessageType;

import java.io.Serializable;

public class GetFriendList implements Serializable {
    public String getUsername() {
        return username;
    }
    public GetFriendList(String username){
        this.username=username;
    }
    public String username;
    @Override
    public String toString() {
        return "GetFriendList{" +
                "username='" + username +
                '}';
    }
}
