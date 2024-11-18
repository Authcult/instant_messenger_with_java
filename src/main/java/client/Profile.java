package client;

public class Profile {
    private String username;
    private String password;
    private String userID;
    Profile(String username){
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = String.valueOf(userID);
    }
}
