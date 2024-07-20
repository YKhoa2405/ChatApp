package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton btnGoBack;
    ImageView imgEditAvatar;
    EditText edtEditUserName,edtEditBio;
    Button btnSaveEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        btnSaveEdit=findViewById(R.id.btnSaveEdit);
        btnGoBack=findViewById(R.id.btnGoBack);
        imgEditAvatar=findViewById(R.id.imgEditAvatar);
        edtEditBio =findViewById(R.id.edtEditBio);
        edtEditUserName=findViewById(R.id.edtEditUserName);

        btnGoBack.setOnClickListener(c->{
            finish();
        });

        imgEditAvatar.setOnClickListener(c->{
            openGallery();
        });
    }

    //    Mở thư viện ảnh
    void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGallery.launch(intent);
    }
    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri uriImage = data.getData();
//                            uploadImageStorage(uriImage);
                        }
                    }
                }
            });
}