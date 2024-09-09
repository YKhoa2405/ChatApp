package com.example.chatapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminCreateUserActivity extends AppCompatActivity {

    private EditText edtUserName, edtEmail, edtPass;
    private MaterialButton btnSave;
    private ImageButton btnGoBack;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_user);

        // Set up padding for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        edtUserName = findViewById(R.id.edtUserName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnSave = findViewById(R.id.btnSave);
        btnGoBack = findViewById(R.id.btnGoBack);

        // Set up button click listeners
        btnSave.setOnClickListener(v -> saveUser());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void saveUser() {
        // Get data from EditTexts
        String userName = edtUserName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        // Validate input
        if (userName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên của bạn", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng Email không chính xác", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }else {
            // Create user with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Save user info to Firestore
                            saveUserInfoToFireStore(email, userName);
                        } else {
                            Toast.makeText(this, "Đăng ký người dùng thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveUserInfoToFireStore(String email, String userName) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("userId", FirebaseUtil.currentUserUid()); // Replace with the actual method to get current user ID
        user.put("created_at", System.currentTimeMillis());
        user.put("user_name", userName);
        user.put("avatar", "https://res.cloudinary.com/dsbebvfff/image/upload/v1720890023/user-profile-icon-free-vector_xcepte.jpg");
        user.put("status", "offline");
        user.put("bio", "null");
        user.put("role", "2");

        db.collection("users") // Replace with your Firestore collection name
                .document(FirebaseUtil.currentUserUid()) // Use the user's ID as the document ID
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Người dùng đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi thêm người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
