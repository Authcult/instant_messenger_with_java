package MessageType;

import java.io.Serializable;

public class Login implements Serializable {
    public Login(){
        this.username="";
        this.password="";
    }
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }

    private String username;
    private String password;
    @Override
    public String toString() {
        return "Login{" +
                "username='" + username  +
                ", password='" + password  +
                '}';
    }
}
