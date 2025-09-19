package com.PROJECT.kitchenkart.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PROJECT.kitchenkart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

/** @noinspection ALL*/
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private MaterialButton resetPasswordButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Make sure you have this layout file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText); // ID from your layout XML
        resetPasswordButton = findViewById(R.id.resetPasswordButton); // ID from your layout XML
        progressBar = findViewById(R.id.progressBar); // ID from your layout XML

        // Set up click listener for the reset password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your registered email address!");
            emailEditText.requestFocus();
            return;
        }

        // You might want to add a more robust email validation here
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Password reset email sent! Check your inbox.",
                                    Toast.LENGTH_LONG).show();
                            // Optionally, navigate back to LoginActivity after successful reset email
                            finish();
                        } else {
                            // Handle cases like email not found, no internet, etc.
                            // task.getException().getMessage() will give more details
                            String errorMessage = "Failed to send reset email. Please try again.";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}