package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        setupButtonClicks();
    }

    private void setupButtonClicks() {
        TextView loginLink = findViewById(R.id.loginLink);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(LoginActivity.class);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(SignupActivity.class);
            }
        });

    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(WelcomeActivity.this, activityClass);
        startActivity(intent);
    }
}