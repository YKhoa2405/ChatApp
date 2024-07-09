package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
                    edtEmail.setError("Email không được để trống");
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    edtEmail.setError("Định dạng Email không hợp lệ");
                }
                if(pass.isEmpty()){
                    edtPass.setError("Mật khẩu không được để trống");
                }
                else if (!pass.equals(configPass)) {
                    edtConfigPass.setError("Mật khẩu nhập lại không khớp");
                }
                else {
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isComplete()){
                                Toast.makeText(SignupActivity.this, "Đăng ký tài khoản thành công",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                            }else{
                                Toast.makeText(SignupActivity.this, "Đăng ký thất bại, vui lòng thử lại",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,MainActivity.class));
            }
        });
    }
}