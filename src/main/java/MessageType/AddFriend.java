package MessageType;

import java.io.Serializable;

public class AddFriend implements Serializable {
    public AddFriend(){
        this.username="";
        this.friendName="";
    }
    public AddFriend(String username, String friendName) {
        this.username = username;
        this.friendName = friendName;
    }
    public String getUsername()
    {
        return username;
    }
    public String getFriendName()
    {
        return friendName;
    }
    public String username;
    public String friendName;
    public String status;
    @Override
    public String toString()
    {
        return "AddFriend{" +
                "username='" + username  +
                ", friendName='" + friendName  +
                '}';
    }
}
