package com.example.chatapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserAdapter extends FirestoreRecyclerAdapter<SearchUserModel, UserAdapter.UserViewHolder> {

    private final Context context;
    private final FirebaseUser currentUser;

    public UserAdapter(@NonNull FirestoreRecyclerOptions<SearchUserModel> options, Context context) {
        super(options);
        this.context = context;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull SearchUserModel model) {
        holder.txtNameSearch.setText(model.getUser_name() != null ? model.getUser_name() : "Thông tin không có sẵn");
        holder.txtStatusSearch.setText(model.getEmail());

        Glide.with(holder.itemView.getContext())
                .load(model.getAvatar())
                .into(holder.imgAvatarSearch);

        holder.imgStatus.setVisibility("online".equals(model.getStatus()) ? View.VISIBLE : View.GONE);

        holder.btnSetAdmin.setOnClickListener(v -> showAdminConfirmationDialog(model.getUserId()));
        holder.btnDeleteUserAdmin.setOnClickListener(v -> {
            if (!model.getUserId().equals(currentUser.getUid())) {
                showDeleteConfirmationDialog(model.getUserId(), holder.getAdapterPosition());
            } else {
                StyleableToast.makeText(context, "Bạn không thể tự xóa tài khoản của mình", R.style.errorToast).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(String userId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa vĩnh viễn tài khoản")
                .setMessage("Tài khoản này sẽ bị xóa vĩnh viễn?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(userId, position))
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteUser(String userId, int position) {
        FirebaseUtil.otherUserDetail(userId).delete()
                .addOnSuccessListener(unused -> {
                    StyleableToast.makeText(context, "Xóa tài khoản thành công", R.style.successToast).show();
                })
                .addOnFailureListener(e -> StyleableToast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại", R.style.errorToast).show());
    }

    private void showAdminConfirmationDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm quản trị viên")
                .setMessage("Tài khoản này được thêm là quản trị viên?")
                .setPositiveButton("Xác nhận", (dialog, which) -> updateUserRole(userId))
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateUserRole(String userId) {
        FirebaseUtil.otherUserDetail(userId).update("role", "1")
                .addOnSuccessListener(aVoid -> {
                    StyleableToast.makeText(context, "Cập nhật thành công", R.style.successToast).show();
                })
                .addOnFailureListener(e -> StyleableToast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại: " + e.getMessage(), R.style.errorToast).show());
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatarSearch, imgStatus;
        TextView txtNameSearch, txtStatusSearch;
        ImageButton btnSetAdmin, btnDeleteUserAdmin;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatarSearch = itemView.findViewById(R.id.imgAvatarSearch);
            txtNameSearch = itemView.findViewById(R.id.txtNameSearch);
            txtStatusSearch = itemView.findViewById(R.id.txtStatusSearch);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            btnSetAdmin = itemView.findViewById(R.id.btnSetAdmin);
            btnDeleteUserAdmin = itemView.findViewById(R.id.btnDeleteUserAdmin);
        }
    }



}
