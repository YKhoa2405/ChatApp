package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ListFriendAdapter;
import com.example.chatapp.model.FriendModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {

    ImageButton btnAddFriend;
    SearchView btnSearchFriend;
    RecyclerView recyclerListFriend;
    ListFriendAdapter adapter;
    TextView emptyTextView,txtCountFriend;
    List<String> friendIds = new ArrayList<>();

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        btnAddFriend = view.findViewById(R.id.btnAddFriend);
        btnSearchFriend = view.findViewById(R.id.btnSearchFriend);
        recyclerListFriend = view.findViewById(R.id.recycleListFriend);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        txtCountFriend = view.findViewById(R.id.txtCountFriend);

        getFriendIds();

        btnSearchFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nextText) {
                searchFriends(nextText);
                return true;
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),FriendRequestActivity.class));
            }
        });

        return view;
    }

    void getFriendIds() {
        FirebaseUtil.allFriendUserCollection(FirebaseUtil.currentUserUid()).whereEqualTo("status","friend").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    friendIds.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FriendModel friendModel = document.toObject(FriendModel.class);
                        if (friendModel.getUserId() != null) {
                            friendIds.add(friendModel.getUserId());
                        }
                    }
                    setupRecycleListFriend(friendIds);
                    updateFriendCount(friendIds.size());
                } else {
                    // Xử lý trường hợp truy vấn thất bại
                    recyclerListFriend.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    updateFriendCount(0);

                }
            }
        });
    }

    void setupRecycleListFriend(List<String> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            recyclerListFriend.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        }

        recyclerListFriend.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);

        Query query = FirebaseUtil.allUserCollection().whereIn(FieldPath.documentId(), friendIds);

        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        // Create the adapter
        adapter = new ListFriendAdapter(options, getContext());
        recyclerListFriend.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the RecyclerView
        recyclerListFriend.setAdapter(adapter);

        // Start listening for changes
        adapter.startListening();

    }

    private void updateFriendCount(int count) {
        txtCountFriend.setText(count + " bạn bè");
    }

    void searchFriends(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // Hiển thị lại danh sách bạn bè ban đầu
            setupRecycleListFriend(friendIds);
            return;
        }

        // Tạo truy vấn Firestore dựa trên userName
        Query query = FirebaseUtil.allUserCollection()
                .whereIn(FieldPath.documentId(), friendIds)
                .whereGreaterThanOrEqualTo("user_name", searchText)
                .whereLessThanOrEqualTo("user_name", searchText + "\uf8ff");

        // Tạo FirestoreRecyclerOptions với truy vấn mới
        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        // Tạo adapter mới
        adapter = new ListFriendAdapter(options, getContext());
        recyclerListFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerListFriend.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
