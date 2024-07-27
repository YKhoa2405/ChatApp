package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.FriendRequestAdapter;
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

public class FriendRequestActivity extends AppCompatActivity implements FriendRequestAdapter.OnFriendRequestUpdateListener {

    private RecyclerView recycleFriendRequest;
    private FriendRequestAdapter adapter;
    private TextView emptyTextView,txtCountFriendRequest;
    private ImageButton btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_request);

        recycleFriendRequest = findViewById(R.id.recycleFriendRequest);
        emptyTextView = findViewById(R.id.emptyTextView);
        btnGoBack = findViewById(R.id.btnGoBack);
        txtCountFriendRequest = findViewById(R.id.txtCountFriendRequest);
        btnGoBack.setOnClickListener(v -> finish());

        getFriendRequestIds();
    }

    private void getFriendRequestIds() {
        FirebaseUtil.allFriendUserCollection(FirebaseUtil.currentUserUid())
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                List<String> friendIds = new ArrayList<>();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String friendId = document.getId();
                                    friendIds.add(friendId);
                                }
                                setupRecycleFriendRequest(friendIds);
                            } else {
                                setupRecycleFriendRequest(new ArrayList<>());
                            }
                        }
                    }
                });
    }

    private void setupRecycleFriendRequest(List<String> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            recycleFriendRequest.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recycleFriendRequest.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            Query query = FirebaseUtil.allUserCollection().whereIn(FieldPath.documentId(), friendIds);

            FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                    .setQuery(query, SearchUserModel.class)
                    .build();

            if (adapter != null) {
                adapter.stopListening();
                int countFriend = adapter.getItemCount();
                txtCountFriendRequest.setText(countFriend);
            }

            adapter = new FriendRequestAdapter(options, this, this);
            recycleFriendRequest.setLayoutManager(new LinearLayoutManager(this));
            recycleFriendRequest.setAdapter(adapter);

            adapter.startListening();
        }
    }

    @Override
    public void onFriendRequestUpdated() {
        getFriendRequestIds();
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
}
