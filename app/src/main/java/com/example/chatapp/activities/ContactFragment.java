package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

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
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

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

        setupRecycleListFriend();


        return  view;
    }

    void setupRecycleListFriend(){
        Query query = FirebaseUtil.allFriendUserCollection(FirebaseUtil.currentUserUid());

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