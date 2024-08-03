package com.example.chatapp.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rcvUsers;
    private UserAdapter userAdapter;
    private EditText edtSearch;
    private Button btnAddNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize RecyclerView and EditText
        rcvUsers = findViewById(R.id.rcvUsers);
        edtSearch = findViewById(R.id.edtSearch);
        btnAddNewUser = findViewById(R.id.btnAddNewUser);
        rcvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Set up initial query
        Query query = FirebaseUtil.allUserCollection()
                .orderBy("user_name", Query.Direction.ASCENDING);

        // Configure FirestoreRecyclerOptions
        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        // Set up UserAdapter
        userAdapter = new UserAdapter(options, this);
        rcvUsers.setAdapter(userAdapter);

        // Set up TextWatcher for search functionality
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        btnAddNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminCreateUserActivity.class);
            startActivity(intent);
        });
    }

    private void searchUsers(String searchText) {
        Query query = FirebaseUtil.allUserCollection()
                .whereGreaterThanOrEqualTo("user_name", searchText)
                .whereLessThanOrEqualTo("user_name", searchText + "\uf8ff");

        // Cấu hình FirestoreRecyclerOptions với truy vấn tìm kiếm
        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        // Dừng lắng nghe với adapter hiện tại
        if (userAdapter != null) {
            userAdapter.stopListening();
        }

        // Tạo adapter mới với truy vấn tìm kiếm
        userAdapter = new UserAdapter(options, this);

        // Cấu hình RecyclerView với adapter và LayoutManager
        rcvUsers.setLayoutManager(new LinearLayoutManager(this));
        rcvUsers.setAdapter(userAdapter);

        // Bắt đầu lắng nghe dữ liệu từ Firestore
        userAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userAdapter != null) {
            userAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userAdapter != null) {
            userAdapter.stopListening();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
    }
}