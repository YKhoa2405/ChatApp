package com.example.chatapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AESUtil;
import com.example.chatapp.util.FirebaseUtil;

import java.util.List;

import javax.crypto.SecretKey;

public class SearchChatAdapter extends RecyclerView.Adapter<SearchChatAdapter.UserViewHolder> {

    private final Context context;
    private final List<ChatMessageModel> chatMessages;
    private final String searchText; // Để bôi đậm từ tìm kiếm

    public SearchChatAdapter(List<ChatMessageModel> chatMessages, Context context, String searchText) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.searchText = searchText;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ChatMessageModel model = chatMessages.get(position);

        FirebaseUtil.otherUserDetail(model.getSenderId()).get().addOnSuccessListener(task -> {
            if (task.exists()) {
                SearchUserModel otherUser = task.toObject(SearchUserModel.class);
                if (otherUser != null) {
                    holder.txtName.setText(otherUser.getUser_name());
                    Glide.with(context).load(otherUser.getAvatar()).into(holder.imgAvatar);
                }
                try {
                    SecretKey key = AESUtil.base64ToKey(model.getSecretKey());
                    String decryptedMessage = AESUtil.decrypt(model.getMessage(), key);

                    // Bôi đậm từ khóa tìm kiếm trong tin nhắn
                    SpannableString spannableString = new SpannableString(decryptedMessage);
                    if (searchText != null && !searchText.isEmpty()) {
                        String searchTextLower = searchText.toLowerCase();
                        String messageLower = decryptedMessage.toLowerCase();
                        int start = messageLower.indexOf(searchTextLower);

                        while (start >= 0) {
                            int end = start + searchTextLower.length();
                            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            start = messageLower.indexOf(searchTextLower, end);
                        }
                    }

                    holder.txtLastChat.setText(spannableString);
                    holder.txtTime.setText(FirebaseUtil.timestampToStringFormat(model.getTimestamp()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtLastChat, txtTime;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastChat = itemView.findViewById(R.id.txtLastChat);
            txtTime = itemView.findViewById(R.id.txtTimeChat);
        }
    }
}
