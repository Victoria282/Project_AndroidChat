package com.example.project_androidchat.Model;

import com.google.firebase.database.DataSnapshot;

public class Users {
    // id / имя / url пользователя
    private String userId;
    private String userName;
    private String imageUrl;

    public Users() {};

    public Users(String userId, String userName, String imageUrl) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUsername(String userName) {
        this.userName = userName;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getUsername() {
        return userName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
