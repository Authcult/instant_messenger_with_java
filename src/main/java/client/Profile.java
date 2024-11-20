package client;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Profile {
    private String username;
    private String password;
    private String userID;
    private String avatarPath; // 用户头像路径

    private static final String CONFIG_FILE = "user_profile.properties"; // 配置文件路径

    public Profile() throws IOException, URISyntaxException {
        setAvatarPath(); // 初始化时加载用户数据
    }
    public Profile(String username) throws IOException, URISyntaxException {
        this.username = username;
        setAvatarPath(); // 初始化时加载用户数据
    }


    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath() throws IOException, URISyntaxException {
        // 读取JSON文件内容
        String jsonString = new String(Files.readAllBytes(Paths.get(getClass().getResource("profile.json").toURI())));
        HashMap map = new Gson().fromJson(jsonString, HashMap.class);
        this.avatarPath = (String) map.get("avatar_path");
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
