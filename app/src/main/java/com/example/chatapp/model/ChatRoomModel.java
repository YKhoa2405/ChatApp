package com.example.chatapp.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    String chatRoomId;
    String lastMessageSenderId;
    Timestamp lassMessageTimestamp;
    List<String> userIds;
    String lassMessageText;
    String key;

    public ChatRoomModel(){}


    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lassMessageTimestamp, String lastMessageSenderId,String key) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lassMessageTimestamp = lassMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.key = key;
    }

    public String getChatRoomId() {return chatRoomId;}

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessage) {
        this.lastMessageSenderId = lastMessage;
    }

    public Timestamp getLassMessageTimestamp() {
        return lassMessageTimestamp;
    }

    public void setLassMessageTimestamp(Timestamp lassMessageTimestamp) {
        this.lassMessageTimestamp = lassMessageTimestamp;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getLassMessageText() {
        return lassMessageText;
    }

    public void setLassMessageText(String lassMessageText) {
        this.lassMessageText = lassMessageText;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



}
