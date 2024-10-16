package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    LinearLayout btnLogout,btnEditProfile;
    ImageView imgAvatar;
    TextView txtUserName,txtBio;
    SearchUserModel currentUserModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar =view.findViewById(R.id.imgAvatar);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtBio=view.findViewById(R.id.txtBio);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        getUserData();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.logout();
                FirebaseUtil.UpdateStatusUser(currentUserModel.getUserId(),"offline");
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(t->{
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,currentUserModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        return  view;
    }

    void getUserData(){
        FirebaseUtil.currentUserDetail().get().addOnCompleteListener(task->{
            currentUserModel= task.getResult().toObject(SearchUserModel.class);
            txtUserName.setText(currentUserModel.getUser_name());
            if(currentUserModel.getBio().equals("null")){
                txtBio.setVisibility(View.GONE);

            }else{
                txtBio.setText(currentUserModel.getBio());
            }

            Glide.with(this)
                    .load(currentUserModel.getAvatar())
                    .into(imgAvatar);
        });
    }
}