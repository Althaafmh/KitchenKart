package com.PROJECT.kitchenkart.Activities; // Ensure this package name is correct for your project

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PROJECT.kitchenkart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

/** @noinspection ALL*/
public class LoginActivity extends AppCompatActivity { // Ensure class is public

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private TextView tvForgotPasswordLink; // Add this for the Forgot Password link

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure you have activity_login.xml

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPasswordLink = findViewById(R.id.tvForgotPasswordLink); // Initialize the new TextView

        // Set up login button click listener
        btnLogin.setOnClickListener(v -> loginUser());

        // Set up register link click listener
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set up forgot password link click listener
        tvForgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_required));
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful, now determine user type
                            String userId = mAuth.getCurrentUser().getUid();
                            DocumentReference userRef = db.collection("users").document(userId);

                            userRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String userType = documentSnapshot.getString("userType");
                                    Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();

                                    Intent intent;
                                    if ("seller".equals(userType)) {
                                        intent = new Intent(LoginActivity.this, SellerDashboardActivity.class);
                                    } else { // Default to buyer
                                        intent = new Intent(LoginActivity.this, BuyerDashboardActivity.class);
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish(); // Close LoginActivity
                                } else {
                                    // User document not found in Firestore (should not happen if registration worked)
                                    Toast.makeText(LoginActivity.this, "User data not found. Please re-register or contact support.", Toast.LENGTH_LONG).show();
                                    mAuth.signOut(); // Log out the user as their data is incomplete
                                }
                            }).addOnFailureListener(e -> {
                                // Error fetching user type from Firestore
                                Toast.makeText(LoginActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // Login failed (e.g., wrong password, email not found)
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                            Toast.makeText(LoginActivity.this, getString(R.string.error_authentication_failed) + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

