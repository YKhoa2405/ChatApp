package com.example.chatapp.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.activities.WelcomeActivity;
import com.example.chatapp.adapters.ListFriendAdapter;
import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rcvUsers;
    private UserAdapter adapter;
    private SearchView edtSearch;
    private FloatingActionButton btnAddNewUser, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize RecyclerView and EditText
        rcvUsers = findViewById(R.id.rcvUsers);
        edtSearch = findViewById(R.id.edtSearch);
        btnAddNewUser = findViewById(R.id.btnAddNewUser);
        btnLogout = findViewById(R.id.btnLogout);

        setupRecycleListUser();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.logout();
                Intent intent = new Intent(AdminActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Set up TextWatcher for search functionality
        edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nextText) {
                searchUsers(nextText);
                return true;
            }
        });
        btnAddNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminCreateUserActivity.class);
            startActivity(intent);
        });
    }

    void setupRecycleListUser() {

        rcvUsers.setVisibility(View.VISIBLE);

        Query query = FirebaseUtil.allUserCollection()
                .whereEqualTo("role", "2")
                .orderBy("user_name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        // Create the adapter
        adapter = new UserAdapter(options, this);
        rcvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Set up the RecyclerView
        rcvUsers.setAdapter(adapter);

        // Start listening for changes
        adapter.startListening();

    }


    private void searchUsers(String searchText) {
        String searchQuery = searchText.toLowerCase();
        Query query = FirebaseUtil.allUserCollection()
                .whereEqualTo("role", "2")
                .orderBy("user_name")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff");

        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new UserAdapter(options, this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                // Kiểm tra nếu không có người dùng nào khớp với tìm kiếm
                if (adapter.getItemCount() == 0) {
                    // Hiển thị thông báo
                    // Bạn có thể hiển thị TextView hoặc một thông báo khác tại đây
                }
            }
        });
        rcvUsers.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();  // Bắt đầu lắng nghe lại từ Firestore
        }
    }

}