package com.example.chatapp.model;

public class UserModel {
    private String name;
    private String avatar; // ID của hình ảnh avatar, nếu có
    private String status;

    public UserModel(String name, String avatar,String status) {
        this.name = name;
        this.avatar = avatar;
        this.status=status;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public String getAvatarResId() {
        return avatar;
    }

    public String getStatus() { return status;}
}
