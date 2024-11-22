package MessageType;

import java.io.Serializable;
import java.util.List;

public class FriendList implements Serializable {
    public FriendList(List<String> friendList){
        this.friendList=friendList;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    private List<String> friendList;


}
