package MessageType;

import java.io.Serializable;

public class CreateUser implements Serializable {
    public CreateUser(){
        this.username="";
        this.password="";
    }
    public CreateUser(String username, String password) {
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
    public String username;
    public String password;
    @Override
    public String toString()
    {
        return "CreateUser{" +
                "username='" + username  +
                ", password='" + password  +
                '}';
    }
}


