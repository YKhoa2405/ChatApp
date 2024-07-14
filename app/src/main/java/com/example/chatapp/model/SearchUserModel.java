package com.example.chatapp.model;

public class SearchUserModel {
    private String user_name;
    private String avatar;
    private String userId;
    private String email;

    public SearchUserModel(String user_name, String avatar, String email, String userId) {
        this.user_name = user_name;
        this.avatar = avatar;
        this.email = email;
        this.userId = userId;
    }

    public SearchUserModel() {
        // No-argument constructor required for Firestore
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
