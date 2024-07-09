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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    Button btnSubmitForgotPass;
    EditText edtEmail;
    ImageButton btnGoBack;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnSubmitForgotPass = findViewById(R.id.btnSubmitForgotPass);
        auth = FirebaseAuth.getInstance();
        edtEmail= findViewById(R.id.edtEmail);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassActivity.this,LoginActivity.class));
            }
        });

        btnSubmitForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ForgotPassActivity.this,"Thành công",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPassActivity.this,LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ForgotPassActivity.this,"Thất bại",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Định dạng Emial không hợp lệ");
                }
                else{
                    edtEmail.setError("Vui lòng nhập Email");
                }
            }
        });
    }

}