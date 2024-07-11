package com.example.chatapp.model;

import android.media.Image;

public class ChatData {
    private String name;
    private String lastChat;
    private String timeChat;
    private String avatar; // ID của hình ảnh avatar, nếu có

    public ChatData(String name, String lastChat, String avatarResId,String timeChat) {
        this.name = name;
        this.lastChat = lastChat;
        this.avatar = avatarResId;
        this.timeChat=timeChat;
    }

    public String getName() {
        return name;
    }

    public String getLastChat() {
        return lastChat;
    }

    public String getTimeChat() {
        return timeChat;
    }

    public String getAvatarResId() {
        return avatar;
    }
}
