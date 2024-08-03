package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private EditText edtEmail,edtPass;
    private Button btnLogin;
    private ImageButton loginGoogle,btnGoBack;
    private TextView forgotPass;
    private SearchUserActivity currentUserModel;
    private RadioGroup radioGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoBack = findViewById(R.id.btnGoBack);
        forgotPass = findViewById(R.id.forgotPass);
        loginGoogle = findViewById(R.id.loginGoogle);
        radioGroup = findViewById(R.id.radioGroup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                RadioButton selectedRoleButton = findViewById(radioGroup.getCheckedRadioButtonId());
                String selectedRole = selectedRoleButton.getText().toString();

                if (email.isEmpty()) {
                    StyleableToast.makeText(LoginActivity.this, "Vui lòng nhập Email", R.style.errorToast).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    StyleableToast.makeText(LoginActivity.this, "Định dạng Email không chính xác", R.style.errorToast).show();
                } else if (pass.isEmpty()) {
                    StyleableToast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", R.style.errorToast).show();
                } else {
                    auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            // Check the user's role in Firestore
                            FirebaseUtil.currentUserDetail().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String role = document.getString("role");
                                            if (selectedRole.equals(role)) {
                                                // Roles match, allow login
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            } else {
                                                // Roles do not match, show error message
                                                StyleableToast.makeText(LoginActivity.this, "Bạn không có quyền truy cập", R.style.errorToast).show();
                                            }
                                        } else {
                                            StyleableToast.makeText(LoginActivity.this, "Người dùng không tồn tại", R.style.errorToast).show();
                                        }
                                    } else {
                                        StyleableToast.makeText(LoginActivity.this, "Lỗi khi kiểm tra vai trò người dùng", R.style.errorToast).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            StyleableToast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác", R.style.errorToast).show();
                        }
                    });
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
                finish();
            }
        });

//        Login google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("MainActivity", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        saveUserInfoFireStore(user);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));

                    } else {
                        StyleableToast.makeText(LoginActivity.this, "Đăng nhập thất bại", R.style.errorToast).show();
                    }
                });
    }

    private void saveUserInfoFireStore(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            String avatarUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            String userName = user.getDisplayName();
            RadioButton selectTag = findViewById(radioGroup.getCheckedRadioButtonId());

            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", FirebaseUtil.currentUserUid());
            userData.put("email", email);
            userData.put("created_at", System.currentTimeMillis());
            userData.put("avatar",avatarUrl);
            userData.put("user_name",userName);
            userData.put("status","online");
            userData.put("bio","null");
            userData.put("role",selectTag);

            FirebaseUtil.currentUserDetail().set(userData);
        }
    }
}