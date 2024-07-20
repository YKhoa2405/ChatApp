package com.example.chatapp.util;


import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

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

    // lấy tất cả bạn bè của user đang đăng nhập
    public static  CollectionReference allFriendUserCollection(String userId){
        return FirebaseFirestore.getInstance().collection("users").document(userId).collection("friends");
    }
    //    Lấy chi tiết user
    public static CollectionReference getUserToFriend(){
        return currentUserDetail().collection("friends");
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

//    lấy tất cả danh sách phòng nhắn tin của user
    public static CollectionReference allChatRoomCollection(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }

//    Lấy ra thông tin của user bản thân đang nhắn tin
    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserUid())){
            return  allUserCollection().document(userIds.get(1));
        } else{
            return  allUserCollection().document(userIds.get(0));
        }
    }

    public static String timestampToStringFormat(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

//    Tải hình ảnh lên Storage
    public static StorageReference getStorageReferenceForImage(String chatRoomId) {
    // Tạo một tham chiếu đến vị trí lưu trữ trong Firebase Storage
        return FirebaseStorage.getInstance().getReference("chat_images")
            .child(chatRoomId)
            .child(System.currentTimeMillis() + ".jpg");
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static Task<Void> UpdateStatusUser(String userId, String status){
        return FirebaseFirestore.getInstance().collection("users").document(userId).update("status",status);
    }

    public static Task<Void> UpdateSeenByMessage(String chatRoomId,String userId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId).collection("chats").document(userId).update("seenBy",true);
    }

}
