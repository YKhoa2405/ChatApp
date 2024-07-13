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
import com.example.chatapp.model.SearchUserModel;
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
        holder.txtNameSearch.setText(model.getUser_Name());
        holder.txtStatusSearch.setText(model.getEmail());
        if(model.getUser_Name()== null){
            holder.txtNameSearch.setText("hola");
        }

        if (model.getAvatar() != null && !model.getAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(model.getAvatar())
                    .into(holder.imgAvatarSearch);
        } else {
            holder.imgAvatarSearch.setImageResource(R.drawable.forgot_password); // Placeholder image
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatarSearch;
        TextView txtNameSearch, txtStatusSearch;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatarSearch = itemView.findViewById(R.id.imgAvatarSearch);
            txtNameSearch = itemView.findViewById(R.id.txtNameSearch);
            txtStatusSearch = itemView.findViewById(R.id.txtStatusSearch);
        }
    }
}
