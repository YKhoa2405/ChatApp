package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.ChatData;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatData> chatMessages;
    private Context context;

    // Constructor
    public ChatAdapter(Context context, List<ChatData> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    // Liên kết dữ liệu với các khung nhìn
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatData chatMessage = chatMessages.get(position);
        holder.txtLastChat.setText(chatMessage.getLastChat());
        holder.txtTimeChat.setText(chatMessage.getTimeChat());
        holder.txtName.setText(chatMessage.getName());

        // Load profile image using Glide
        Glide.with(context).load(chatMessage.getAvatarResId()).into(holder.imgAvatar);

        // Xử lý click từng item
        holder.itemView.setOnClickListener(v -> {
            // Handle item click
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ViewHolder class
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLastChat, txtTimeChat;
        ImageView imgAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastChat = itemView.findViewById(R.id.txtLastChat);
            txtTimeChat = itemView.findViewById(R.id.txtTimeChat);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
