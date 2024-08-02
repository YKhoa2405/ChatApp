package com.example.chatapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton btnGoBack;
    ImageView imgEditAvatar;
    EditText edtEditUserName, edtEditBio,edtDateBirthday;
    Button btnSaveEdit;
    SearchUserModel currentUserModel;
    Uri imageUri; // This will hold the selected image URI
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        btnGoBack = findViewById(R.id.btnGoBack);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        edtEditBio = findViewById(R.id.edtEditBio);
        edtEditUserName = findViewById(R.id.edtEditUserName);
        edtDateBirthday = findViewById(R.id.edtDateBirthday);

        currentUserModel = AndroidUtil.getUserModelAsIntent(getIntent());
        edtEditUserName.setText(currentUserModel.getUser_name());
        Glide.with(this)
                .load(currentUserModel.getAvatar())
                .into(imgEditAvatar);
        edtEditBio.setText(currentUserModel.getBio());
        edtDateBirthday.setText(currentUserModel.getBirthDay());

        btnGoBack.setOnClickListener(c -> finish());

        imgEditAvatar.setOnClickListener(c -> openGallery());

        calendar = Calendar.getInstance();
        edtDateBirthday.setOnClickListener(v -> {
            new DatePickerDialog(EditProfileActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        btnSaveEdit.setOnClickListener(view -> {
            String newUserName = edtEditUserName.getText().toString().trim();
            String newBio = edtEditBio.getText().toString();
            String birthDay = edtDateBirthday.getText().toString();
            if (newUserName.isEmpty()) {
                StyleableToast.makeText(EditProfileActivity.this, "Vui lòng không để trống", R.style.errorToast).show();
                return;
            }
            if (newBio.isEmpty()) {
                StyleableToast.makeText(EditProfileActivity.this, "Vui lòng không để trống", R.style.errorToast).show();
                return;
            }
            currentUserModel.setUser_name(newUserName);
            currentUserModel.setBio(newBio);
            currentUserModel.setStatus("online");
            currentUserModel.setBirthDay(birthDay);
            if (imageUri != null) {
                // User selected a new image, upload it and then update Firestore
                uploadImageStorage(imageUri);
            } else {
                // No new image selected, just update Firestore
                updateToFireStore();
            }
        });
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener =
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            };

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Định dạng bạn muốn cho ngày
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("vi", "VN"));
        edtDateBirthday.setText(sdf.format(calendar.getTime())); // Cập nhật EditText với ngày đã chọn
    }


    // Open gallery to pick an image
    void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryLauncher.launch(intent);
    }

    // Handle result from image selection
    ActivityResultLauncher<Intent> openGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData(); // Update imageUri with selected image
                            Glide.with(EditProfileActivity.this)
                                    .load(imageUri)
                                    .into(imgEditAvatar); // Preview selected image
                        }
                    }
                }
            }
    );

    // Upload selected image to Firebase Storage
    void uploadImageStorage(Uri uriImage) {
        StorageReference storageRef = FirebaseUtil.getStorageReferenceImageToUser(currentUserModel.getUserId());
        storageRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    currentUserModel.setAvatar(uri.toString());
                    updateToFireStore();
                });
            }
        });
    }

    // Update user details in Firestore
    void updateToFireStore() {
        FirebaseUtil.currentUserDetail().set(currentUserModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StyleableToast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công", R.style.successToast).show();
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                intent.putExtra("showProfileFragment", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                StyleableToast.makeText(EditProfileActivity.this, "Có lỗi xảy ra, vui lòng thử lại", R.style.errorToast).show();
            }
        });
    }
}
