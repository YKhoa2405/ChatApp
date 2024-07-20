package com.example.chatapp.model;

public class FriendModel {
    private String userId;
    private String status;

    public FriendModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public FriendModel(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }




}
