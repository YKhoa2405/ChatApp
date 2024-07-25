package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.util.AESUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

import javax.crypto.SecretKey;

public class ChatMessRecycleAdapter extends FirestoreRecyclerAdapter<ChatMessageModel,ChatMessRecycleAdapter.ChatMessageViewHolder> {

    Context context;
    private final PrivateKey privateKey;
    public ChatMessRecycleAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, PrivateKey privateKey) {
        super(options);
        this.context = context;
        this.privateKey = privateKey;
    }


    protected void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // Kiểm tra loại tin nhắn
        if (model.getMessageMethod() == 1) {
            // Tin nhắn text
            if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
                // Tin nhắn từ người dùng hiện tại
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.VISIBLE);
                try {
                    SecretKey key = AESUtil.base64ToKey(model.getSecretKey());
                    String decryptedMessage = AESUtil.decrypt(model.getMessage(), key);
                    holder.rightChatTxt.setText(decryptedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.txtTimeRightChatDetail.setVisibility(View.VISIBLE);
                holder.txtTimeRightChatDetail.setText(getFormattedDateTime(model.getTimestamp()));
            } else {
                // Tin nhắn từ người gửi khác
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.VISIBLE);
                try {
                    SecretKey key = AESUtil.base64ToKey(model.getSecretKey());
                    String decryptedMessage = AESUtil.decrypt(model.getMessage(), key);
                    holder.leftChatTxt.setText(decryptedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.txtTimeLeftChatDetail.setVisibility(View.VISIBLE);
                holder.txtTimeLeftChatDetail.setText(getFormattedDateTime(model.getTimestamp()));
            }
        } else if (model.getMessageMethod() == 2) {
            // Tin nhắn hình ảnh
            if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
                // Tin nhắn hình ảnh từ người dùng hiện tại
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightChatImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getMessage()).into(holder.rightChatImage);
            } else {
                // Tin nhắn hình ảnh từ người gửi khác
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.leftChatImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getMessage()).into(holder.leftChatImage);
            }
        }
    }

    public String getFormattedDateTime(Timestamp timestamp) {
        // Chuyển đổi Timestamp thành Date
        Date date = timestamp.toDate();

        // Lấy ngày hiện tại
        Calendar now = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTime(date);

        // So sánh ngày hiện tại và ngày của tin nhắn
        if (now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)) {
            // Nếu cùng ngày thì chỉ hiển thị giờ và phút
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return format.format(date);
        } else {
            // Nếu khác ngày thì hiển thị ngày tháng năm và giờ phút
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            return format.format(date);
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
        TextView leftChatTxt,rightChatTxt,txtTimeLeftChatDetail,txtTimeRightChatDetail;
        ImageView rightChatImage,leftChatImage;

        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.leftChatLayout);
            rightChatLayout = itemView.findViewById(R.id.rightChatLayout);
            leftChatTxt = itemView.findViewById(R.id.leftChatTxt);
            rightChatTxt = itemView.findViewById(R.id.rightChatTxt);
            leftChatImage = itemView.findViewById(R.id.leftChatImage);
            rightChatImage = itemView.findViewById(R.id.rightChatImage);
            txtTimeLeftChatDetail = itemView.findViewById(R.id.txtTimeLeftChatDetail);
            txtTimeRightChatDetail = itemView.findViewById(R.id.txtTimeRightChatDetail);

        }
    }
}
