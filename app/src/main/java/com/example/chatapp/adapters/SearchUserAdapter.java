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
import com.example.chatapp.activities.ProfileUserActivity;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<SearchUserModel, SearchUserAdapter.UserViewHolder> {

    private final Context context;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<SearchUserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull SearchUserModel model) {
        holder.txtNameSearch.setText(model.getUser_name());
        holder.txtStatusSearch.setText(model.getEmail());
        if(model.getUserId().equals(FirebaseUtil.currentUserUid())){
            holder.txtNameSearch.setText(String.format("%s(Tôi)", model.getUser_name()));
        }
        Glide.with(holder.itemView.getContext())
                .load(model.getAvatar())
                .into(holder.imgAvatarSearch);


        if ("online".equals(model.getStatus())) {
            holder.imgStatus.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileUserActivity.class);
                AndroidUtil.passUserModelAsIntent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                FirebaseUtil.getSearchHistoryCollection(FirebaseUtil.currentUserUid())
                        .whereEqualTo("userId", model.getUserId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                                FirebaseUtil.getSearchHistoryCollection(FirebaseUtil.currentUserUid()).add(model);
                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatarSearch,imgStatus;
        TextView txtNameSearch, txtStatusSearch;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatarSearch = itemView.findViewById(R.id.imgAvatarSearch);
            txtNameSearch = itemView.findViewById(R.id.txtNameSearch);
            txtStatusSearch = itemView.findViewById(R.id.txtStatusSearch);
            imgStatus = itemView.findViewById(R.id.imgStatus);

        }
    }
}
