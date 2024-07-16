package com.example.chatapp.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private int messageMethod;

    public ChatMessageModel(){};


    public ChatMessageModel(String senderId, String message, Timestamp timestamp, int messageMethod) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.messageMethod=messageMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getMessageMethod() {
        return messageMethod;
    }

    public void setMessageMethod(int messageMethod) {
        this.messageMethod = messageMethod;
    }


}
