package com.example.chatapp.model;

import com.google.firebase.Timestamp;

public class FriendModel {
    private String userId;
    private Timestamp create_add_friend;


    private String status;

    public FriendModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getCreate_add_friend() {
        return create_add_friend;
    }

    public void setCreate_add_friend(Timestamp create_add_friend) {
        this.create_add_friend = create_add_friend;
    }

    public FriendModel(String userId, Timestamp create_add_friend, String status) {
        this.userId = userId;
        this.create_add_friend = create_add_friend;
        this.status  =status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }





}
