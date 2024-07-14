package com.example.chatapp.util;

import android.content.Intent;

import com.example.chatapp.model.SearchUserModel;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, SearchUserModel model){
        intent.putExtra("user_name",model.getUser_name());
        intent.putExtra("avatar",model.getAvatar());
        intent.putExtra("userId",model.getUserId());
    }

    public static SearchUserModel getUserModelAsIntent(Intent intent){
        SearchUserModel userModel = new SearchUserModel();
        userModel.setUser_name(intent.getStringExtra("user_name"));
        userModel.setAvatar(intent.getStringExtra("avatar"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }

}
