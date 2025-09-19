package com.PROJECT.kitchenkart; // Ensure this matches your actual project package name

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Import Button
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.PROJECT.kitchenkart.Activities.LoginActivity; // Import LoginActivity
import com.PROJECT.kitchenkart.Activities.RegisterActivity;


/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {

    private Button btnLogin;    // Declare the Login Button
    private Button btnRegister; // Declare the Register Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Ensure you have activity_main.xml

        // Initialize the Login Button by finding its ID from the layout
        btnLogin = findViewById(R.id.btnLogin);
        // Initialize the Register Button by finding its ID from the layout
        btnRegister = findViewById(R.id.btnRegister); // Assuming ID is btnRegister in activity_main.xml

        // Set an OnClickListener for the Login Button
        btnLogin.setOnClickListener(v -> {
            // Create an Intent to start the LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent); // Start the LoginActivity
        });

        // Set an OnClickListener for the Register Button
        btnRegister.setOnClickListener(v -> {
            // Create an Intent to start the RegistrationActivity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent); // Start the RegistrationActivity
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}