package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatMessRecycleAdapter extends FirestoreRecyclerAdapter<ChatMessageModel,ChatMessRecycleAdapter.ChatMessageViewHolder> {

    Context context;
    public ChatMessRecycleAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position, @NonNull ChatMessageModel model) {
//        Kiểm tra xem đâu là tin nhắn của người dùng đang đang nhập để hiển thị giao diện
        if(model.getSenderId().equals(FirebaseUtil.currentUserUid())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTxt.setText(model.getMessage());
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.letChatTxt.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_detail, parent, false);
        return new ChatMessageViewHolder(view);
    }



    static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout,rightChatLayout;
        TextView letChatTxt,rightChatTxt;

        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.leftChatLayout);
            rightChatLayout = itemView.findViewById(R.id.rightChatLayout);
            letChatTxt = itemView.findViewById(R.id.letChatTxt);
            rightChatTxt = itemView.findViewById(R.id.rightChatTxt);

        }
    }
}
