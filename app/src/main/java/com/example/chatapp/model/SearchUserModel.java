package com.example.chatapp.model;

import com.google.firebase.Timestamp;

public class SearchUserModel {
    private String user_name;
    private String avatar;
    private String userId;
    private String email;
    private String status; // Thêm trường trạng thái
    private String bio;
    private String token;
    private String birthDay;

    public SearchUserModel(String user_name, String avatar, String email, String userId, String status,String bio,String birthDay) {
        this.user_name = user_name;
        this.avatar = avatar;
        this.email = email;
        this.userId = userId;
        this.status = status; // Khởi tạo trạng thái
        this.bio = bio;
        this.birthDay=birthDay;
    }

    public SearchUserModel() {
        // No-argument constructor required for Firestore
    }

    // Getter và Setter cho các trường

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
}
