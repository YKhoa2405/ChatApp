package com.example.chatapp.model;

public class SearchUserModel {
    private String user_name;
    private String avatar;
    private String status;
    private String email;

    public SearchUserModel(String user_name, String avatar, String status) {
        this.user_name = user_name;
        this.avatar = avatar;
        this.status = status;
        this.email=email;
    }

    public SearchUserModel() {
        // No-argument constructor required for Firestore
    }

    public String getUser_Name() {
        return user_name;
    }

    public String getEmail(){
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }
}
