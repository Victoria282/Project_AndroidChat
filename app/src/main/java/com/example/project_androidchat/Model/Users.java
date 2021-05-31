package com.example.project_androidchat.Model;

public class Users {
    // id / имя / url пользователя
    private String userId;
    private String userName;
    private String imageUrl;
    private String status;

    public Users() {};

    public Users(String userId, String userName, String imageUrl, String status) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.status = status;
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

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
