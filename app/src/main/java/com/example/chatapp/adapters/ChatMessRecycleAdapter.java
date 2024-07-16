package com.example.chatapp.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        if(model.getMessageMethod()==1){
            if(model.getSenderId().equals(FirebaseUtil.currentUserUid())){
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatTxt.setText(String.format("%s\n%s", model.getMessage(), FirebaseUtil.timestampToStringFormat(model.getTimestamp())));
            } else {
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTxt.setText(model.getMessage());
            }
        } else if(model.getMessageMethod()== 2){
            if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightChatImage.setVisibility(View.VISIBLE); // Show image view for right side
                holder.rightChatTxt.setVisibility(View.GONE); // Hide text view for right side
                Glide.with(context).load(model.getMessage()).into(holder.rightChatImage);
            } else {
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.leftChatTxt.setVisibility(View.GONE); // Hide text view for left side
                holder.leftChatImage.setVisibility(View.VISIBLE); // Show image view for left side
                Glide.with(context).load(model.getMessage()).into(holder.leftChatImage);
            }
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
        TextView leftChatTxt,rightChatTxt;
        ImageView rightChatImage,leftChatImage;

        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.leftChatLayout);
            rightChatLayout = itemView.findViewById(R.id.rightChatLayout);
            leftChatTxt = itemView.findViewById(R.id.leftChatTxt);
            rightChatTxt = itemView.findViewById(R.id.rightChatTxt);
            leftChatImage = itemView.findViewById(R.id.leftChatImage);
            rightChatImage = itemView.findViewById(R.id.rightChatImage);

        }
    }
}
