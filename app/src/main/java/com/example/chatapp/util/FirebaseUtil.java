package com.example.chatapp.util;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserUid(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetail(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserUid());
    }

    public static CollectionReference allUserCollection(){
        return  FirebaseFirestore.getInstance().collection("users");
    }

//    Lấy phòng chat
    public static  DocumentReference getChatRooms(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId);
    }

//    Lấy chi tiết tin nhắn
    public static CollectionReference getChatRoomMessage(String chaRoomId){
        return getChatRooms(chaRoomId).collection("chats");
    }


    public static String getChatRoomId(String userId1,String userId2){
//        hashCode trả về mã số nguyên đại diện cho 1 userId
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;

        }
    }
}
