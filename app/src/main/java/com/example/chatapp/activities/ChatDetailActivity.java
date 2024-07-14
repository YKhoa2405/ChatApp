package com.example.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.Arrays;

public class ChatDetailActivity extends AppCompatActivity {

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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString().trim();
                if(message.isEmpty()){
                    return;
                }
                else{
                    sendMessageUer(message);
                }

            }
        });
    }

    void sendMessageUer(String message){

//         Cập nhật thời gian của tin nhắn cuối cùng trong phòng chat bằng thời gian hiện tại.
        chatRoomModel.setLassMessageTimestamp(Timestamp.now());
//        Cập nhật người gửi tin nhắn cuối cùng bằng UID của người dùng hiện tại.
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserUid());
//        Lưu thông tin cập nhật
        FirebaseUtil.getChatRooms(chatRoomId).set(chatRoomModel);
//        Đối tượng tin nhắn mới
        ChatMessageModel chatMessageModel = new ChatMessageModel(FirebaseUtil.currentUserUid(),message, Timestamp.now());

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

    void setUpChatMessageRecycle(){
//        Câu lệnh truy vấn
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


}