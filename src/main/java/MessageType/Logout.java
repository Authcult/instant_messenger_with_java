package MessageType;

import java.io.Serializable;

public class Logout implements Serializable {
    public String username;
    public Logout(String username)
    {
        this.username = username;
    }
    @Override
    public String toString()
    {
        return "Logout"+username;
    }
}
