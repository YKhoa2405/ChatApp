package com.example.chatapp.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatMessRecycleAdapter;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.ChatRoomModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AESUtil;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import java.util.Arrays;


import javax.crypto.SecretKey;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChatDetailActivity extends AppCompatActivity {

    SearchView searchChat;
    LinearLayout layoutTopBar,layoutInfo;
    Toolbar topBar;
    String chatRoomId;
    SearchUserModel otherUser;
    ImageView imgAvatar,imgAvatarChat,imgSearchText,imgCallVideo;
    TextView txtUserName,txtUserNameChat;
    ImageButton btnSend, btnSendImage;
    EditText edtMessage;
    RecyclerView chatRecycle;
    ChatRoomModel chatRoomModel;
    ChatMessRecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserUid(), otherUser.getUserId());

        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatarChat = findViewById(R.id.imgAvatarChat);
        imgSearchText=findViewById(R.id.imgSearchText);
        imgCallVideo = findViewById(R.id.imgCallVideo);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserNameChat = findViewById(R.id.txtUserNameChat);
        btnSendImage = findViewById(R.id.btnSendImage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatRecycle = findViewById(R.id.chatRecycle);
        layoutTopBar = findViewById(R.id.layoutTopBar);
        layoutInfo = findViewById(R.id.layoutInfo);
        topBar = findViewById(R.id.topBar);

        txtUserName.setText(otherUser.getUser_name());
        txtUserNameChat.setText(otherUser.getUser_name());
        Glide.with(this).load(otherUser.getAvatar()).into(imgAvatar);
        Glide.with(this).load(otherUser.getAvatar()).into(imgAvatarChat);

        getCreateChatRoomModel();
        setUpChatMessageRecycle();

        topBar.setNavigationOnClickListener(task->{finish();});

        imgAvatar.setOnClickListener(task->{
            Intent intent = new Intent(this, ProfileUserActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,otherUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String message = charSequence.toString().trim();
                btnSend.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        btnSendImage.setOnClickListener(view -> openGallery());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    try {
                        SecretKey key = AESUtil.generateKey();
                        // Chuyển đổi khóa thành chuỗi Base64
                        String base64Key = AESUtil.keyToBase64(key);
                        String encryptedMessage = AESUtil.encrypt(message, key);
                        sendMessageUser(encryptedMessage, 1,base64Key);
                        edtMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imgSearchText.setOnClickListener(task->{
            Intent intent = new Intent(this, SearchChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,otherUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

//        String roomID = chatRoomModel.getChatRoomId();
//        imgCallVideo.setOnClickListener(view -> {
//            if (roomID != null && !roomID.isEmpty()) {
//                startCall(roomID);
//            } else {
//                Toast.makeText(ChatDetailActivity.this, "Invalid room ID", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    void sendMessageUser(String message, int methodMess, String key) {

        chatRoomModel.setLassMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserUid());
        chatRoomModel.setLassMessageText(methodMess == 1 ? message : "Đã gửi một hình ảnh");
        chatRoomModel.setKey(key);

        FirebaseUtil.getChatRooms(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(FirebaseUtil.currentUserUid(), message, Timestamp.now(), methodMess, false,key);

        FirebaseUtil.getChatRoomMessage(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                edtMessage.setText("");
            }
        });
    }

    void getCreateChatRoomModel() {
        FirebaseUtil.getChatRooms(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new ChatRoomModel(chatRoomId, Arrays.asList(FirebaseUtil.currentUserUid(), otherUser.getUserId()), Timestamp.now(), "","");
                    FirebaseUtil.getChatRooms(chatRoomId).set(chatRoomModel);
                }
            }
        });
    }

    void setUpChatMessageRecycle() {
        Query query = FirebaseUtil.getChatRoomMessage(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        adapter = new ChatMessRecycleAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRecycle.setLayoutManager(manager);
        chatRecycle.setAdapter(adapter);

        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecycle.smoothScrollToPosition(0);
                updateEmptyView();
            }

            private void updateEmptyView() {
                if (adapter.getItemCount() == 0) {
                    layoutInfo.setVisibility(View.VISIBLE);
                    chatRecycle.setVisibility(View.GONE);
                }else {
                    layoutInfo.setVisibility(View.GONE);
                    chatRecycle.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateEmptyView();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                updateEmptyView();
            }
        });
    }


    void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGallery.launch(intent);
    }

    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri uriImage = data.getData();
                            uploadImageStorage(uriImage);
                        }
                    }
                }
            });

    void uploadImageStorage(Uri uriImage) {
        StorageReference storageRef = FirebaseUtil.getStorageReferenceForImage(chatRoomId);
        storageRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        sendMessageUser(uri.toString(), 2,null);
                    }
                }).addOnFailureListener(e -> StyleableToast.makeText(ChatDetailActivity.this, "Có lỗi xảy ra", R.style.errorToast).show());
            }
        }).addOnFailureListener(e -> StyleableToast.makeText(ChatDetailActivity.this, "Có lỗi xảy ra", R.style.errorToast).show());
    }

//    private void startCall(String roomID) {
//        // Khởi tạo dịch vụ gọi nếu chưa làm
//        processService(FirebaseUtil.currentUserUid());
//    }
//
//    void processService(String userId){
//        Application application = getApplication(); // Android's application context
//        long appID =80127349;   // yourAppID
//        String appSign ="27e13035dbf4275a8279f0be28673211f41184e16bca37c48012fd1193014798";  // yourAppSign
//        String userID =userId; // yourUserID, userID should only contain numbers, English characters, and '_'.
//        String userName = otherUser.getUser_name();   // yourUserName
//
//        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
//
//        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
//
//    }

}
