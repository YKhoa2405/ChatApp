package com.example.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.FriendModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import io.github.muddz.styleabletoast.StyleableToast;

public class FriendRequestAdapter extends FirestoreRecyclerAdapter<SearchUserModel, FriendRequestAdapter.FriendRequestViewHolder> {

    private final Context context;
    private final OnFriendRequestUpdateListener listener;

    public interface OnFriendRequestUpdateListener {
        void onFriendRequestUpdated();
    }

    public FriendRequestAdapter(@NonNull FirestoreRecyclerOptions<SearchUserModel> options, Context context, OnFriendRequestUpdateListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull SearchUserModel model) {
        holder.txtUserName.setText(model.getUser_name());
        Glide.with(holder.itemView.getContext())
                .load(model.getAvatar())
                .into(holder.imgAvatar);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherUser = model.getUserId();
                String currentUserUid = FirebaseUtil.currentUserUid();
                Timestamp createAddFriend = Timestamp.now();

                // Cập nhật trạng thái bạn bè
                FirebaseUtil.updateStatusFriend(otherUser).update("status", "friend");
                FirebaseUtil.updateStatusFriend(otherUser).update("create_add_friend", createAddFriend);

                // Thêm user vào friend của người gửi
                FriendModel friendRequest = new FriendModel(currentUserUid, createAddFriend, "friend");
                FirebaseUtil.allFriendUserCollection(otherUser).document(currentUserUid).set(friendRequest)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Notify Activity to update the UI
                                if (listener != null) {
                                    listener.onFriendRequestUpdated();
                                }
                            }
                        });
            }
        });

        holder.btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherUser = model.getUserId();
                String currentUserUid = FirebaseUtil.currentUserUid();
                FirebaseUtil.allFriendUserCollection(currentUserUid).document(otherUser).delete().addOnCompleteListener(task->{
                    if (task.isSuccessful()) {
                        // Notify Activity to update the UI
                        if (listener != null) {
                            listener.onFriendRequestUpdated();
                        }
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtUserName;
        Button btnRefuse, btnAccept;

        FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRefuse = itemView.findViewById(R.id.btnRefuse);
        }
    }
}

