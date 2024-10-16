package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.github.muddz.styleabletoast.StyleableToast;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText edtEmail, edtPass, edtConfigPass,edtUserName;
    private Button btnSignUp;
    private ImageButton btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtConfigPass = findViewById(R.id.edtConfigPass);
        edtUserName = findViewById(R.id.edtUserName);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoBack = findViewById(R.id.btnGoBack);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                String useName = edtUserName.getText().toString().trim();
                String configPass = edtConfigPass.getText().toString().trim();

                if (user.isEmpty()) {
                    StyleableToast.makeText(SignupActivity.this, "Vui lòng nhập Email", R.style.errorToast).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    StyleableToast.makeText(SignupActivity.this, "Định dạng Email không chính xác", R.style.errorToast).show();
                } else if (pass.isEmpty()) {
                    StyleableToast.makeText(SignupActivity.this, "Vui lòng nhập mật khẩu", R.style.errorToast).show();
                } else if (useName.isEmpty()) {
                    StyleableToast.makeText(SignupActivity.this, "Vui lòng nhập tên của bạn ", R.style.errorToast).show();

                } else if (!pass.equals(configPass)) {
                    StyleableToast.makeText(SignupActivity.this, "Nhập lại mật khẩu không chính xác", R.style.errorToast).show();
                }
                else {
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveUserInfoToFireStore(user,useName);
                                StyleableToast.makeText(SignupActivity.this, "Đăng ký tài khoản thành công", R.style.successToast).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            } else {
                                StyleableToast.makeText(SignupActivity.this, "Đăng ký tài khoản thất bại, vui lòng thử lại", R.style.errorToast).show();
                            }
                        }
                    });
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, WelcomeActivity.class));
            }
        });
    }

    private void saveUserInfoToFireStore(String email, String userName) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("userId", FirebaseUtil.currentUserUid());
        user.put("created_at", System.currentTimeMillis());
        user.put("user_name",userName);
        user.put("avatar", "https://res.cloudinary.com/dsbebvfff/image/upload/v1720890023/user-profile-icon-free-vector_xcepte.jpg");
        user.put("status","online");
        user.put("bio","null");
        user.put("role","2");


        FirebaseUtil.currentUserDetail().set(user);
    }
}
