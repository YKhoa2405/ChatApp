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
import com.example.chatapp.activities.ChatDetailActivity;
import com.example.chatapp.activities.ProfileUserActivity;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class FriendRequestAdapter extends FirestoreRecyclerAdapter<SearchUserModel, FriendRequestAdapter.FriendRequestViewHolder> {

    private final Context context;

    public FriendRequestAdapter(@NonNull FirestoreRecyclerOptions<SearchUserModel> options, Context context) {
        super(options);
        this.context = context;
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
//                id của người nhận
                String currentUserUid = FirebaseUtil.currentUserUid();
//                id của người gửi
                String receiverUserId = model.getUserId();
                Timestamp createAddFriend = Timestamp.now();

                // Tạo yêu cầu kết bạn
                Map<String, Object> friendRequest = new HashMap<>();
                friendRequest.put("userId", currentUserUid);
                friendRequest.put("createAddFriend", createAddFriend);

                // Tạo yêu cầu đã gửi
                Map<String, Object> sentFriendRequest = new HashMap<>();
                sentFriendRequest.put("receiverId", receiverUserId);
                sentFriendRequest.put("createAddFriend", createAddFriend);

                // Lấy instance của Firestore và batch
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                WriteBatch batch = db.batch();

                // Thêm yêu cầu kết bạn vào sub-collection 'friend_request' của người nhận
                DocumentReference friendRequestRef = FirebaseUtil.allFriendUserCollection(receiverUserId).document(currentUserUid);
                batch.set(friendRequestRef, friendRequest);

                // Thêm yêu cầu đã gửi vào collection 'sentFriendRequest' của người gửi
                DocumentReference sentRequestRef = FirebaseUtil.allFriendUserCollection(currentUserUid).document(receiverUserId);
                batch.set(sentRequestRef, sentFriendRequest);

//                Xóa khỏi danh sách chờ kết bạn
                DocumentReference deleteRequestRef = FirebaseUtil.allFriendRequestCollection(currentUserUid).document(receiverUserId);
                batch.delete(deleteRequestRef);


                // Thực hiện batch
                batch.commit();
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
        Button btnRefuse,btnAccept;

        FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnRefuse=itemView.findViewById(R.id.btnRefuse);

        }
    }
}
