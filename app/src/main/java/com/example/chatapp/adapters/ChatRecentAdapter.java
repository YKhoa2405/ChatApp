package com.example.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.activities.ChatDetailActivity;
import com.example.chatapp.model.ChatRoomModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecentAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, ChatRecentAdapter.ChatRecentViewHolder> {

    Context context;

    public ChatRecentAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRecentViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                boolean lassMessSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserUid());
                int max_length = 40;
                String messageText = model.getLassMessageText();

                SearchUserModel searchUserModel=task.getResult().toObject(SearchUserModel.class);


                if (messageText.length() > max_length) {
                    messageText = messageText.substring(0, max_length) + " ...";
                }

                if (lassMessSentByMe) {
                    holder.txtLastChat.setText(String.format("Bạn: %s", messageText));
                }
                else{
                    holder.txtLastChat.setText(String.format(messageText));
                }
                holder.txtName.setText(searchUserModel.getUser_name());
                holder.txtTimeChat.setText(FirebaseUtil.timestampToStringFormat(model.getLassMessageTimestamp()));
                Glide.with(holder.itemView.getContext())
                            .load(searchUserModel.getAvatar())
                            .into(holder.imgAvatar);

                if ("online".equals(searchUserModel.getStatus())) {
                    holder.imgStatus.setVisibility(View.VISIBLE);
                    holder.imgStatus1.setVisibility(View.VISIBLE);

                } else {
                    holder.imgStatus.setVisibility(View.GONE);
                    holder.imgStatus1.setVisibility(View.GONE);

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatDetailActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent,searchUserModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public ChatRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatRecentViewHolder(view);
    }

    // ViewHolder class
    public static class ChatRecentViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLastChat, txtTimeChat;
        ImageView imgAvatar,imgStatus,imgStatus1;

        public ChatRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastChat = itemView.findViewById(R.id.txtLastChat);
            txtTimeChat = itemView.findViewById(R.id.txtTimeChat);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgStatus=itemView.findViewById(R.id.imgStatus);
            imgStatus1=itemView.findViewById(R.id.imgStatus1);

        }
    }
}
