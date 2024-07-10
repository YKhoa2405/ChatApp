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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.github.muddz.styleabletoast.StyleableToast;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText edtEmail,edtPass,edtConfigPass;
    private Button btnSignUp;
    private ImageButton btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        edtEmail  = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        edtConfigPass  = findViewById(R.id.edtConfigPass);
        btnSignUp  = findViewById(R.id.btnSignUp);
        btnGoBack  =findViewById(R.id.btnGoBack);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                String configPass = edtConfigPass.getText().toString().trim();

                if(user.isEmpty()){
                    StyleableToast.makeText(SignupActivity.this, "Vui lòng nhập Email", R.style.errorToast).show();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    StyleableToast.makeText(SignupActivity.this, "Định dạng Email không chính xác", R.style.errorToast).show();
                }
                if(pass.isEmpty()){
                    StyleableToast.makeText(SignupActivity.this, "Vui lòng nhập mật khẩu", R.style.errorToast).show();
                }
                else if (!pass.equals(configPass)) {
                    StyleableToast.makeText(SignupActivity.this, "Nhập lại mật khẩu không chính xác", R.style.errorToast).show();
                }
                else {
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isComplete()){
                                StyleableToast.makeText(SignupActivity.this, "Đăng ký tài khoản thành công", R.style.successToast).show();
                                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                            }else{
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
                startActivity(new Intent(SignupActivity.this,WelcomeActivity.class));
            }
        });
    }
}