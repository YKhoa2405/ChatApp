package com.example.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.FriendModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProfileUserActivity extends AppCompatActivity {
    TextView txtUserName, txtEmail,txtBio, txtTimeJoin,txtUserId;
    ImageView imgAvatar;
    ImageButton btnGoBack;
    CardView addFriend;
    SearchUserModel otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_user);

        addFriend = findViewById(R.id.addFriend);
        txtEmail =findViewById(R.id.txtEmail);
        txtUserName =findViewById(R.id.txtUserName);
        txtBio =findViewById(R.id.txtBio);
        txtUserId =findViewById(R.id.txtUserId);
        imgAvatar =findViewById(R.id.imgAvatar);
        btnGoBack =findViewById(R.id.btnGoBack);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());

        btnGoBack.setOnClickListener(t->{
            finish();
        });

        txtUserName.setText(otherUser.getUser_name());
        Glide.with(this).load(otherUser.getAvatar()).into(imgAvatar);
        txtEmail.setText(otherUser.getEmail());
        txtUserId.setText(otherUser.getUserId());
        if(otherUser.getBio().equals("null")){
            txtBio.setVisibility(View.GONE);

        }else{
            txtBio.setText(otherUser.getBio());
        }

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserUid = FirebaseUtil.currentUserUid();
                String receiverUserId = otherUser.getUserId();
                Timestamp createAddFriend = Timestamp.now();

                // Tạo yêu cầu kết bạn
                Map<String, Object> friendRequest = new HashMap<>();
                friendRequest.put("senderId", currentUserUid);
                friendRequest.put("createAddFriend", createAddFriend);

                // Tạo yêu cầu đã gửi
                Map<String, Object> sentFriendRequest = new HashMap<>();
                sentFriendRequest.put("receiverId", receiverUserId);
                sentFriendRequest.put("createAddFriend", createAddFriend);

                // Lấy instance của Firestore và batch
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                WriteBatch batch = db.batch();

                // Thêm yêu cầu kết bạn vào sub-collection 'friend_request' của người nhận
                DocumentReference friendRequestRef = FirebaseUtil.allFriendRequestCollection(receiverUserId).document(currentUserUid);
                batch.set(friendRequestRef, friendRequest);

                // Thêm yêu cầu đã gửi vào collection 'sentFriendRequest' của người gửi
                DocumentReference sentRequestRef = FirebaseUtil.addUserToSendFriendRequest().document(receiverUserId);
                batch.set(sentRequestRef, sentFriendRequest);

                // Thực hiện batch
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            StyleableToast.makeText(ProfileUserActivity.this, "Yêu cầu kết bạn đã được gửi", R.style.successToast).show();
                        } else {
                            StyleableToast.makeText(ProfileUserActivity.this, "Có lỗi xảy ra", R.style.errorToast).show();
                        }
                    }
                });
            }
        });


    }
}