package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;


import javax.crypto.SecretKey;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChatDetailActivity extends AppCompatActivity {

    SearchView searchChat;
    LinearLayout layoutTopBar;
    Toolbar topBar;
    String chatRoomId;
    SearchUserModel otherUser;
    ImageView imgAvatar;
    TextView txtUserName;
    ImageButton btnSend, btnSendImage;
    EditText edtMessage;
    RecyclerView chatRecycle;
    ChatRoomModel chatRoomModel;
    ChatMessRecycleAdapter adapter;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserUid(), otherUser.getUserId());

        imgAvatar = findViewById(R.id.imgAvatar);
        txtUserName = findViewById(R.id.txtUserName);
        btnSendImage = findViewById(R.id.btnSendImage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatRecycle = findViewById(R.id.chatRecycle);
        searchChat = findViewById(R.id.searchChat);
        layoutTopBar = findViewById(R.id.layoutTopBar);
        topBar = findViewById(R.id.topBar);

        txtUserName.setText(otherUser.getUser_name());
        Glide.with(this).load(otherUser.getAvatar()).into(imgAvatar);

        getCreateChatRoomModel();
        setUpChatMessageRecycle();

        topBar.setNavigationOnClickListener(task->{finish();});

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_chat_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item1) {
            // Xử lý khi nhấp vào "Tìm kiếm tin nhắn"
            StyleableToast.makeText(ChatDetailActivity.this, "block thất bại", R.style.errorToast).show();

            return true;
        } else if (id == R.id.item2) {
            // Xử lý khi nhấp vào "Chặn người dùng"
            StyleableToast.makeText(ChatDetailActivity.this, "block thất bại", R.style.errorToast).show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    void sendMessageUser(String message, int methodMess,String key) {

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

        adapter = new ChatMessRecycleAdapter(options, this,privateKey);
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
}
