package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ListFriendAdapter;
import com.example.chatapp.adapters.SearchUserAdapter;
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


    CardView btnAddFriend,btnSearchFriend;
    RecyclerView recyclerListFriend;
    ListFriendAdapter adapter;

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_contact, container, false);

        btnAddFriend = view.findViewById(R.id.btnAddFriend);
        btnSearchFriend=view.findViewById(R.id.btnSearchFriend);
        recyclerListFriend =view.findViewById(R.id.recycleListFriend);

        getFriendIds();


        return  view;
    }

    void getFriendIds() {
        FirebaseUtil.allFriendUserCollection(FirebaseUtil.currentUserUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<String> friendIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FriendModel friendModel = document.toObject(FriendModel.class);
                        friendIds.add(friendModel.getUserId());
                    }
                    setupRecycleListFriend(friendIds);
                }
            }
        });
    }


    void setupRecycleListFriend(List<String> friendIds){
        Query query = FirebaseUtil.allUserCollection().whereIn(FieldPath.documentId(),friendIds);

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