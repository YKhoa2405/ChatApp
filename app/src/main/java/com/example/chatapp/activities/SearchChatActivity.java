package com.example.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ListFriendAdapter;
import com.example.chatapp.adapters.SearchChatAdapter;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AESUtil;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

public class SearchChatActivity extends AppCompatActivity {

    ImageButton btnGoBack;
    RecyclerView recycleSearchChat;
    SearchView searchChat;
    SearchUserModel otherUser;
    String chatRoomId;
    TextView txtEqual;
    LinearLayout layoutSearchNull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_chat);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserUid(), otherUser.getUserId());

        btnGoBack = findViewById(R.id.btnGoBack);
        recycleSearchChat = findViewById(R.id.recycleSearchChat);
        searchChat  = findViewById(R.id.searchChat);
        txtEqual = findViewById(R.id.txtEqual);
        layoutSearchNull = findViewById(R.id.layoutSearchNull);

        btnGoBack.setOnClickListener(task->{finish();});

        setupSearchView();


    }

    private void setupSearchView() {
        searchChat.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (!newText.isEmpty()) {
                    setUpRecycleSearchChat(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    void setUpRecycleSearchChat(String searchText) {
        FirebaseUtil.getChatRoomMessage(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<ChatMessageModel> chatMessages = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    ChatMessageModel model = document.toObject(ChatMessageModel.class);
                    if (model != null) {
                        try {
                            SecretKey key = AESUtil.base64ToKey(model.getSecretKey());
                            String decryptedMessage = AESUtil.decrypt(model.getMessage(), key);
                            // Tạo pattern cho từ hoàn chỉnh
                            String regex = "\\b" + Pattern.quote(searchText.toLowerCase()) + "\\b";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(decryptedMessage.toLowerCase());
                            if (matcher.find()) {
                                chatMessages.add(model);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Set up adapter with filtered data
                SearchChatAdapter adapter = new SearchChatAdapter(chatMessages, getApplicationContext(),searchText);
                recycleSearchChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recycleSearchChat.setAdapter(adapter);

                int countMessage = adapter.getItemCount();
                txtEqual.setText(String.format("%d tin nhắn khớp", countMessage));

            }else{
                recycleSearchChat.setVisibility(View.GONE);
                txtEqual.setVisibility(View.GONE);
                layoutSearchNull.setVisibility(View.VISIBLE);
            }
        });
    }


}