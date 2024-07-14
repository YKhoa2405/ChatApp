package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatRecentAdapter;
import com.example.chatapp.model.ChatRoomModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private RecyclerView recycleChatRecent;
    private ChatRecentAdapter chatRecentAdapter;
    private ImageButton btnSearch;
    private ImageView imgAvatar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        btnSearch = view.findViewById(R.id.btnSearch);
        recycleChatRecent = view.findViewById(R.id.recycleChatRecent);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
            }
        });

        setUpRecycleChatRecent();

        return view;
    }

    void setUpRecycleChatRecent(){
        Query query = FirebaseUtil.allChatRoomCollection()
                .whereArrayContains("userIds",FirebaseUtil.currentUserUid())
                .orderBy("lassMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class)
                .build();

        // Set up the RecyclerView
        chatRecentAdapter = new ChatRecentAdapter(options, getContext());
        recycleChatRecent.setLayoutManager(new LinearLayoutManager(getContext()));

        recycleChatRecent.setAdapter(chatRecentAdapter);
        chatRecentAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatRecentAdapter != null) {
            chatRecentAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatRecentAdapter != null) {
            chatRecentAdapter.stopListening();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public  void onResume(){
        super.onResume();
        if(chatRecentAdapter!=null){
            chatRecentAdapter.notifyDataSetChanged();
        }
    }

}
