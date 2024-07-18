package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatMessRecycleAdapter;
import com.example.chatapp.adapters.SearchUserAdapter;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.ChatRoomModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.Arrays;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChatDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    String chatRoomId;
    SearchUserModel otherUser;
    ImageView imgAvatar;
    TextView txtUserName;
    ImageButton btnSend,btnGoBack,btnSendImage;
    EditText edtMessage;
    RecyclerView chatRecycle;
    ChatRoomModel chatRoomModel;
    ChatMessRecycleAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

//        Nhận dữ liệu từ màn hình Search
        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
//        Lấy ChatUserId
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserUid(),otherUser.getUserId());

        imgAvatar = findViewById(R.id.imgAvatar);
        txtUserName = findViewById(R.id.txtUserName);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSendImage = findViewById(R.id.btnSendImage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatRecycle = findViewById(R.id.chatRecycle);


        btnGoBack.setOnClickListener((v)->{
            finish();
        });



        txtUserName.setText(otherUser.getUser_name());
        Glide.with(this).load(otherUser.getAvatar()).into(imgAvatar);

        getCreateChatRoomModel();
        setUpChatMessageRecycle();

        edtMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Kiểm tra nội dung của EditText và ẩn/hiện nút gửi
                String message = charSequence.toString().trim();
                if (message.isEmpty()) {
                    btnSend.setVisibility(View.GONE); // Ẩn nút gửi
                } else {
                    btnSend.setVisibility(View.VISIBLE); // Hiển thị nút gửi
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageUer(message,1);
                    edtMessage.setText(""); // Xóa nội dung sau khi gửi
                }
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    void sendMessageUer(String message,int methodMess){

//         Cập nhật thời gian của tin nhắn cuối cùng trong phòng chat bằng thời gian hiện tại.
        chatRoomModel.setLassMessageTimestamp(Timestamp.now());
//        Cập nhật người gửi tin nhắn cuối cùng bằng UID của người dùng hiện tại.
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserUid());
//        Cập nhật tin nhắn cuối cùng
        if(methodMess==1){
            chatRoomModel.setLassMessageText(message);
        }else if(methodMess==2){
            chatRoomModel.setLassMessageText("Đã gửi một hình ảnh");
        }


//        Lưu thông tin cập nhật
        FirebaseUtil.getChatRooms(chatRoomId).set(chatRoomModel);
//        Đối tượng tin nhắn mới
        ChatMessageModel chatMessageModel = new ChatMessageModel(FirebaseUtil.currentUserUid(),message, Timestamp.now(),methodMess);

        FirebaseUtil.getChatRoomMessage(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                edtMessage.setText("");
            }
        });
    }


//    Tạo phòng chat giưã 2 người
    void getCreateChatRoomModel() {
        FirebaseUtil.getChatRooms(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Lấy kết quả từ Firestore và chuyển đổi thành đối tượng ChatRoomModel
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);

                if (chatRoomModel == null) {

                    chatRoomModel = new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.currentUserUid(),otherUser.getUserId()),
                            Timestamp.now(),
                            "");
                    FirebaseUtil.getChatRooms(chatRoomId).set(chatRoomModel);
                }
            } else {
                // Xử lý khi không thể truy vấn Firestore thành công
            }
        });
    }

//    Hiển thị tin nhắn lên màn hình
    void setUpChatMessageRecycle(){
        Query query = FirebaseUtil.getChatRoomMessage(chatRoomId).orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        // Tạo adapter, luôn luôn phải có để sử dụng adapter
        adapter = new ChatMessRecycleAdapter(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

//        Đảo ngược thứ tự Item, new chat xuất hiện đầu tiên
        manager.setReverseLayout(true);
        chatRecycle.setLayoutManager(manager);
        // Set up the RecyclerView
        chatRecycle.setAdapter(adapter);
//        Lắng nghe thay đổi của recycleView
        adapter.startListening();
//        Cuộn RecyclerView mượt mà đến vị trí đầu tiên (tin nhắn mới nhất) khi có tin nhắn mới được chèn vào.
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecycle.smoothScrollToPosition(0);
            }
        });
    }

//    Mở thư viện ảnh
    void openGallery(){
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

//    Tải ảnh lên Firebase Storage
    void uploadImageStorage(Uri uriImage){
        StorageReference storageRef = FirebaseUtil.getStorageReferenceForImage(chatRoomId);
        storageRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        sendMessageUer(uri.toString(),2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StyleableToast.makeText(ChatDetailActivity.this, "Có lỗi xảy ra", R.style.errorToast).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(ChatDetailActivity.this, "Có lỗi xảy ra", R.style.errorToast).show();
            }});
    }


}